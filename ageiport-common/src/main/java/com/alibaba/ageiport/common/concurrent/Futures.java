package com.alibaba.ageiport.common.concurrent;

import java.util.concurrent.*;

public final class Futures {

    private Futures() {
    }

    public static <V> ListenableFuture<V> withTimeout(
            ListenableFuture<V> delegate,
            long time,
            TimeUnit unit,
            ScheduledExecutorService scheduledExecutor) {
        if (delegate.isDone()) {
            return delegate;
        }
        return TimeoutFuture.create(delegate, time, unit, scheduledExecutor);
    }

    public static <V> void addCallback(
            final ListenableFuture<V> future,
            final FutureCallback<? super V> callback,
            Executor executor) {
        future.addListener(new CallbackListener<V>(future, callback), executor);
    }
}
