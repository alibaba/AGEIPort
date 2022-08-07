package com.alibaba.ageiport.processor.core.spi.eventbus;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @author lingyi
 */
public interface EventBus {

    void register(EventListener listener);

    void unregister(EventListener listener);

    void post(EventObject event);
}
