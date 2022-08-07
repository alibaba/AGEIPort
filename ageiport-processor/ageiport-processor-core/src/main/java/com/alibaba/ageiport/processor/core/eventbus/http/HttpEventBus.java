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

    private static final Logger logger = LoggerFactory.getLogger(HttpDispatcher.class);

    private AgeiPort ageiPort;

    HttpClient httpClient;

    HttpEventBusAgent agent;

    HttpEventBusOptions options;

    public HttpEventBus(AgeiPort ageiPort, HttpEventBusOptions options) {
        this.ageiPort = ageiPort;
        this.options = options;
        this.agent = new HttpEventBusAgent(ageiPort, options);
        Vertx vertx = Vertx.vertx();
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

        String message = StringUtils.format("main:{}, sub:{}, ip{}, stage:{}",
                taskStageEvent.getMainTaskId(), taskStageEvent.getSubTaskId(), host, taskStageEvent.getStage());

        this.httpClient.request(requestOptions, event -> {
            if (event.succeeded()) {
                HttpClientRequest httpClientRequest = event.result();
                String body = JsonUtil.toJsonString(eventObject);
                httpClientRequest.send(body, event1 -> {
                    if (event1.succeeded()) {
                        HttpClientResponse response = event1.result();
                        response.bodyHandler(event11 -> {
                            String resultJson = event11.toString();
                            HttpDispatchResponse dispatchResponse = JsonUtil.toObject(resultJson, HttpDispatchResponse.class);
                            if (dispatchResponse.getSuccess()) {
                                logger.debug("post event success, {}", message);
                            } else {
                                logger.error("post event failed, {}", message);
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
