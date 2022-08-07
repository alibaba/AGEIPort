package com.alibaba.ageiport.processor.core.eventbus.local;

import com.alibaba.ageiport.processor.core.spi.eventbus.EventBusOptions;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class LocalEventBusOptions implements EventBusOptions {

    private int corePoolSize = 2;

    private int maxPoolSize = 4;

    private int queueSize = 10240;

    @Override
    public String type() {
        return LocalEventBusFactory.class.getSimpleName();
    }
}
