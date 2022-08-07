package com.alibaba.ageiport.common.concurrent;


import java.util.concurrent.*;

public final class TimeoutFuture<V> extends FluentFuture.TrustedFuture<V> {
    public static <V> ListenableFuture<V> create(
            ListenableFuture<V> delegate,
            long time,
            TimeUnit unit,
            ScheduledExecutorService scheduledExecutor) {
        TimeoutFuture<V> result = new TimeoutFuture<>(delegate);
        Fire<V> fire = new Fire<>(result);
        result.timer = scheduledExecutor.schedule(fire, time, unit);
        delegate.addListener(fire, DirectExecutor.INSTANCE);
        return result;
    }


    private
    ListenableFuture<V> delegateRef;
    private
    ScheduledFuture<?> timer;

    private TimeoutFuture(ListenableFuture<V> delegate) {
        this.delegateRef = delegate;
    }

    /**
     * A runnable that is called when the delegate or the timer completes.
     */
    private static final class Fire<V> implements Runnable {
        TimeoutFuture<V> timeoutFutureRef;

        Fire(TimeoutFuture<V> timeoutFuture) {
            this.timeoutFutureRef = timeoutFuture;
        }

        @Override
        public void run() {
            // If either of these reads return null then we must be after a successful cancel or another
            // call to this method.
            TimeoutFuture<V> timeoutFuture = timeoutFutureRef;
            if (timeoutFuture == null) {
                return;
            }
            ListenableFuture<V> delegate = timeoutFuture.delegateRef;
            if (delegate == null) {
                return;
            }


            timeoutFutureRef = null;
            if (delegate.isDone()) {
                timeoutFuture.setFuture(delegate);
            } else {
                try {
                    ScheduledFuture<?> timer = timeoutFuture.timer;
                    timeoutFuture.timer = null;
                    String message = "Timed out";
                    try {
                        if (timer != null) {
                            long overDelayMs = Math.abs(timer.getDelay(TimeUnit.MILLISECONDS));
                            if (overDelayMs > 10) {
                                message += " (timeout delayed by " + overDelayMs + " ms after scheduled time)";
                            }
                        }
                        message += ": " + delegate;
                    } finally {
                        timeoutFuture.setException(new TimeoutFutureException(message));
                    }
                } finally {
                    delegate.cancel(true);
                }
            }
        }
    }

    private static final class TimeoutFutureException extends TimeoutException {

        private static final long serialVersionUID = 525283805297276561L;

        private TimeoutFutureException(String message) {
            super(message);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            setStackTrace(new StackTraceElement[0]);
            return this;
        }
    }

    @Override
    protected String pendingToString() {
        ListenableFuture<? extends V> localInputFuture = delegateRef;
        ScheduledFuture<?> localTimer = timer;
        if (localInputFuture != null) {
            String message = "inputFuture=[" + localInputFuture + "]";
            if (localTimer != null) {
                final long delay = localTimer.getDelay(TimeUnit.MILLISECONDS);
                // Negative delays look confusing in an error message
                if (delay > 0) {
                    message += ", remaining delay=[" + delay + " ms]";
                }
            }
            return message;
        }
        return null;
    }

    @Override
    protected void afterDone() {
        maybePropagateCancellationTo(delegateRef);

        Future<?> localTimer = timer;
        if (localTimer != null) {
            localTimer.cancel(false);
        }

        delegateRef = null;
        timer = null;
    }
}