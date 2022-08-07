package com.alibaba.ageiport.processor.core.executor;

import com.alibaba.ageiport.common.concurrent.Futures;
import com.alibaba.ageiport.common.concurrent.ListenableFuture;
import com.alibaba.ageiport.common.concurrent.ListeningExecutorService;
import com.alibaba.ageiport.common.concurrent.ThreadPoolUtil;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author lingyi
 */
public class DataMergeExecutor {

    public int timeoutMs;

    private ListeningExecutorService executor;

    public DataMergeExecutor(AgeiPortOptions.DataMergeExecutor options) {
        this.executor = ThreadPoolUtil.createListeningExecutor(
                options.getName(),
                options.getCorePoolSize(),
                options.getMaxPoolSize(),
                new LinkedBlockingQueue<>(options.getQueueSize())
        );
        this.timeoutMs = options.getTimeoutMs();
    }

    public ListenableFuture<?> submit(Runnable runnable) {
        ListenableFuture<?> future = executor.submit(runnable);
        ListenableFuture<?> timeoutFuture = Futures.withTimeout(
                future,
                timeoutMs,
                TimeUnit.MILLISECONDS,
                ThreadPoolUtil.scheduledExecutorService());
        return timeoutFuture;
    }

}
