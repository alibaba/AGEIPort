package com.alibaba.ageiport.processor.core.eventbus.local.async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an event subscriber.
 *
 * <p>The type of event will be indicated by the method's first (and only) parameter. If this
 * annotation is applied to methods with zero parameters, or more than one parameter, the object
 * containing the method will not be able to register for event delivery from the {@link EventBus}.
 *
 * @author Cliff Biffle
 * @since 10.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
}