package com.alibaba.ageiport.processor.core.spi.dispatcher;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;

/**
 * @author lingyi
 */
@SPI
public interface DispatcherFactory {

    Dispatcher create(AgeiPort ageiPort, DispatcherOptions dispatcherOptions);

}
