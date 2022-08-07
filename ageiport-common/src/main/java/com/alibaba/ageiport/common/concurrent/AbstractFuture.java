package com.alibaba.ageiport.common.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;

import static java.util.concurrent.atomic.AtomicReferenceFieldUpdater.newUpdater;

@Slf4j
public abstract class AbstractFuture<V> extends InternalFutureFailureAccess
        implements ListenableFuture<V> {


    private static final boolean GENERATE_CANCELLATION_CAUSES;

    static {
        boolean generateCancellationCauses;
        try {
            generateCancellationCauses =
                    Boolean.parseBoolean(
                            System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));
        } catch (SecurityException e) {
            generateCancellationCauses = false;
        }
        GENERATE_CANCELLATION_CAUSES = generateCancellationCauses;
    }


    interface Trusted<V> extends ListenableFuture<V> {
    }

    private static final long SPIN_THRESHOLD_NANOS = 1000L;

    private static final AtomicHelper ATOMIC_HELPER;

    static {
        AtomicHelper helper;
        Throwable thrownUnsafeFailure = null;
        Throwable thrownAtomicReferenceFieldUpdaterFailure = null;

        helper =
                new SafeAtomicHelper(
                        newUpdater(Waiter.class, Thread.class, "thread"),
                        newUpdater(Waiter.class, Waiter.class, "next"),
                        newUpdater(AbstractFuture.class, Waiter.class, "waiters"),
                        newUpdater(AbstractFuture.class, Listener.class, "listeners"),
                        newUpdater(AbstractFuture.class, Object.class, "value"));
        ATOMIC_HELPER = helper;

        Class<?> ensureLoaded = LockSupport.class;

        if (thrownAtomicReferenceFieldUpdaterFailure != null) {
            log.warn("UnsafeAtomicHelper is broken!", thrownUnsafeFailure);
            log.warn("SafeAtomicHelper is broken!", thrownAtomicReferenceFieldUpdaterFailure);
        }
    }

    /**
     * Waiter links form a Treiber stack, in the {@link #waiters} field.
     */
    private static final class Waiter {
        static final Waiter TOMBSTONE = new Waiter(false /* ignored param */);

        volatile
        Thread thread;
        volatile
        Waiter next;

        Waiter(boolean unused) {
        }

        Waiter() {

            ATOMIC_HELPER.putThread(this, Thread.currentThread());
        }


        void setNext(Waiter next) {
            ATOMIC_HELPER.putNext(this, next);
        }

        void unpark() {

            Thread w = thread;
            if (w != null) {
                thread = null;
                LockSupport.unpark(w);
            }
        }
    }


    private void removeWaiter(Waiter node) {
        node.thread = null; // mark as 'deleted'
        restart:
        while (true) {
            Waiter pred = null;
            Waiter curr = waiters;
            if (curr == Waiter.TOMBSTONE) {
                return; // give up if someone is calling complete
            }
            Waiter succ;
            while (curr != null) {
                succ = curr.next;
                if (curr.thread != null) { // we aren't unlinking this node, update pred.
                    pred = curr;
                } else if (pred != null) { // We are unlinking this node and it has a predecessor.
                    pred.next = succ;
                    if (pred.thread == null) { // We raced with another node that unlinked pred. Restart.
                        continue restart;
                    }
                } else if (!ATOMIC_HELPER.casWaiters(this, curr, succ)) { // We are unlinking head
                    continue restart; // We raced with an add or complete
                }
                curr = succ;
            }
            break;
        }
    }

    /**
     * Listeners also form a stack through the {@link #listeners} field.
     */
    private static final class Listener {
        static final Listener TOMBSTONE = new Listener(null, null);
        final Runnable task;
        final Executor executor;

        // writes to next are made visible by subsequent CAS's on the listeners field
        Listener next;

        Listener(Runnable task, Executor executor) {
            this.task = task;
            this.executor = executor;
        }
    }

    /**
     * A special value to represent {@code null}.
     */
    private static final Object NULL = new Object();

    /**
     * A special value to represent failure, when {@link #setException} is called successfully.
     */
    private static final class Failure {
        static final Failure FALLBACK_INSTANCE =
                new Failure(
                        new Throwable("Failure occurred while trying to finish a future.") {
                            private static final long serialVersionUID = 964519898025613334L;

                            @Override
                            public synchronized Throwable fillInStackTrace() {
                                return this; // no stack trace
                            }
                        });
        final Throwable exception;

        Failure(Throwable exception) {
            this.exception = exception;
        }
    }


    private static final class Cancellation {
        // constants to use when GENERATE_CANCELLATION_CAUSES = false
        static final Cancellation CAUSELESS_INTERRUPTED;
        static final Cancellation CAUSELESS_CANCELLED;

        static {
            if (GENERATE_CANCELLATION_CAUSES) {
                CAUSELESS_CANCELLED = null;
                CAUSELESS_INTERRUPTED = null;
            } else {
                CAUSELESS_CANCELLED = new Cancellation(false, null);
                CAUSELESS_INTERRUPTED = new Cancellation(true, null);
            }
        }

        final boolean wasInterrupted;
        final
        Throwable cause;

        Cancellation(boolean wasInterrupted, Throwable cause) {
            this.wasInterrupted = wasInterrupted;
            this.cause = cause;
        }
    }

    /**
     * A special value that encodes the 'setFuture' state.
     */
    private static final class SetFuture<V> implements Runnable {
        final AbstractFuture<V> owner;
        final ListenableFuture<? extends V> future;

        SetFuture(AbstractFuture<V> owner, ListenableFuture<? extends V> future) {
            this.owner = owner;
            this.future = future;
        }

        @Override
        public void run() {
            if (owner.value != this) {
                // nothing to do, we must have been cancelled, don't bother inspecting the future.
                return;
            }
            Object valueToSet = getFutureValue(future);
            if (ATOMIC_HELPER.casValue(owner, this, valueToSet)) {
                complete(owner);
            }
        }
    }

    private volatile
    Object value;


    private volatile
    Listener listeners;

    private volatile
    Waiter waiters;

    /**
     * Constructor for use by subclasses.
     */
    protected AbstractFuture() {
    }

    @Override
    public V get(long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException, ExecutionException {
        // NOTE: if timeout < 0, remainingNanos will be < 0 and we will fall into the while(true) loop
        // at the bottom and throw a timeoutexception.
        final long timeoutNanos = unit.toNanos(timeout); // we rely on the implicit null check on unit.
        long remainingNanos = timeoutNanos;
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Object localValue = value;
        if (localValue != null & !(localValue instanceof SetFuture)) {
            return getDoneValue(localValue);
        }
        // we delay calling nanoTime until we know we will need to either park or spin
        final long endNanos = remainingNanos > 0 ? System.nanoTime() + remainingNanos : 0;
        long_wait_loop:
        if (remainingNanos >= SPIN_THRESHOLD_NANOS) {
            Waiter oldHead = waiters;
            if (oldHead != Waiter.TOMBSTONE) {
                Waiter node = new Waiter();
                do {
                    node.setNext(oldHead);
                    if (ATOMIC_HELPER.casWaiters(this, oldHead, node)) {
                        while (true) {
                            LockSupport.parkNanos(this, remainingNanos);
                            // Check interruption first, if we woke up due to interruption we need to honor that.
                            if (Thread.interrupted()) {
                                removeWaiter(node);
                                throw new InterruptedException();
                            }

                            // Otherwise re-read and check doneness. If we loop then it must have been a spurious
                            // wakeup
                            localValue = value;
                            if (localValue != null & !(localValue instanceof SetFuture)) {
                                return getDoneValue(localValue);
                            }

                            // timed out?
                            remainingNanos = endNanos - System.nanoTime();
                            if (remainingNanos < SPIN_THRESHOLD_NANOS) {
                                // Remove the waiter, one way or another we are done parking this thread.
                                removeWaiter(node);
                                break long_wait_loop; // jump down to the busy wait loop
                            }
                        }
                    }
                    oldHead = waiters; // re-read and loop.
                } while (oldHead != Waiter.TOMBSTONE);
            }
            // re-read value, if we get here then we must have observed a TOMBSTONE while trying to add a
            // waiter.
            return getDoneValue(value);
        }
        // If we get here then we have remainingNanos < SPIN_THRESHOLD_NANOS and there is no node on the
        // waiters list
        while (remainingNanos > 0) {
            localValue = value;
            if (localValue != null & !(localValue instanceof SetFuture)) {
                return getDoneValue(localValue);
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            remainingNanos = endNanos - System.nanoTime();
        }

        String futureToString = toString();
        final String unitString = unit.toString().toLowerCase(Locale.ROOT);
        String message = "Waited " + timeout + " " + unit.toString().toLowerCase(Locale.ROOT);
        if (remainingNanos + SPIN_THRESHOLD_NANOS < 0) {
            message += " (plus ";
            long overWaitNanos = -remainingNanos;
            long overWaitUnits = unit.convert(overWaitNanos, TimeUnit.NANOSECONDS);
            long overWaitLeftoverNanos = overWaitNanos - unit.toNanos(overWaitUnits);
            boolean shouldShowExtraNanos =
                    overWaitUnits == 0 || overWaitLeftoverNanos > SPIN_THRESHOLD_NANOS;
            if (overWaitUnits > 0) {
                message += overWaitUnits + " " + unitString;
                if (shouldShowExtraNanos) {
                    message += ",";
                }
                message += " ";
            }
            if (shouldShowExtraNanos) {
                message += overWaitLeftoverNanos + " nanoseconds ";
            }

            message += "delay)";
        }
        // It's confusing to see a completed future in a timeout message; if isDone() returns false,
        // then we know it must have given a pending toString value earlier. If not, then the future
        // completed after the timeout expired, and the message might be success.
        if (isDone()) {
            throw new TimeoutException(message + " but future completed as timeout expired");
        }
        throw new TimeoutException(message + " for " + futureToString);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default {@link AbstractFuture} implementation throws {@code InterruptedException} if the
     * current thread is interrupted during the call, even if the value is already available.
     *
     * @throws CancellationException {@inheritDoc}
     */
    @Override
    public V get() throws InterruptedException, ExecutionException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Object localValue = value;
        if (localValue != null & !(localValue instanceof SetFuture)) {
            return getDoneValue(localValue);
        }
        Waiter oldHead = waiters;
        if (oldHead != Waiter.TOMBSTONE) {
            Waiter node = new Waiter();
            do {
                node.setNext(oldHead);
                if (ATOMIC_HELPER.casWaiters(this, oldHead, node)) {
                    // we are on the stack, now wait for completion.
                    while (true) {
                        LockSupport.park(this);
                        // Check interruption first, if we woke up due to interruption we need to honor that.
                        if (Thread.interrupted()) {
                            removeWaiter(node);
                            throw new InterruptedException();
                        }
                        // Otherwise re-read and check doneness. If we loop then it must have been a spurious
                        // wakeup
                        localValue = value;
                        if (localValue != null & !(localValue instanceof SetFuture)) {
                            return getDoneValue(localValue);
                        }
                    }
                }
                oldHead = waiters; // re-read and loop.
            } while (oldHead != Waiter.TOMBSTONE);
        }
        // re-read value, if we get here then we must have observed a TOMBSTONE while trying to add a
        // waiter.
        return getDoneValue(value);
    }

    /**
     * Unboxes {@code obj}. Assumes that obj is not {@code null} or a {@link SetFuture}.
     */
    private V getDoneValue(Object obj) throws ExecutionException {
        // While this seems like it might be too branch-y, simple benchmarking proves it to be
        // unmeasurable (comparing done AbstractFutures with immediateFuture)
        if (obj instanceof Cancellation) {
            throw cancellationExceptionWithCause("Task was cancelled.", ((Cancellation) obj).cause);
        } else if (obj instanceof Failure) {
            throw new ExecutionException(((Failure) obj).exception);
        } else if (obj == NULL) {
            return null;
        } else {
            @SuppressWarnings("unchecked") // this is the only other option
            V asV = (V) obj;
            return asV;
        }
    }

    @Override
    public boolean isDone() {
        final Object localValue = value;
        return localValue != null & !(localValue instanceof SetFuture);
    }

    @Override
    public boolean isCancelled() {
        final Object localValue = value;
        return localValue instanceof Cancellation;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Object localValue = value;
        boolean rValue = false;
        if (localValue == null | localValue instanceof SetFuture) {
            Object valueToSet =
                    GENERATE_CANCELLATION_CAUSES
                            ? new Cancellation(
                            mayInterruptIfRunning, new CancellationException("Future.cancel() was called."))
                            : (mayInterruptIfRunning
                            ? Cancellation.CAUSELESS_INTERRUPTED
                            : Cancellation.CAUSELESS_CANCELLED);
            AbstractFuture<?> abstractFuture = this;
            while (true) {
                if (ATOMIC_HELPER.casValue(abstractFuture, localValue, valueToSet)) {
                    rValue = true;
                    // We call interuptTask before calling complete(), which is consistent with
                    // FutureTask
                    if (mayInterruptIfRunning) {
                        abstractFuture.interruptTask();
                    }
                    complete(abstractFuture);
                    if (localValue instanceof SetFuture) {
                        // propagate cancellation to the future set in setfuture, this is racy, and we don't
                        // care if we are successful or not.
                        ListenableFuture<?> futureToPropagateTo = ((SetFuture) localValue).future;
                        if (futureToPropagateTo instanceof Trusted) {
                            // If the future is a TrustedFuture then we specifically avoid calling cancel()
                            // this has 2 benefits
                            // 1. for long chains of futures strung together with setFuture we consume less stack
                            // 2. we avoid allocating Cancellation objects at every level of the cancellation
                            //    chain
                            // We can only do this for TrustedFuture, because TrustedFuture.cancel is final and
                            // does nothing but delegate to this method.
                            AbstractFuture<?> trusted = (AbstractFuture<?>) futureToPropagateTo;
                            localValue = trusted.value;
                            if (localValue == null | localValue instanceof SetFuture) {
                                abstractFuture = trusted;
                                continue; // loop back up and try to complete the new future
                            }
                        } else {
                            // not a TrustedFuture, call cancel directly.
                            futureToPropagateTo.cancel(mayInterruptIfRunning);
                        }
                    }
                    break;
                }
                localValue = abstractFuture.value;
                if (!(localValue instanceof SetFuture)) {
                    break;
                }
            }
        }
        return rValue;
    }

    protected void interruptTask() {
    }

    protected final boolean wasInterrupted() {
        final Object localValue = value;
        return (localValue instanceof Cancellation) && ((Cancellation) localValue).wasInterrupted;
    }

    /**
     * {@inheritDoc}
     *
     * @since 10.0
     */
    @Override
    public void addListener(Runnable listener, Executor executor) {
        if (!isDone()) {
            Listener oldHead = listeners;
            if (oldHead != Listener.TOMBSTONE) {
                Listener newNode = new Listener(listener, executor);
                do {
                    newNode.next = oldHead;
                    if (ATOMIC_HELPER.casListeners(this, oldHead, newNode)) {
                        return;
                    }
                    oldHead = listeners; // re-read
                } while (oldHead != Listener.TOMBSTONE);
            }
        }
        executeListener(listener, executor);
    }

    protected boolean set(V value) {
        Object valueToSet = value == null ? NULL : value;
        if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
            complete(this);
            return true;
        }
        return false;
    }


    protected boolean setException(Throwable throwable) {
        Object valueToSet = new Failure(throwable);
        if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
            complete(this);
            return true;
        }
        return false;
    }

    protected boolean setFuture(ListenableFuture<? extends V> future) {
        Object localValue = value;
        if (localValue == null) {
            if (future.isDone()) {
                Object value = getFutureValue(future);
                if (ATOMIC_HELPER.casValue(this, null, value)) {
                    complete(this);
                    return true;
                }
                return false;
            }
            SetFuture valueToSet = new SetFuture<V>(this, future);
            if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
                try {
                    future.addListener(valueToSet, DirectExecutor.INSTANCE);
                } catch (Throwable t) {
                    Failure failure;
                    try {
                        failure = new Failure(t);
                    } catch (Throwable oomMostLikely) {
                        failure = Failure.FALLBACK_INSTANCE;
                    }
                    boolean unused = ATOMIC_HELPER.casValue(this, valueToSet, failure);
                }
                return true;
            }
            localValue = value; // we lost the cas, fall through and maybe cancel
        }
        if (localValue instanceof Cancellation) {
            future.cancel(((Cancellation) localValue).wasInterrupted);
        }
        return false;
    }

    private static Object getFutureValue(ListenableFuture<?> future) {
        if (future instanceof Trusted) {
            Object v = ((AbstractFuture<?>) future).value;
            if (v instanceof Cancellation) {
                Cancellation c = (Cancellation) v;
                if (c.wasInterrupted) {
                    v =
                            c.cause != null
                                    ? new Cancellation(/* wasInterrupted= */ false, c.cause)
                                    : Cancellation.CAUSELESS_CANCELLED;
                }
            }
            return v;
        }
        if (future instanceof InternalFutureFailureAccess) {
            Throwable throwable =
                    InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess) future);
            if (throwable != null) {
                return new Failure(throwable);
            }
        }
        boolean wasCancelled = future.isCancelled();
        // Don't allocate a CancellationException if it's not necessary
        if (!GENERATE_CANCELLATION_CAUSES & wasCancelled) {
            return Cancellation.CAUSELESS_CANCELLED;
        }
        // Otherwise calculate the value by calling .get()
        try {
            Object v = getUninterruptibly(future);
            if (wasCancelled) {
                return new Cancellation(
                        false,
                        new IllegalArgumentException(
                                "get() did not throw CancellationException, despite reporting "
                                        + "isCancelled() == true: "
                                        + future));
            }
            return v == null ? NULL : v;
        } catch (ExecutionException exception) {
            if (wasCancelled) {
                return new Cancellation(
                        false,
                        new IllegalArgumentException(
                                "get() did not throw CancellationException, despite reporting "
                                        + "isCancelled() == true: "
                                        + future,
                                exception));
            }
            return new Failure(exception.getCause());
        } catch (CancellationException cancellation) {
            if (!wasCancelled) {
                return new Failure(
                        new IllegalArgumentException(
                                "get() threw CancellationException, despite reporting isCancelled() == false: "
                                        + future,
                                cancellation));
            }
            return new Cancellation(false, cancellation);
        } catch (Throwable t) {
            return new Failure(t);
        }
    }

    /**
     * internal dependency on other /util/concurrent classes.
     */
    private static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
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

    /**
     * Unblocks all threads and runs all listeners.
     */
    private static void complete(AbstractFuture<?> future) {
        Listener next = null;
        outer:
        while (true) {
            future.releaseWaiters();
            // We call this before the listeners in order to avoid needing to manage a separate stack data
            // structure for them.  Also, some implementations rely on this running prior to listeners
            // so that the cleanup work is visible to listeners.
            // afterDone() should be generally fast and only used for cleanup work... but in theory can
            // also be recursive and create StackOverflowErrors
            future.afterDone();
            // push the current set of listeners onto next
            next = future.clearListeners(next);
            future = null;
            while (next != null) {
                Listener curr = next;
                next = next.next;
                Runnable task = curr.task;
                if (task instanceof SetFuture) {
                    SetFuture<?> setFuture = (SetFuture<?>) task;
                    future = setFuture.owner;
                    if (future.value == setFuture) {
                        Object valueToSet = getFutureValue(setFuture.future);
                        if (ATOMIC_HELPER.casValue(future, setFuture, valueToSet)) {
                            continue outer;
                        }
                    }
                    // other wise the future we were trying to set is already done.
                } else {
                    executeListener(task, curr.executor);
                }
            }
            break;
        }
    }


    protected void afterDone() {
    }

    // TODO(b/114236866): Inherit doc from InternalFutureFailureAccess. Also, -link to its URL.


    @Override
    protected final Throwable tryInternalFastPathGetFailure() {
        if (this instanceof Trusted) {
            Object obj = value;
            if (obj instanceof Failure) {
                return ((Failure) obj).exception;
            }
        }
        return null;
    }


    final void maybePropagateCancellationTo(Future<?> related) {
        if (related != null & isCancelled()) {
            related.cancel(wasInterrupted());
        }
    }


    private void releaseWaiters() {
        Waiter head;
        do {
            head = waiters;
        } while (!ATOMIC_HELPER.casWaiters(this, head, Waiter.TOMBSTONE));
        for (Waiter currentWaiter = head; currentWaiter != null; currentWaiter = currentWaiter.next) {
            currentWaiter.unpark();
        }
    }


    private Listener clearListeners(Listener onto) {

        Listener head;
        do {
            head = listeners;
        } while (!ATOMIC_HELPER.casListeners(this, head, Listener.TOMBSTONE));
        Listener reversedList = onto;
        while (head != null) {
            Listener tmp = head;
            head = head.next;
            tmp.next = reversedList;
            reversedList = tmp;
        }
        return reversedList;
    }

    // TODO(user): move parts into a default method on ListenableFuture?
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(super.toString()).append("[status=");
        if (isCancelled()) {
            builder.append("CANCELLED");
        } else if (isDone()) {
            addDoneString(builder);
        } else {
            addPendingString(builder); // delegates to addDoneString if future completes mid-way
        }
        return builder.append("]").toString();
    }

    /**
     * Provide a human-readable explanation of why this future has not yet completed.
     *
     * @return null if an explanation cannot be provided (e.g. because the future is done).
     * @since 23.0
     */
    protected String pendingToString() {
        // TODO(diamondm) consider moving this into addPendingString so it's always in the output
        if (this instanceof ScheduledFuture) {
            return "remaining delay=["
                    + ((ScheduledFuture) this).getDelay(TimeUnit.MILLISECONDS)
                    + " ms]";
        }
        return null;
    }

    private void addPendingString(StringBuilder builder) {
        // Capture current builder length so it can be truncated if this future ends up completing while
        // the toString is being calculated
        int truncateLength = builder.length();

        builder.append("PENDING");

        Object localValue = value;
        if (localValue instanceof SetFuture) {
            builder.append(", setFuture=[");
            appendUserObject(builder, ((SetFuture) localValue).future);
            builder.append("]");
        } else {
            String pendingDescription;
            try {
                pendingDescription = pendingToString();
            } catch (RuntimeException | StackOverflowError e) {
                // Don't call getMessage or toString() on the exception, in case the exception thrown by the
                // subclass is implemented with bugs similar to the subclass.
                pendingDescription = "Exception thrown from implementation: " + e.getClass();
            }
            if (pendingDescription != null) {
                builder.append(", info=[").append(pendingDescription).append("]");
            }
        }

        // The future may complete while calculating the toString, so we check once more to see if the
        // future is done
        if (isDone()) {
            // Truncate anything that was appended before realizing this future is done
            builder.delete(truncateLength, builder.length());
            addDoneString(builder);
        }
    }

    private void addDoneString(StringBuilder builder) {
        try {
            V value = getUninterruptibly(this);
            builder.append("SUCCESS, result=[");
            appendUserObject(builder, value);
            builder.append("]");
        } catch (ExecutionException e) {
            builder.append("FAILURE, cause=[").append(e.getCause()).append("]");
        } catch (CancellationException e) {
            builder.append("CANCELLED"); // shouldn't be reachable
        } catch (RuntimeException e) {
            builder.append("UNKNOWN, cause=[").append(e.getClass()).append(" thrown from get()]");
        }
    }

    /**
     * Helper for printing user supplied objects into our toString method.
     */
    private void appendUserObject(StringBuilder builder, Object o) {
        // This is some basic recursion detection for when people create cycles via set/setFuture or
        // when deep chains of futures exist resulting in a StackOverflowException. We could detect
        // arbitrary cycles using a thread local but this should be a good enough solution (it is also
        // what jdk collections do in these cases)
        try {
            if (o == this) {
                builder.append("this future");
            } else {
                builder.append(o);
            }
        } catch (RuntimeException | StackOverflowError e) {
            // Don't call getMessage or toString() on the exception, in case the exception thrown by the
            // user object is implemented with bugs similar to the user object.
            builder.append("Exception thrown from implementation: ").append(e.getClass());
        }
    }

    /**
     * Submits the given runnable to the given {@link Executor} catching and logging all {@linkplain
     * RuntimeException runtime exceptions} thrown by the executor.
     */
    private static void executeListener(Runnable runnable, Executor executor) {
        try {
            executor.execute(runnable);
        } catch (RuntimeException e) {
            // Log it and keep going -- bad runnable and/or executor. Don't punish the other runnables if
            // we're given a bad one. We only catch RuntimeException because we want Errors to propagate
            // up.
            e.printStackTrace();
        }
    }

    private abstract static class AtomicHelper {

        abstract void putThread(Waiter waiter, Thread newValue);

        abstract void putNext(Waiter waiter, Waiter newValue);

        abstract boolean casWaiters(AbstractFuture<?> future, Waiter expect, Waiter update);

        abstract boolean casListeners(AbstractFuture<?> future, Listener expect, Listener update);

        abstract boolean casValue(AbstractFuture<?> future, Object expect, Object update);
    }


    private static final class SafeAtomicHelper extends AtomicHelper {
        final AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater;
        final AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;

        SafeAtomicHelper(
                AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater,
                AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater,
                AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater,
                AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater,
                AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater) {
            this.waiterThreadUpdater = waiterThreadUpdater;
            this.waiterNextUpdater = waiterNextUpdater;
            this.waitersUpdater = waitersUpdater;
            this.listenersUpdater = listenersUpdater;
            this.valueUpdater = valueUpdater;
        }

        @Override
        void putThread(Waiter waiter, Thread newValue) {
            waiterThreadUpdater.lazySet(waiter, newValue);
        }

        @Override
        void putNext(Waiter waiter, Waiter newValue) {
            waiterNextUpdater.lazySet(waiter, newValue);
        }

        @Override
        boolean casWaiters(AbstractFuture<?> future, Waiter expect, Waiter update) {
            return waitersUpdater.compareAndSet(future, expect, update);
        }

        @Override
        boolean casListeners(AbstractFuture<?> future, Listener expect, Listener update) {
            return listenersUpdater.compareAndSet(future, expect, update);
        }

        @Override
        boolean casValue(AbstractFuture<?> future, Object expect, Object update) {
            return valueUpdater.compareAndSet(future, expect, update);
        }
    }

    /**
     * {@link AtomicHelper} based on {@code synchronized} and volatile writes.
     *
     * <p>This is an implementation of last resort for when certain basic VM features are broken (like
     * AtomicReferenceFieldUpdater).
     */
    private static final class SynchronizedHelper extends AtomicHelper {
        @Override
        void putThread(Waiter waiter, Thread newValue) {
            waiter.thread = newValue;
        }

        @Override
        void putNext(Waiter waiter, Waiter newValue) {
            waiter.next = newValue;
        }

        @Override
        boolean casWaiters(AbstractFuture<?> future, Waiter expect, Waiter update) {
            synchronized (future) {
                if (future.waiters == expect) {
                    future.waiters = update;
                    return true;
                }
                return false;
            }
        }

        @Override
        boolean casListeners(AbstractFuture<?> future, Listener expect, Listener update) {
            synchronized (future) {
                if (future.listeners == expect) {
                    future.listeners = update;
                    return true;
                }
                return false;
            }
        }

        @Override
        boolean casValue(AbstractFuture<?> future, Object expect, Object update) {
            synchronized (future) {
                if (future.value == expect) {
                    future.value = update;
                    return true;
                }
                return false;
            }
        }
    }

    private static CancellationException cancellationExceptionWithCause(
            String message, Throwable cause) {
        CancellationException exception = new CancellationException(message);
        exception.initCause(cause);
        return exception;
    }
}