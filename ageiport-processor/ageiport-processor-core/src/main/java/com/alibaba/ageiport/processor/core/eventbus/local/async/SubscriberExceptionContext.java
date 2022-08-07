package com.alibaba.ageiport.processor.core.eventbus.local.async;

import java.lang.reflect.Method;

public class SubscriberExceptionContext {
    private final EventBus eventBus;
    private final Object event;
    private final Object subscriber;
    private final Method subscriberMethod;

    SubscriberExceptionContext(
            EventBus eventBus, Object event, Object subscriber, Method subscriberMethod) {
        if (eventBus == null) {
            throw new NullPointerException("eventBus is null");
        }

        if (event == null) {
            throw new NullPointerException("eveneventtBus is null");
        }

        if (subscriber == null) {
            throw new NullPointerException("subscriber is null");
        }

        if (subscriberMethod == null) {
            throw new NullPointerException("subscriberMethod is null");
        }

        this.eventBus = eventBus;
        this.event = event;
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Object getEvent() {
        return event;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public Method getSubscriberMethod() {
        return subscriberMethod;
    }
}