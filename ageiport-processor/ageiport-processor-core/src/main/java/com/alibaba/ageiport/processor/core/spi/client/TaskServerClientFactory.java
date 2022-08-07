package com.alibaba.ageiport.processor.core.spi.client;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;

/**
 * @author lingyi
 */
@SPI
public interface TaskServerClientFactory {

    TaskServerClient taskServerClient(AgeiPort ageiPort, TaskServerClientOptions options);

}
