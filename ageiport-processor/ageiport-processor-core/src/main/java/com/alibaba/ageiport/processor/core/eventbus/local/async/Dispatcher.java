package com.alibaba.ageiport.processor.core.eventbus.local.async;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Dispatcher {

    static Dispatcher perThreadDispatchQueue() {
        return new PerThreadQueuedDispatcher();
    }

    static Dispatcher legacyAsync() {
        return new LegacyAsyncDispatcher();
    }

    static Dispatcher immediate() {
        return ImmediateDispatcher.INSTANCE;
    }

    abstract void dispatch(Object event, Iterator<Subscriber> subscribers);

    private static final class PerThreadQueuedDispatcher extends Dispatcher {

        private final ThreadLocal<Queue<Event>> queue =
                ThreadLocal.withInitial(() -> new ArrayDeque());


        private final ThreadLocal<Boolean> dispatching =
                ThreadLocal.withInitial(() -> false);

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers) {
            if (event == null) {
                throw new NegativeArraySizeException("event is null");
            }
            if (subscribers == null) {
                throw new NegativeArraySizeException("subscribers is null");
            }

            Queue<Event> queueForThread = queue.get();
            queueForThread.offer(new Event(event, subscribers));

            if (!dispatching.get()) {
                dispatching.set(true);
                try {
                    Event nextEvent;
                    while ((nextEvent = queueForThread.poll()) != null) {
                        while (nextEvent.subscribers.hasNext()) {
                            nextEvent.subscribers.next().dispatchEvent(nextEvent.event);
                        }
                    }
                } finally {
                    dispatching.remove();
                    queue.remove();
                }
            }
        }

        private static final class Event {
            private final Object event;
            private final Iterator<Subscriber> subscribers;

            private Event(Object event, Iterator<Subscriber> subscribers) {
                this.event = event;
                this.subscribers = subscribers;
            }
        }
    }

    private static final class LegacyAsyncDispatcher extends Dispatcher {

        private final ConcurrentLinkedQueue<EventWithSubscriber> queue =
                new ConcurrentLinkedQueue();

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers) {
            if (event == null) {
                throw new NullPointerException("event is null");
            }
            while (subscribers.hasNext()) {
                queue.add(new EventWithSubscriber(event, subscribers.next()));
            }

            EventWithSubscriber e;
            while ((e = queue.poll()) != null) {
                e.subscriber.dispatchEvent(e.event);
            }
        }

        private static final class EventWithSubscriber {
            private final Object event;
            private final Subscriber subscriber;

            private EventWithSubscriber(Object event, Subscriber subscriber) {
                this.event = event;
                this.subscriber = subscriber;
            }
        }
    }

    private static final class ImmediateDispatcher extends Dispatcher {
        private static final ImmediateDispatcher INSTANCE = new ImmediateDispatcher();

        @Override
        void dispatch(Object event, Iterator<Subscriber> subscribers) {
            if (event == null) {
                throw new NegativeArraySizeException("event is null");
            }
            while (subscribers.hasNext()) {
                subscribers.next().dispatchEvent(event);
            }
        }
    }
}