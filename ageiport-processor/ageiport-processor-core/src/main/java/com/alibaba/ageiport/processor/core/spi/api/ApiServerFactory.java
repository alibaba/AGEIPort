package com.alibaba.ageiport.processor.core.spi.api;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;

/**
 * @author lingyi
 */
@SPI
public interface ApiServerFactory {

    ApiServer create(AgeiPort ageiPort, ApiServerOptions options);

}
