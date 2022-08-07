package com.alibaba.ageiport.processor.core.api.http;

import com.alibaba.ageiport.common.function.Handler;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.api.ApiServer;
import com.alibaba.ageiport.processor.core.spi.api.model.ExecuteMainTaskRequest;
import com.alibaba.ageiport.processor.core.spi.api.model.ExecuteMainTaskResponse;
import com.alibaba.ageiport.processor.core.spi.api.model.GetMainTaskProgressRequest;
import com.alibaba.ageiport.processor.core.spi.api.model.GetMainTaskProgressResponse;
import com.alibaba.ageiport.processor.core.spi.service.GetTaskProgressParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteResult;
import com.alibaba.ageiport.processor.core.spi.service.TaskProgressResult;
import com.alibaba.ageiport.processor.core.spi.task.stage.CommonStage;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;

/**
 * @author lingyi
 */
public class HttpApiServer implements ApiServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpApiServer.class);

    public static final String TASK_PROGRESS_URL = "/TaskProgress";

    public static final String TASK_EXECUTE_URL = "/TaskExecute";

    private AgeiPort ageiPort;

    private HttpClient httpClient;

    private HttpApiServerOptions options;

    public HttpApiServer(AgeiPort ageiPort, HttpApiServerOptions options) {
        this.ageiPort = ageiPort;
        this.options = options;
        Vertx vertx = ageiPort.getBean(Vertx.class, s -> Vertx.vertx(), ageiPort);
        this.httpClient = vertx.createHttpClient();
        HttpApiVerticle verticle = new HttpApiVerticle(ageiPort, options, this);
        vertx.deployVerticle(verticle);
    }

    @Override
    public void executeTask(ExecuteMainTaskRequest request, Handler<ExecuteMainTaskResponse> handler) {
        TaskExecuteParam taskExecuteParam = BeanUtils.cloneProp(request, TaskExecuteParam.class);
        TaskExecuteResult executeResult = ageiPort.getTaskService().executeTask(taskExecuteParam);
        ExecuteMainTaskResponse response = new ExecuteMainTaskResponse();
        response.setSuccess(executeResult.getSuccess());
        response.setMessage(executeResult.getErrorMessage());
        response.setMainTaskId(executeResult.getMainTaskId());
        handler.handle(response);
    }

    @Override
    public void getTaskProgress(GetMainTaskProgressRequest request, Handler<GetMainTaskProgressResponse> handler) {
        String mainTaskId = request.getMainTaskId();
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(mainTaskId);

        if (canUserCurrentNode(mainTask)) {
            GetTaskProgressParam taskProgressRequest = new GetTaskProgressParam(mainTaskId);
            TaskProgressResult taskProgressResult = ageiPort.getTaskService().getTaskProgress(taskProgressRequest);
            GetMainTaskProgressResponse response = BeanUtils.cloneProp(taskProgressResult, GetMainTaskProgressResponse.class);
            response.setSuccess(true);
            handler.handle(response);
        } else {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.setHost(mainTask.getHost())
                    .setPort(this.options.getPort())
                    .setMethod(HttpMethod.POST)
                    .setURI(TASK_PROGRESS_URL)
                    .setTimeout(3000);
            this.httpClient.request(requestOptions, event1 -> {
                String message = StringUtils.format("getTaskProgress response failed main:{}, host{}", mainTaskId, mainTask.getHost());
                if (event1.succeeded()) {
                    HttpClientRequest httpClientRequest = event1.result();
                    httpClientRequest.send(JsonUtil.toJsonString(request), event11 -> {
                        if (event11.succeeded()) {
                            String jsonString = event11.result().body().toString();
                            handler.handle(JsonUtil.toObject(jsonString, GetMainTaskProgressResponse.class));
                        } else {
                            logger.error(message, event11.cause());
                            GetMainTaskProgressResponse response = new GetMainTaskProgressResponse();
                            response.setSuccess(false);
                            response.setMessage(message);
                            handler.handle(response);
                        }
                    });
                } else {
                    logger.error(message, event1.cause());
                    GetMainTaskProgressResponse response = new GetMainTaskProgressResponse();
                    response.setSuccess(false);
                    response.setMessage(message);
                    handler.handle(response);
                }
            });
        }
    }

    private boolean canUserCurrentNode(MainTask mainTask) {
        return ageiPort.getClusterManager().getLocalNode().getIp().equals(mainTask.getHost())
                || mainTask.getStatus().equals(CommonStage.ERROR.getCode())
                || mainTask.getStatus().equals(CommonStage.FINISHED.getCode());
    }

}
