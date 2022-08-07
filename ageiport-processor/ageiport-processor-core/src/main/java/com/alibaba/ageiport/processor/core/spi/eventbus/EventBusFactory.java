package com.alibaba.ageiport.processor.core.spi.eventbus;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;

/**
 * @author lingyi
 */
@SPI
public interface EventBusFactory {

    EventBus create(AgeiPort ageiPort, EventBusOptions options);

}
