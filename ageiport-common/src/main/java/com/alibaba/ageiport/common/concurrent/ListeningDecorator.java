package com.alibaba.ageiport.common.concurrent;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ListeningDecorator extends AbstractListeningExecutorService {
    private final ExecutorService delegate;

    public ListeningDecorator(ExecutorService delegate) {
      this.delegate = delegate;
    }

    @Override
    public final boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public final boolean isShutdown() {
      return delegate.isShutdown();
    }

    @Override
    public final boolean isTerminated() {
      return delegate.isTerminated();
    }

    @Override
    public final void shutdown() {
      delegate.shutdown();
    }

    @Override
    public final List<Runnable> shutdownNow() {
      return delegate.shutdownNow();
    }

    @Override
    public final void execute(Runnable command) {
      delegate.execute(command);
    }
  }