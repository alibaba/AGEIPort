package com.alibaba.ageiport.processor.core.eventbus.local.async;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

class Subscriber {

    static Subscriber create(EventBus bus, Object listener, Method method) {
        return new SynchronizedSubscriber(bus, listener, method);
    }

    private EventBus bus;

    final Object target;

    private final Method method;

    private final Executor executor;

    private Subscriber(EventBus bus, Object target, Method method) {
        this.bus = bus;
        this.target = target;
        this.method = method;
        method.setAccessible(true);

        this.executor = bus.executor();
    }


    final void dispatchEvent(final Object event) {
        executor.execute(() -> {
            try {
                invokeSubscriberMethod(event);
            } catch (InvocationTargetException e) {
                bus.handleSubscriberException(e.getCause(), context(event));
            }
        });
    }


    void invokeSubscriberMethod(Object event) throws InvocationTargetException {
        try {
            if (event == null) {
                throw new NullPointerException("event is null");
            }
            method.invoke(target, event);
        } catch (IllegalArgumentException e) {
            throw new Error("Method rejected target/argument: " + event, e);
        } catch (IllegalAccessException e) {
            throw new Error("Method became inaccessible: " + event, e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            }
            throw e;
        }
    }

    private SubscriberExceptionContext context(Object event) {
        return new SubscriberExceptionContext(bus, event, target, method);
    }

    @Override
    public final int hashCode() {
        return (31 + method.hashCode()) * 31 + System.identityHashCode(target);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Subscriber) {
            Subscriber that = (Subscriber) obj;
            return target == that.target && method.equals(that.method);
        }
        return false;
    }

    static final class SynchronizedSubscriber extends Subscriber {

        private SynchronizedSubscriber(EventBus bus, Object target, Method method) {
            super(bus, target, method);
        }

        @Override
        void invokeSubscriberMethod(Object event) throws InvocationTargetException {
            synchronized (this) {
                super.invokeSubscriberMethod(event);
            }
        }
    }
}