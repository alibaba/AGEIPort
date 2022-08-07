package com.alibaba.ageiport.processor.core.eventbus.local.async;

import java.util.concurrent.Executor;

public class AsyncEventBus extends EventBus {

    public AsyncEventBus(String identifier, Executor executor) {
        super(identifier, executor, Dispatcher.legacyAsync(), LoggingHandler.INSTANCE);
    }

    public AsyncEventBus(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler) {
        super("default", executor, Dispatcher.legacyAsync(), subscriberExceptionHandler);
    }

    public AsyncEventBus(Executor executor) {
        super("default", executor, Dispatcher.legacyAsync(), LoggingHandler.INSTANCE);
    }
}