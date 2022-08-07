package com.alibaba.ageiport.processor.core.spi.task.factory;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;

/**
 * @author lingyi
 */
@SPI
public interface MainTaskWorkerFactory {

    MainTaskWorker create(AgeiPort ageiPort, MainTask mainTask);

}
