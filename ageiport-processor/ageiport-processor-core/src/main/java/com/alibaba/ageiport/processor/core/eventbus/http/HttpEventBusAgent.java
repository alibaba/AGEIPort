package com.alibaba.ageiport.processor.core.eventbus.http;

import com.alibaba.ageiport.common.concurrent.ThreadPoolUtil;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.dispatcher.http.HttpDispatchResponse;
import com.alibaba.ageiport.processor.core.eventbus.local.async.AsyncEventBus;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import io.vertx.core.AbstractVerticle;

import java.util.EventListener;
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
        vertx.createHttpServer().requestHandler(request -> {
            if (HttpEventBus.URL.equals(request.uri())) {
                request.body(event -> {
                    if (event.succeeded()) {
                        String requestJson = event.result().toString();
                        TaskStageEvent stageEvent = JsonUtil.toObject(requestJson, TaskStageEvent.class);
                        eventBus.post(stageEvent);
                        request.response().end("success");
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


    public void register(EventListener listener) {
        eventBus.register(listener);
    }

    public void unregister(EventListener listener) {
        eventBus.unregister(listener);
    }

}