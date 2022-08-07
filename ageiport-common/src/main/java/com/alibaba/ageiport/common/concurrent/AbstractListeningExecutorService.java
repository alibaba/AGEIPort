package com.alibaba.ageiport.common.concurrent;


import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;


/**
 * @author lingyi
 */
public abstract class AbstractListeningExecutorService extends AbstractExecutorService implements ListeningExecutorService {

    /**
     * @since 19.0 (present with return type {@code ListenableFutureTask} since 14.0)
     */
    @Override
    protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return TrustedListenableFutureTask.create(runnable, value);
    }

    /**
     * @since 19.0 (present with return type {@code ListenableFutureTask} since 14.0)
     */
    @Override
    protected final <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return TrustedListenableFutureTask.create(callable);
    }

    @Override
    public ListenableFuture<?> submit(Runnable task) {
        return (ListenableFuture<?>) super.submit(task);
    }

    @Override
    public <T> ListenableFuture<T> submit(Runnable task, T result) {
        return (ListenableFuture<T>) super.submit(task, result);
    }

    @Override
    public <T> ListenableFuture<T> submit(Callable<T> task) {
        return (ListenableFuture<T>) super.submit(task);
    }
}