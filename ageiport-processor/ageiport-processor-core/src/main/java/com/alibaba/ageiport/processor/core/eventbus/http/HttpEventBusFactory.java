package com.alibaba.ageiport.processor.core.eventbus.http;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBusFactory;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBusOptions;

/**
 * @author lingyi
 */
public class HttpEventBusFactory implements EventBusFactory {
    @Override
    public EventBus create(AgeiPort ageiPort, EventBusOptions options) {
        return new HttpEventBus(ageiPort, (HttpEventBusOptions) options);
    }
}
