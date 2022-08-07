package com.alibaba.ageiport.processor.core.spi.eventbus;

import java.util.EventListener;
import java.util.EventObject;

public interface Listener<E extends EventObject> extends EventListener {
    void handle(E event);
}
