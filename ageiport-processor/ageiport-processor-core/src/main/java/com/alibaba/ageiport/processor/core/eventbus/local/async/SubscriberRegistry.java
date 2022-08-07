package com.alibaba.ageiport.processor.core.eventbus.local.async;


import com.alibaba.ageiport.common.utils.ReflectUtils;
import com.alibaba.ageiport.common.utils.TypeUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public final class SubscriberRegistry {


    private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers =
            new ConcurrentHashMap<>();

    private final EventBus bus;

    public SubscriberRegistry(EventBus bus) {
        if (bus == null) {
            throw new NullPointerException("bus is null");
        }
        this.bus = bus;
    }

    void register(Object listener) {
        ConcurrentMap<Class<?>, Set<Subscriber>> listenerMethods = findAllSubscribers(listener);

        for (Map.Entry<Class<?>, Set<Subscriber>> entry : listenerMethods.entrySet()) {
            Class<?> eventType = entry.getKey();
            Collection<Subscriber> eventMethodsInListener = entry.getValue();

            CopyOnWriteArraySet<Subscriber> eventSubscribers = this.subscribers.computeIfAbsent(eventType, key -> new CopyOnWriteArraySet<>());
            eventSubscribers.addAll(eventMethodsInListener);
        }
    }

    void unregister(Object listener) {
        ConcurrentMap<Class<?>, Set<Subscriber>> listenerMethods = findAllSubscribers(listener);

        for (Map.Entry<Class<?>, Set<Subscriber>> entry : listenerMethods.entrySet()) {
            Class<?> eventType = entry.getKey();
            Collection<Subscriber> listenerMethodsForType = entry.getValue();

            CopyOnWriteArraySet<Subscriber> currentSubscribers = subscribers.get(eventType);
            if (currentSubscribers == null || !currentSubscribers.removeAll(listenerMethodsForType)) {
                throw new IllegalArgumentException(
                        "missing event subscriber for an annotated method. Is " + listener + " registered?");
            }
        }
    }

    Iterator<Subscriber> getSubscribers(Object event) {
        Set<Class<?>> eventTypes = flattenHierarchy(event.getClass());

        List<Iterator<Subscriber>> subscriberIterators = new ArrayList(eventTypes.size());

        for (Class<?> eventType : eventTypes) {
            CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);
            if (eventSubscribers != null) {
                subscriberIterators.add(eventSubscribers.iterator());
            }
        }
        List<Subscriber> collect = subscriberIterators.stream().flatMap(iters -> {
            List<Subscriber> subscribers = new ArrayList<>();
            while (iters.hasNext()) {
                subscribers.add(iters.next());
            }
            return subscribers.stream();
        }).collect(Collectors.toList());
        return collect.iterator();
    }


    private static final ConcurrentMap<Class<?>, List<Method>> subscriberMethodsMap = new ConcurrentHashMap<>();

    private ConcurrentMap<Class<?>, Set<Subscriber>> findAllSubscribers(Object listener) {
        ConcurrentMap<Class<?>, Set<Subscriber>> methodsInListener = new ConcurrentHashMap<>();
        Class<?> clazz = listener.getClass();
        for (Method method : getAnnotatedMethods(clazz)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> eventType = parameterTypes[0];
            Set<Subscriber> subscribers = methodsInListener.computeIfAbsent(eventType, key -> new HashSet<>());
            subscribers.add(Subscriber.create(bus, listener, method));
        }
        return methodsInListener;
    }

    private static List<Method> getAnnotatedMethods(Class<?> clazz) {
        List<Method> methods = subscriberMethodsMap.get(clazz);
        if (methods != null) {
            return methods;
        }
        List<Method> annotatedMethodsNotCached = getAnnotatedMethodsNotCached(clazz);
        subscriberMethodsMap.put(clazz, annotatedMethodsNotCached);
        return annotatedMethodsNotCached;
    }

    private static List<Method> getAnnotatedMethodsNotCached(Class<?> clazz) {
        Map<MethodIdentifier, Method> identifiers = new HashMap<>(1 << 3);
        for (Method method : ReflectUtils.getMethods(clazz)) {
            if (method.isAnnotationPresent(Subscribe.class) && !method.isSynthetic()) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new IllegalArgumentException("Method {} has @Subscribe annotation but has {} parameters."
                            + "Subscriber methods must have exactly 1 parameter.");
                }
                MethodIdentifier ident = new MethodIdentifier(method);
                if (!identifiers.containsKey(ident)) {
                    identifiers.put(ident, method);
                }
            }
        }
        return identifiers.values().stream().collect(Collectors.toList());
    }


    private static final ConcurrentMap<Class<?>, Set<Class<?>>> flattenHierarchyMap = new ConcurrentHashMap<>();

    static Set<Class<?>> flattenHierarchy(Class<?> concreteClass) {
        Set<Class<?>> classes = flattenHierarchyMap.get(concreteClass);
        if (classes != null) {
            return classes;
        }
        classes = TypeUtils.flattenHierarchy(concreteClass);
        flattenHierarchyMap.put(concreteClass, classes);
        return classes;
    }

    private static final class MethodIdentifier {

        private final String name;
        private final List<Class<?>> parameterTypes;

        MethodIdentifier(Method method) {
            this.name = method.getName();
            this.parameterTypes = Arrays.asList(method.getParameterTypes());
        }

        @Override
        public int hashCode() {
            Object[] objects = new Object[]{name, parameterTypes};
            return Arrays.hashCode(objects);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MethodIdentifier) {
                MethodIdentifier ident = (MethodIdentifier) o;
                return name.equals(ident.name) && parameterTypes.equals(ident.parameterTypes);
            }
            return false;
        }
    }
}