package com.alibaba.ageiport.common.concurrent;

import java.util.concurrent.Executor;

/**
 * An {@link Executor} that runs each task in the thread that invokes {@link Executor#execute
 * execute}.
 */
enum DirectExecutor implements Executor {
    INSTANCE;

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public String toString() {
        return "MoreExecutors.directExecutor()";
    }
}