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
import com.alibaba.ageiport.processor.core.spi.api.model.*;
import com.alibaba.ageiport.processor.core.spi.service.*;
import com.alibaba.ageiport.processor.core.spi.task.stage.CommonStage;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
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

    public static final String SYNC_EXTENSION_API_URL = "/SyncExecute";

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

        if (isTaskInFinal(mainTask)) {
            GetMainTaskProgressResponse response = new GetMainTaskProgressResponse();
            response.setSuccess(true);
            response.setMainTaskId(mainTask.getMainTaskId());
            response.setStatus(mainTask.getStatus());

            Stage stage = CommonStage.of(mainTask.getStatus());
            response.setStageCode(stage.getCode());
            response.setStageName(stage.getName());
            response.setErrorSubTaskCount(mainTask.getSubFailedCount());
            response.setTotalSubTaskCount(mainTask.getSubTotalCount());
            response.setFinishedSubTaskCount(mainTask.getSubFinishedCount());
            response.setPercent(1D);
            response.setIsFinished(stage == CommonStage.FINISHED);
            response.setIsError(stage == CommonStage.ERROR);
            handler.handle(response);
            return;
        }
        if (canUserCurrentNode(mainTask)) {
            TaskProgressParam taskProgressRequest = new TaskProgressParam(mainTaskId);
            TaskService taskService = ageiPort.getTaskService();
            TaskProgressResult taskProgressResult = taskService.getTaskProgress(taskProgressRequest);
            GetMainTaskProgressResponse response = BeanUtils.cloneProp(taskProgressResult, GetMainTaskProgressResponse.class);
            response.setSuccess(true);
            handler.handle(response);
            return;
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setHost(mainTask.getHost())
                .setPort(this.options.getPort())
                .setMethod(HttpMethod.POST)
                .setURI(TASK_PROGRESS_URL)
                .setTimeout(1000);

        this.httpClient.request(requestOptions, e -> {
            String message = StringUtils.format("getTaskProgress response failed main:{}, host{}", mainTaskId, mainTask.getHost());
            if (e.succeeded()) {
                HttpClientRequest httpClientRequest = e.result();
                String requestJson = JsonUtil.toJsonString(request);
                httpClientRequest.send(requestJson, asyncResult -> {
                    if (asyncResult.succeeded()) {
                        String jsonString = asyncResult.result().body().toString();
                        GetMainTaskProgressResponse response = JsonUtil.toObject(jsonString, GetMainTaskProgressResponse.class);
                        handler.handle(response);
                    } else {
                        logger.error(message, asyncResult.cause());
                        GetMainTaskProgressResponse response = new GetMainTaskProgressResponse();
                        response.setSuccess(false);
                        response.setMessage(message);
                        handler.handle(response);
                    }
                });
            } else {
                logger.error(message, e.cause());
                GetMainTaskProgressResponse response = new GetMainTaskProgressResponse();
                response.setSuccess(false);
                response.setMessage(message);
                handler.handle(response);
            }
        });

    }

    @Override
    public void executeSyncExtension(SyncExtensionApiRequest request, Handler<SyncExtensionApiResponse> handler) {
        SyncExtensionApiParam syncExtensionApiParam = BeanUtils.cloneProp(request, SyncExtensionApiParam.class);
        SyncExtensionApiResult executeResult = ageiPort.getTaskService().executeSyncExtension(syncExtensionApiParam);
        SyncExtensionApiResponse response = new SyncExtensionApiResponse();
        response.setSuccess(executeResult.getSuccess());
        response.setMessage(executeResult.getErrorMessage());
        handler.handle(response);
    }

    private boolean canUserCurrentNode(MainTask mainTask) {
        return ageiPort.getClusterManager().getLocalNode().getIp().equals(mainTask.getHost());
    }

    private boolean isTaskInFinal(MainTask mainTask) {
        return mainTask.getStatus().equals(CommonStage.ERROR.getCode())
                || mainTask.getStatus().equals(CommonStage.FINISHED.getCode());
    }
}
