package com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortImpl;
import com.alibaba.ageiport.processor.core.dispatcher.http.HttpDispatchResponse;
import com.alibaba.ageiport.processor.core.eventbus.http.HttpEventBus;
import com.alibaba.ageiport.processor.core.eventbus.local.LocalEventBus;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteResult;
import com.alibaba.ageiport.processor.core.spi.service.TaskService;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka.model.PingParam;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;

@RestController
public class TaskController {
    static Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private AgeiPort ageiPort;

    @PostMapping("/task")
    public TaskExecuteResult run(@RequestBody TaskExecuteParam request) {
        TaskService taskService = ageiPort.getTaskService();
        return taskService.executeTask(request);
    }


    @GetMapping("/ping")
    public String ping() {
        return System.currentTimeMillis() + "";
    }

    @GetMapping("/ping2")
    public String ping2(@RequestParam("host") String host) {
        final Vertx vertx = ageiPort.getBean(Vertx.class, s -> Vertx.vertx(), ageiPort);
        final HttpClient httpClient = vertx.createHttpClient();
        RequestOptions requestOptions = new RequestOptions();

        requestOptions.setHost(host)
                .setPort(9742)
                .setMethod(HttpMethod.GET)
                .setURI("/event/ping")
                .setTimeout(1000);

        httpClient.request(requestOptions, new Handler<AsyncResult<HttpClientRequest>>() {
            @Override
            public void handle(AsyncResult<HttpClientRequest> event) {
                if (event.succeeded()) {
                    logger.info("request success, ", event.cause());
                    HttpClientRequest clientRequest = event.result();
                    clientRequest.send("some", new Handler<AsyncResult<HttpClientResponse>>() {
                        @Override
                        public void handle(AsyncResult<HttpClientResponse> event) {
                            if (event.succeeded()) {
                                final int statusCode = event.result().statusCode();

                                logger.info("send success,statusCode:{} ", event.cause(), statusCode);
                                event.result().bodyHandler(new Handler<Buffer>() {
                                    @Override
                                    public void handle(Buffer event) {
                                        final String string = event.toString();
                                        logger.info("send success, receive:{}", string);
                                    }
                                });
                            } else {
                                logger.error("send failed, ", event.cause());
                            }
                        }
                    });

                } else {
                    logger.error("request failed, ", event.cause());
                }
            }
        });

        return "ok";
    }

    @PostMapping("/ping3")
    public String ping3(@RequestBody PingParam pingParam) {
        final Vertx vertx = ageiPort.getBean(Vertx.class, s -> Vertx.vertx(), ageiPort);
        final HttpClient httpClient = vertx.createHttpClient();
        RequestOptions requestOptions = new RequestOptions();

        requestOptions.setHost(pingParam.getHost())
                .setPort(9742)
                .setMethod(HttpMethod.POST)
                .setURI("/event")
                .setTimeout(1000);

        String message = JsonUtil.toJsonString(pingParam);

        httpClient.request(requestOptions, e -> {
            if (e.succeeded()) {
                HttpClientRequest httpClientRequest = e.result();
                String body = JsonUtil.toJsonString(pingParam);
                logger.info("http event bus send:{}", body);
                httpClientRequest.send(body, asyncResult -> {
                    if (asyncResult.succeeded()) {
                        if (asyncResult.result().statusCode() == 200) {
                            HttpClientResponse response = asyncResult.result();
                            response.bodyHandler(bodyResult -> {
                                String resultJson = bodyResult.toString();
                                HttpDispatchResponse dispatchResponse = JsonUtil.toObject(resultJson, HttpDispatchResponse.class);
                                if (dispatchResponse != null && Boolean.TRUE.equals(dispatchResponse.getSuccess())) {
                                    logger.info("post event success, {}", message);
                                } else {
                                    logger.error("post event failed, message:{}, resultJson:{}", message, resultJson);
                                }
                            });
                        } else {
                            logger.error("post response failed, send error, {}, statusCode:{}", message, asyncResult.result().statusCode());
                        }
                    } else {
                        logger.error("post response failed, send failed, {}", message, asyncResult.cause());
                    }
                });
            } else {
                logger.error("post request failed, {}", message, e.cause());
            }
        });

        return "ok";
    }

}
