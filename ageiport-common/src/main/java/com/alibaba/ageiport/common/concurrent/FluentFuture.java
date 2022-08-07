package com.alibaba.ageiport.common.concurrent;

import java.util.concurrent.*;

public abstract class FluentFuture<V> extends AbstractFuture<V> {

    abstract static class TrustedFuture<V> extends FluentFuture<V>
            implements AbstractFuture.Trusted<V> {
        @Override
        public final V get() throws InterruptedException, ExecutionException {
            return super.get();
        }

        @Override
        public final V get(long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return super.get(timeout, unit);
        }

        @Override
        public final boolean isDone() {
            return super.isDone();
        }

        @Override
        public final boolean isCancelled() {
            return super.isCancelled();
        }

        @Override
        public final void addListener(Runnable listener, Executor executor) {
            super.addListener(listener, executor);
        }

        @Override
        public final boolean cancel(boolean mayInterruptIfRunning) {
            return super.cancel(mayInterruptIfRunning);
        }
    }

    FluentFuture() {
    }
}