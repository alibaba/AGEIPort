package com.alibaba.ageiport.common.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class CallbackListener<V> implements Runnable {
    final Future<V> future;
    final FutureCallback<? super V> callback;

    public CallbackListener(Future<V> future, FutureCallback<? super V> callback) {
        this.future = future;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (future instanceof InternalFutureFailureAccess) {
            Throwable failure =
                    InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess) future);
            if (failure != null) {
                callback.onFailure(failure);
                return;
            }
        }
        final V value;
        try {
            value = getDone(future);
        } catch (ExecutionException e) {
            callback.onFailure(e.getCause());
            return;
        } catch (RuntimeException | Error e) {
            callback.onFailure(e);
            return;
        }
        callback.onSuccess(value);
    }

    public static <V> V getDone(Future<V> future) throws ExecutionException {
        /*
         * We throw IllegalStateException, since the call could succeed later. Perhaps we "should" throw
         * IllegalArgumentException, since the call could succeed with a different argument. Those
         * exceptions' docs suggest that either is acceptable. Google's Java Practices page recommends
         * IllegalArgumentException here, in part to keep its recommendation simple: Static methods
         * should throw IllegalStateException only when they use static state.
         *
         *
         * Why do we deviate here? The answer: We want for fluentFuture.getDone() to throw the same
         * exception as Futures.getDone(fluentFuture).
         */
        if (!future.isDone()) {
            throw new IllegalStateException("Future was expected to be done:" + future);
        }
        return getUninterruptibly(future);
    }

    public static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}