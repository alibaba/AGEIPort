package com.alibaba.ageiport.processor.core.eventbus.local.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.Executor;


public class EventBus {

    private static final Logger logger = LoggerFactory.getLogger(EventBus.class.getName());

    private final String identifier;
    private final Executor executor;
    private final SubscriberExceptionHandler exceptionHandler;

    private final SubscriberRegistry subscribers = new SubscriberRegistry(this);
    private final Dispatcher dispatcher;


    public EventBus(
            String identifier,
            Executor executor,
            Dispatcher dispatcher,
            SubscriberExceptionHandler exceptionHandler) {
        if (identifier == null) {
            throw new NullPointerException("identifier is null");
        }
        if (executor == null) {
            throw new NullPointerException("executor is null");
        }
        if (dispatcher == null) {
            throw new NullPointerException("dispatcher is null");
        }
        if (exceptionHandler == null) {
            throw new NullPointerException("exceptionHandler is null");
        }
        this.identifier = identifier;
        this.executor = executor;
        this.dispatcher = dispatcher;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Returns the identifier for this event bus.
     *
     * @since 19.0
     */
    public final String identifier() {
        return identifier;
    }

    /**
     * Returns the default executor this event bus uses for dispatching events to subscribers.
     */
    final Executor executor() {
        return executor;
    }

    /**
     * Handles the given exception thrown by a subscriber with the given context.
     */
    void handleSubscriberException(Throwable e, SubscriberExceptionContext context) {
        if (e == null) {
            throw new NullPointerException("e is null");
        }
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        try {
            exceptionHandler.handleException(e, context);
        } catch (Throwable e2) {
            // if the handler threw an exception... well, just log it
            logger.error("Exception {} thrown while handling exception: {}", e2, e, e2);
        }
    }

    /**
     * Registers all subscriber methods on {@code object} to receive events.
     *
     * @param object object whose subscriber methods should be registered.
     */
    public void register(Object object) {
        subscribers.register(object);
    }

    /**
     * Unregisters all subscriber methods on a registered {@code object}.
     *
     * @param object object whose subscriber methods should be unregistered.
     * @throws IllegalArgumentException if the object was not previously registered.
     */
    public void unregister(Object object) {
        subscribers.unregister(object);
    }

    /**
     * Posts an event to all registered subscribers. This method will return successfully after the
     * event has been posted to all subscribers, and regardless of any exceptions thrown by
     * subscribers.
     *
     * @param event event to post.
     */
    public void post(Object event) {
        Iterator<Subscriber> eventSubscribers = subscribers.getSubscribers(event);
        if (eventSubscribers.hasNext()) {
            dispatcher.dispatch(event, eventSubscribers);
        }
    }

    /**
     * Simple logging handler for subscriber exceptions.
     */
    static final class LoggingHandler implements SubscriberExceptionHandler {
        static final LoggingHandler INSTANCE = new LoggingHandler();
        private static final Logger logger = LoggerFactory.getLogger(LoggingHandler.class.getName());

        @Override
        public void handleException(Throwable exception, SubscriberExceptionContext context) {
            logger.error(message(context), exception);
        }

        private static String message(SubscriberExceptionContext context) {
            Method method = context.getSubscriberMethod();
            return "Exception thrown by subscriber method "
                    + method.getName()
                    + '('
                    + method.getParameterTypes()[0].getName()
                    + ')'
                    + " on subscriber "
                    + context.getSubscriber()
                    + " when dispatching event: "
                    + context.getEvent();
        }
    }
}
