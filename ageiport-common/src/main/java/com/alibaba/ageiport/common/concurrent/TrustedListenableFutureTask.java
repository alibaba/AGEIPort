package com.alibaba.ageiport.common.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
/**
 * A {@link RunnableFuture} that also implements the {@link ListenableFuture} interface.
 *
 * <p>This should be used in preference to {@link ListenableFutureTask} when possible for
 * performance reasons.
 */
public class TrustedListenableFutureTask<V> extends FluentFuture.TrustedFuture<V>
        implements RunnableFuture<V> {

    static <V> TrustedListenableFutureTask<V> create(Callable<V> callable) {
        return new TrustedListenableFutureTask<V>(callable);
    }

    /**
     * Creates a {@code ListenableFutureTask} that will upon running, execute the given {@code
     * Runnable}, and arrange that {@code get} will return the given result on successful completion.
     *
     * @param runnable the runnable task
     * @param result   the result to return on successful completion. If you don't need a particular
     *                 result, consider using constructions of the form: {@code ListenableFuture<?> f =
     *                 ListenableFutureTask.create(runnable, null)}
     */
    static <V> TrustedListenableFutureTask<V> create(Runnable runnable, V result) {
        return new TrustedListenableFutureTask<V>(Executors.callable(runnable, result));
    }

    /*
     * In certain circumstances, this field might theoretically not be visible to an afterDone() call
     * triggered by cancel(). For details, see the comments on the fields of TimeoutFuture.
     *
     * <p>{@code volatile} is required for j2objc transpiling:
     * https://developers.google.com/j2objc/guides/j2objc-memory-model#atomicity
     */
    private volatile InterruptibleTask<?> task;

    TrustedListenableFutureTask(Callable<V> callable) {
        this.task = new TrustedFutureInterruptibleTask(callable);
    }

    @Override
    public void run() {
        InterruptibleTask localTask = task;
        if (localTask != null) {
            localTask.run();
        }
        /*
         * In the Async case, we may have called setFuture(pendingFuture), in which case afterDone()
         * won't have been called yet.
         */
        this.task = null;
    }

    @Override
    protected void afterDone() {
        super.afterDone();

        if (wasInterrupted()) {
            InterruptibleTask localTask = task;
            if (localTask != null) {
                localTask.interruptTask();
            }
        }

        this.task = null;
    }

    @Override
    protected String pendingToString() {
        InterruptibleTask localTask = task;
        if (localTask != null) {
            return "task=[" + localTask + "]";
        }
        return super.pendingToString();
    }

    private final class TrustedFutureInterruptibleTask extends InterruptibleTask<V> {
        private final Callable<V> callable;

        TrustedFutureInterruptibleTask(Callable<V> callable) {
            this.callable = callable;
        }

        @Override
        final boolean isDone() {
            return TrustedListenableFutureTask.this.isDone();
        }

        @Override
        V runInterruptibly() throws Exception {
            return callable.call();
        }

        @Override
        void afterRanInterruptibly(V result, Throwable error) {
            if (error == null) {
                TrustedListenableFutureTask.this.set(result);
            } else {
                setException(error);
            }
        }

        @Override
        String toPendingString() {
            return callable.toString();
        }
    }

}