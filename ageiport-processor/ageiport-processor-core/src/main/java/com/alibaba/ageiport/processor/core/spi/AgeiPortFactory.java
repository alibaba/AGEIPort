package com.alibaba.ageiport.processor.core.spi;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;

/**
 * @author lingyi
 */
@SPI("AgeiPortFactoryImpl")
public interface AgeiPortFactory {

    AgeiPort ageiPort(AgeiPortOptions options);

}
