package com.alibaba.ageiport.processor.core.api.http;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.api.ApiServer;
import com.alibaba.ageiport.processor.core.spi.api.model.ApiResponse;
import com.alibaba.ageiport.processor.core.spi.api.model.ExecuteMainTaskRequest;
import com.alibaba.ageiport.processor.core.spi.api.model.GetMainTaskProgressRequest;
import com.alibaba.ageiport.processor.core.spi.api.model.SyncExtensionApiRequest;
import com.alibaba.fastjson.JSON;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;

import java.util.HashMap;
import java.util.Map;

public class HttpApiVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(HttpApiVerticle.class);

    private AgeiPort ageiPort;
    private HttpApiServerOptions options;

    HttpClient httpClient;

    private ApiServer apiServer;

    public HttpApiVerticle(AgeiPort ageiPort, HttpApiServerOptions options, ApiServer apiServer) {
        this.ageiPort = ageiPort;
        this.options = options;
        this.apiServer = apiServer;
    }

    @Override
    public void start() {
        this.httpClient = vertx.createHttpClient();
        vertx.createHttpServer().requestHandler(request -> request.body(event -> {
            if (event.succeeded()) {
                String requestJson = event.result().toString();
                //fixme 验签
                switch (request.uri()) {
                    case HttpApiServer.TASK_PROGRESS_URL: {
                        GetMainTaskProgressRequest req = JsonUtil.toObject(requestJson, GetMainTaskProgressRequest.class);
                        apiServer.getTaskProgress(req, response -> {
                            String jsonResult = JsonUtil.toJsonString(response);
                            request.response().end(jsonResult);
                        });
                        break;
                    }
                    case HttpApiServer.TASK_EXECUTE_URL: {
                        ExecuteMainTaskRequest req = JsonUtil.toObject(requestJson, ExecuteMainTaskRequest.class);
                        apiServer.executeTask(req, response -> {
                            String jsonResult = JsonUtil.toJsonString(response);
                            request.response().end(jsonResult);
                        });
                        break;
                    }
                    case HttpApiServer.SYNC_EXTENSION_API_URL: {
                        SyncExtensionApiRequest req = JsonUtil.toObject(requestJson, SyncExtensionApiRequest.class);
                        apiServer.executeSyncExtension(req, response -> {
                            String jsonResult = JsonUtil.toJsonString(response);
                            request.response().end(jsonResult);
                        });
                        break;
                    }
                    case HttpApiServer.TASK_HEALTH: {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("timestamp", System.currentTimeMillis());
                        String jsonResult = JSON.toJSONString(result);
                        request.response().end(jsonResult);
                        break;
                    }
                    default: {
                        ApiResponse apiResponse = new ApiResponse();
                        apiResponse.setSuccess(false);
                        apiResponse.setMessage("not found:" + request.uri());
                        request.response().setStatusCode(404).end();
                        break;
                    }
                }
            } else {
                logger.error("request failed, uri:{}", request.uri(), event.cause());
                ApiResponse apiResponse = new ApiResponse();
                apiResponse.setSuccess(false);
                apiResponse.setMessage(event.cause().getMessage());
                String responseJson = JsonUtil.toJsonString(apiResponse);
                request.response().end(responseJson);
            }
        })).listen(options.getPort());
    }

}