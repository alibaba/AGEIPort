package com.alibaba.ageiport.common.concurrent;

public abstract class InternalFutureFailureAccess {
    protected InternalFutureFailureAccess() {
    }

    protected abstract Throwable tryInternalFastPathGetFailure();
}