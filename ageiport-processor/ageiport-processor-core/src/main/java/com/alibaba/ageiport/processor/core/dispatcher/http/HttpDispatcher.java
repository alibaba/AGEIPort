package com.alibaba.ageiport.processor.core.dispatcher.http;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.executor.MainWorkerExecutor;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterManager;
import com.alibaba.ageiport.processor.core.spi.cluster.Node;
import com.alibaba.ageiport.processor.core.spi.dispatcher.Dispatcher;
import com.alibaba.ageiport.processor.core.spi.dispatcher.RootDispatcherContext;
import com.alibaba.ageiport.processor.core.spi.dispatcher.SubDispatcherContext;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskWorker;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskWorkerFactory;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;
import com.alibaba.ageiport.processor.core.task.monitor.ClearTask;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingyi
 */
@Getter
@Setter
public class HttpDispatcher implements Dispatcher {

    public static final String URL = "/subTasks";

    private static final Logger logger = LoggerFactory.getLogger(HttpDispatcher.class);

    private AgeiPort ageiPort;

    private HttpDispatcherOptions options;

    private Map<String, Node> failedNodeMap;

    private DispatchQueue dispatchQueue;

    private ClearTask clearTask;

    private int nodeIndex;


    HttpClient httpClient;


    public HttpDispatcher(AgeiPort ageiPort, HttpDispatcherOptions options) {
        this.ageiPort = ageiPort;
        this.options = options;
        this.failedNodeMap = new ConcurrentHashMap<>();
        this.dispatchQueue = new DispatchQueue();
        this.clearTask = new ClearTask("HttpDispatcher clear task");
        HttpDispatcherAgent agent = new HttpDispatcherAgent(ageiPort, this);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(agent);
        this.httpClient = vertx.createHttpClient();
        new Thread(() -> {
            while (true) {
                doDispatchInClient();
            }
        }).start();
    }

    private void doDispatchInClient() {
        SubDispatcherContext context = null;
        try {
            context = dispatchQueue.get();
            Node node = getNextNode();

            int count = 0;
            while (this.failedNodeMap.containsKey(node.getId())) {
                node = getNextNode();
                int okNodeCount = ageiPort.getClusterManager().getNodes().size() - this.failedNodeMap.size();
                if (count++ > okNodeCount) {
                    throw new RuntimeException("no ok node");
                }
            }
            dispatchToNode(context, node);
        } catch (Throwable e) {
            logger.error("doDispatchInClient failed, context:{}", context, e);
        }
    }

    private void dispatchToNode(SubDispatcherContext context, Node node) {
        HttpDispatchRequest request = new HttpDispatchRequest(context.getMainTaskId(), context.getSubTaskNos());
        RequestOptions options = new RequestOptions();
        options.setHost(node.getIp())
                .setPort(this.options.getPort())
                .setMethod(HttpMethod.POST)
                .setURI(URL)
                .setTimeout(3000);

        this.httpClient.request(options, event -> {
            if (event.succeeded()) {
                HttpClientRequest httpClientRequest = event.result();
                String body = JsonUtil.toJsonString(request);
                httpClientRequest.send(body, event1 -> {
                    if (event1.succeeded()) {
                        HttpClientResponse response = event1.result();
                        response.bodyHandler(event11 -> {
                            String resultJson = event11.toString();
                            HttpDispatchResponse dispatchResponse = JsonUtil.toObject(resultJson, HttpDispatchResponse.class);
                            if (dispatchResponse.getSuccess()) {
                                logger.info("dispatchToNode success, main:{}, ip{}, nos:{}, labels{}", context.getMainTaskId(), node.getIp(), context.getSubTaskNos(), context.getLabels());
                            } else {
                                logger.error("dispatchToNode server response failed, ", resultJson);
                                dispatchFailed(node, context);
                            }
                        });
                    } else {
                        logger.error("dispatchToNode handle response, ", event1.cause());
                        dispatchFailed(node, context);
                    }
                });
            } else {
                logger.error("dispatchToNode get request failed, ", event.cause());
                dispatchFailed(node, context);
            }
        });
    }

    private void dispatchFailed(Node node, SubDispatcherContext context) {
        logger.error("dispatchFailed, main:{}, ip:{}, nos:{}, labels:{}",
                context.getMainTaskId(), node.getIp(), context.getSubTaskNos(), context.getLabels());
        this.failedNodeMap.put(node.getId(), node);
        clearTask.addClearTask(node.getId(), options.getNodeFallbackMs(), () -> failedNodeMap.remove(node.getId()));
        dispatchQueue.add(context);
    }

    private Node getNextNode() {
        ClusterManager clusterManager = ageiPort.getClusterManager();
        List<Node> nodes = clusterManager.getNodes();
        int nodeCount = nodes.size();
        int index = nodeIndex % nodeCount;
        Node node = nodes.get(index);
        nodeIndex = (nodeIndex + 1) % nodeCount;
        return node;
    }

    @Override
    public void dispatchMainTaskPrepare(RootDispatcherContext context) {
        String mainTaskId = context.getMainTaskId();
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(mainTaskId);
        TaskSpecificationRegistry taskSpecificationRegistry = ageiPort.getSpecificationRegistry();
        TaskSpec taskSpec = taskSpecificationRegistry.get(mainTask.getCode());

        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        MainTaskWorkerFactory workerFactory = spiSelector.selectExtension(taskSpec.getExecuteType(), mainTask.getType(), mainTask.getCode(), MainTaskWorkerFactory.class);

        MainTaskWorker worker = workerFactory.create(ageiPort, mainTask);
        worker.isReduce(false);

        MainWorkerExecutor workerExecutor = ageiPort.getMainWorkerExecutor();
        workerExecutor.submit(worker);
    }

    @Override
    public void dispatchSubTasks(SubDispatcherContext context) {
        List<Integer> subTaskNos = context.getSubTaskNos();
        int nodeCount = ageiPort.getClusterManager().getNodes().size();
        List<List<Integer>> subTaskAvgByNodeCount = Lists.averageAssign(subTaskNos, nodeCount);

        for (List<Integer> nos : subTaskAvgByNodeCount) {
            SubDispatcherContext contextToDispatch = new SubDispatcherContext();
            contextToDispatch.setMainTaskId(context.getMainTaskId());
            contextToDispatch.setSubTaskNos(nos);
            contextToDispatch.setLabels(context.getLabels());

            dispatchQueue.add(contextToDispatch);
        }
    }

    @Override
    public void dispatchMainTaskReduce(RootDispatcherContext context) {
        String mainTaskId = context.getMainTaskId();
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(mainTaskId);
        TaskSpecificationRegistry taskSpecificationRegistry = ageiPort.getSpecificationRegistry();
        TaskSpec taskSpec = taskSpecificationRegistry.get(mainTask.getCode());

        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        MainTaskWorkerFactory workerFactory = spiSelector.selectExtension(taskSpec.getExecuteType(), taskSpec.getTaskType(), mainTask.getCode(), MainTaskWorkerFactory.class);

        MainTaskWorker worker = workerFactory.create(ageiPort, mainTask);
        worker.isReduce(true);

        MainWorkerExecutor workerExecutor = ageiPort.getMainWorkerExecutor();
        workerExecutor.submit(worker);
    }
}
