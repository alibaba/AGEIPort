package com.alibaba.ageiport.processor.core.eventbus.local;


import com.alibaba.ageiport.common.concurrent.ThreadPoolUtil;
import com.alibaba.ageiport.processor.core.eventbus.local.async.AsyncEventBus;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;

import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * 本地事件队列
 *
 * @author lingyi
 */
public class LocalEventBus implements EventBus {

    private AsyncEventBus eventBus;

    public LocalEventBus(LocalEventBusOptions options) {
        ExecutorService executorService = ThreadPoolUtil.createExecutor(
                "eb-local",
                options.getCorePoolSize(),
                options.getMaxPoolSize(),
                new ArrayBlockingQueue<>(options.getQueueSize()));
        this.eventBus = new AsyncEventBus(executorService);
    }

    @Override
    public void register(EventListener listener) {
        eventBus.register(listener);
    }

    @Override
    public void unregister(EventListener listener) {
        eventBus.unregister(listener);
    }

    @Override
    public void post(EventObject event) {
        eventBus.post(event);
    }

}
