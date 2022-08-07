package com.alibaba.ageiport.common.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
public interface ListenableFuture<V> extends Future<V> {
    void addListener(Runnable listener, Executor executor);
}