package com.alibaba.ageiport.processor.core.dispatcher.http;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.TaskIdUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.executor.SubWorkerExecutor;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskWorker;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskWorkerFactory;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;
import io.vertx.core.AbstractVerticle;

import java.util.List;

/**
 * @author lingyi
 */
public class HttpDispatcherAgent extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(HttpDispatcher.class);

    private AgeiPort ageiPort;

    private HttpDispatcher dispatcher;
    private HttpDispatcherOptions options;

    public HttpDispatcherAgent(AgeiPort ageiPort, HttpDispatcher dispatcher) {
        this.ageiPort = ageiPort;
        this.dispatcher = dispatcher;
        this.options = dispatcher.getOptions();
    }

    private void dispatchSubTaskToLocal(String mainTaskId, List<Integer> subTaskNos) {
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(mainTaskId);
        TaskSpecificationRegistry taskSpecificationRegistry = ageiPort.getSpecificationRegistry();
        TaskSpec taskSpec = taskSpecificationRegistry.get(mainTask.getCode());

        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        SubTaskWorkerFactory workerFactory = spiSelector.selectExtension(taskSpec.getExecuteType(), taskSpec.getTaskType(), mainTask.getCode(), SubTaskWorkerFactory.class);

        for (Integer subTaskNo : subTaskNos) {
            String subTaskId = TaskIdUtil.genSubTaskId(mainTaskId, subTaskNo);
            SubTask subTask = ageiPort.getTaskServerClient().getSubTask(subTaskId);
            SubTaskWorker worker = workerFactory.create(ageiPort, subTask);
            SubWorkerExecutor workerExecutor = ageiPort.getSubWorkerExecutor();
            workerExecutor.submit(worker);
        }
    }

    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(request -> {
            if (HttpDispatcher.URL.equals(request.uri())) {
                request.body(event -> {
                    if (event.succeeded()) {
                        String requestJson = event.result().toString();
                        HttpDispatchRequest dispatchRequest = JsonUtil.toObject(requestJson, HttpDispatchRequest.class);
                        dispatchSubTaskToLocal(dispatchRequest.getMainTaskId(), dispatchRequest.getSubTaskNos());
                        HttpDispatchResponse dispatchResponse = new HttpDispatchResponse(true);
                        String responseJson = JsonUtil.toJsonString(dispatchResponse);
                        request.response().end(responseJson);
                    } else {
                        HttpDispatchResponse dispatchResponse = new HttpDispatchResponse(false);
                        String responseJson = JsonUtil.toJsonString(dispatchResponse);
                        request.response().end(responseJson);
                    }
                });
            } else {
                request.response().setStatusCode(404).end("");
            }
        }).listen(options.getPort());
    }
}