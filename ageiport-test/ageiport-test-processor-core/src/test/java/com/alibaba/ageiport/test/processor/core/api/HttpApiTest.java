package com.alibaba.ageiport.test.processor.core.api;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.api.http.HttpApiServer;
import com.alibaba.ageiport.processor.core.api.http.HttpApiServerOptions;
import com.alibaba.ageiport.processor.core.spi.api.model.ExecuteMainTaskRequest;
import com.alibaba.ageiport.processor.core.spi.api.model.ExecuteMainTaskResponse;
import com.alibaba.ageiport.processor.core.spi.api.model.GetMainTaskProgressRequest;
import com.alibaba.ageiport.processor.core.spi.api.model.GetMainTaskProgressResponse;
import com.alibaba.ageiport.test.processor.core.TestHelper;
import com.alibaba.ageiport.test.processor.core.exporter.StandaloneExportProcessor;
import com.alibaba.ageiport.test.processor.core.model.Query;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpApiTest {

    @Test
    @SneakyThrows
    public void test() {
        AgeiPortOptions options = new AgeiPortOptions();
        AgeiPort ageiPort = AgeiPort.ageiPort(options);

        Query query = new Query();
        query.setTotalCount(100);
        ExecuteMainTaskRequest executeMainTaskRequest = new ExecuteMainTaskRequest();
        executeMainTaskRequest.setTaskSpecificationCode(StandaloneExportProcessor.class.getSimpleName());
        executeMainTaskRequest.setBizUserId("userId");
        executeMainTaskRequest.setBizQuery(JsonUtil.toJsonString(query));

        HttpApiServerOptions serverOptions = (HttpApiServerOptions) ageiPort.getOptions().getApiServerOptions();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setHost("localhost")
                .setURI(HttpApiServer.TASK_EXECUTE_URL)
                .setPort(serverOptions.getPort())
                .setMethod(HttpMethod.POST);

        CompletableFuture<ExecuteMainTaskResponse> executeFuture = new CompletableFuture<>();
        Vertx.vertx().createHttpClient().request(requestOptions, event -> {
            HttpClientRequest clientRequest = event.result();
            String json = JsonUtil.toJsonString(executeMainTaskRequest);
            clientRequest.send(json, event1 -> {
                HttpClientResponse result = event1.result();
                result.body(event2 -> {
                    String jsonString = event2.result().toString();
                    ExecuteMainTaskResponse response = JsonUtil.toObject(jsonString, ExecuteMainTaskResponse.class);
                    executeFuture.complete(response);
                });
            });
        });
        ExecuteMainTaskResponse executeMainTaskResponse = executeFuture.get(3, TimeUnit.SECONDS);
        log.info("future, executeMainTaskResponse:{}", executeMainTaskResponse);


        RequestOptions requestOptions2 = new RequestOptions();
        requestOptions2.setHost("localhost")
                .setURI(HttpApiServer.TASK_PROGRESS_URL)
                .setPort(serverOptions.getPort())
                .setMethod(HttpMethod.POST);
        GetMainTaskProgressRequest getMainTaskProgressRequest = new GetMainTaskProgressRequest();
        getMainTaskProgressRequest.setMainTaskId(executeMainTaskResponse.getMainTaskId());
        CompletableFuture<GetMainTaskProgressResponse> progressFuture = new CompletableFuture<>();
        Vertx.vertx().createHttpClient().request(requestOptions2, event -> {
            HttpClientRequest clientRequest = event.result();
            String json = JsonUtil.toJsonString(getMainTaskProgressRequest);
            clientRequest.send(json, event1 -> {
                HttpClientResponse result = event1.result();
                result.body(event22 -> {
                    final String jsonString = event22.result().toString();
                    GetMainTaskProgressResponse response = JsonUtil.toObject(jsonString, GetMainTaskProgressResponse.class);
                    progressFuture.complete(response);
                });
            });
        });
        GetMainTaskProgressResponse getMainTaskInstanceResponse = progressFuture.get(3, TimeUnit.SECONDS);
        log.info("future, getMainTaskInstanceResponse:{}", getMainTaskInstanceResponse);

        TestHelper testHelper = new TestHelper(ageiPort);
        testHelper.assertWithFile(executeMainTaskResponse.getMainTaskId(), query.getTotalCount());
    }
}
