package com.alibaba.ageiport.processor.core.eventbus.http;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.dispatcher.http.HttpDispatchResponse;
import com.alibaba.ageiport.processor.core.dispatcher.http.HttpDispatcher;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;

import java.util.EventListener;
import java.util.EventObject;

/**
 * 本地事件队列
 *
 * @author lingyi
 */

public class HttpEventBus implements EventBus {

    public static final String URL = "/event";

    public static final String HEALTH_URL = "/event/ping";

    private static final Logger logger = LoggerFactory.getLogger(HttpDispatcher.class);

    private AgeiPort ageiPort;

    HttpClient httpClient;

    HttpEventBusAgent agent;

    HttpEventBusOptions options;

    public HttpEventBus(AgeiPort ageiPort, HttpEventBusOptions options) {
        this.ageiPort = ageiPort;
        this.options = options;
        this.agent = new HttpEventBusAgent(ageiPort, options);
        Vertx vertx = ageiPort.getBean(Vertx.class, s -> Vertx.vertx(), ageiPort);
        vertx.deployVerticle(agent);
        this.httpClient = vertx.createHttpClient();

    }

    @Override
    public void register(EventListener listener) {
        this.agent.register(listener);
    }

    @Override
    public void unregister(EventListener listener) {
        this.agent.unregister(listener);
    }

    @Override
    public void post(EventObject eventObject) {
        TaskStageEvent taskStageEvent = (TaskStageEvent) eventObject;
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(taskStageEvent.getMainTaskId());
        RequestOptions requestOptions = new RequestOptions();

        String host = mainTask.getHost();
        requestOptions.setHost(host)
                .setPort(this.options.getPort())
                .setMethod(HttpMethod.POST)
                .setURI(URL)
                .setTimeout(3000);

        String message = StringUtils.format("main:{}, sub:{}, ip:{}, stage:{}", taskStageEvent.getMainTaskId(), taskStageEvent.getSubTaskId(), host, taskStageEvent.getStage());

        this.httpClient.request(requestOptions, e -> {
            if (e.succeeded()) {
                HttpClientRequest httpClientRequest = e.result();
                String body = JsonUtil.toJsonString(eventObject);
                logger.info("send:{}", body);
                httpClientRequest.send(body, asyncResult -> {
                    if (asyncResult.succeeded() && asyncResult.result().statusCode() == 200) {
                        HttpClientResponse response = asyncResult.result();
                        response.bodyHandler(bodyResult -> {
                            String resultJson = bodyResult.toString();
                            HttpDispatchResponse dispatchResponse = JsonUtil.toObject(resultJson, HttpDispatchResponse.class);
                            if (dispatchResponse != null && Boolean.TRUE.equals(dispatchResponse.getSuccess())) {
                                logger.debug("post event success, {}", message);
                            } else {
                                logger.error("post event failed, message:{}, resultJson:{}", message, resultJson);
                            }
                        });
                    } else {
                        logger.error("post response failed, {}", message);
                    }
                });
            } else {
                logger.error("post request failed, {}", message);
            }
        });
    }

}
