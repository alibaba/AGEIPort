package com.alibaba.ageiport.processor.core.spi.task.factory;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;

/**
 * @author lingyi
 */
@SPI
public interface SubTaskContextFactory {

    SubTaskContext create(AgeiPort ageiPort, String subTaskId);

}
