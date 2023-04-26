package com.alibaba.ageiport.processor.core.eventbus.http;

import com.alibaba.ageiport.common.concurrent.ThreadPoolUtil;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.dispatcher.http.HttpDispatchResponse;
import com.alibaba.ageiport.processor.core.eventbus.local.async.AsyncEventBus;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.fastjson.JSON;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author lingyi
 */
public class HttpEventBusAgent extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(HttpEventBusAgent.class);

    private AgeiPort ageiPort;

    private HttpEventBusOptions options;

    private ExecutorService executorService;

    private AsyncEventBus eventBus;

    public HttpEventBusAgent(AgeiPort ageiPort, HttpEventBusOptions options) {
        this.ageiPort = ageiPort;
        this.options = options;
        this.executorService = ThreadPoolUtil.createExecutor(
                "eb-agent",
                options.getEventHandleCorePoolSize(),
                options.getEventHandleMaxPoolSize(),
                new ArrayBlockingQueue<>(options.getEventHandleQueueSize())
        );
        this.eventBus = new AsyncEventBus(executorService);
    }

    @Override
    public void start() {
        logger.info("AGEIPort HttpEventBus Agent start");
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(serverRequest -> {
            if (HttpEventBus.URL.equals(serverRequest.uri())) {
                serverRequest.body(event -> {
                    if (event.succeeded()) {
                        String requestJson = event.result().toString();
                        logger.info("server receive:{}", requestJson);
                        try {
                            TaskStageEvent stageEvent = JsonUtil.toObject(requestJson, TaskStageEvent.class);
                            eventBus.post(stageEvent);
                            HttpDispatchResponse dispatchResponse = new HttpDispatchResponse(true);
                            String responseJson = JsonUtil.toJsonString(dispatchResponse);
                            serverRequest.response().setStatusCode(200).end(responseJson);
                        } catch (Throwable e) {
                            logger.error("consume request json failed, requestJson:{}", requestJson, e);
                            HttpDispatchResponse dispatchResponse = new HttpDispatchResponse(false);
                            String responseJson = JsonUtil.toJsonString(dispatchResponse);
                            serverRequest.response().setStatusCode(200).end(responseJson);
                        }
                    } else {
                        logger.error("consume request error, {}", event.cause());
                        HttpDispatchResponse dispatchResponse = new HttpDispatchResponse(false);
                        String responseJson = JsonUtil.toJsonString(dispatchResponse);
                        serverRequest.response().setStatusCode(200).end(responseJson);
                    }
                });
                return;
            }
            if (HttpEventBus.HEALTH_URL.equals(serverRequest.uri())) {
                logger.info("server uri:{}", serverRequest.uri());
                serverRequest.body(event -> {
                    logger.info("server event result:{}", event.succeeded());
                    if (event.succeeded()) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("timestamp", System.currentTimeMillis());
                        String chunk = JSON.toJSONString(result);
                        serverRequest.response().setStatusCode(200).end(chunk);
                    } else {
                        logger.error("consume request error, {}", event.cause());
                        HttpDispatchResponse dispatchResponse = new HttpDispatchResponse(false);
                        String responseJson = JsonUtil.toJsonString(dispatchResponse);
                        serverRequest.response().setStatusCode(200).end(responseJson);
                    }
                });
                return;
            }
            logger.error("not found, url:{}", serverRequest.uri());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("errorMessage", "404 not found");
            String chunk = JSON.toJSONString(result);
            serverRequest.response().setStatusCode(404).end(chunk);
        });
        httpServer.listen(options.getPort());
        logger.info("AGEIPort HttpEventBus Agent start finished");
    }


    public void register(EventListener listener) {
        eventBus.register(listener);
    }

    public void unregister(EventListener listener) {
        eventBus.unregister(listener);
    }

}