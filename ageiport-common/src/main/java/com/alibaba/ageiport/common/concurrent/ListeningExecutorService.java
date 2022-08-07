package com.alibaba.ageiport.common.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author lingyi
 */
public interface ListeningExecutorService extends ExecutorService {

    @Override
    <T> ListenableFuture<T> submit(Callable<T> task);

    @Override
    ListenableFuture<?> submit(Runnable task);

    @Override
    <T> ListenableFuture<T> submit(Runnable task, T result);

    @Override
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException;

    @Override
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException;
}