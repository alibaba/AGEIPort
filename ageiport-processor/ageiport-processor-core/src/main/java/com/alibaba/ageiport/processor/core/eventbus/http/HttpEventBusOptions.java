package com.alibaba.ageiport.processor.core.eventbus.http;

import com.alibaba.ageiport.processor.core.spi.eventbus.EventBusOptions;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */

@Getter
@Setter
public class HttpEventBusOptions implements EventBusOptions {
    @Override
    public String type() {
        return HttpEventBusFactory.class.getSimpleName();
    }

    private Integer port = 9742;

    private int eventHandleCorePoolSize = 2;

    private int eventHandleMaxPoolSize = 4;

    private int eventHandleQueueSize = 10240;


}
