package com.alibaba.ageiport.processor.core.spi.task.acceptor;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;

/**
 * @author lingyi
 */
@SPI
public interface TaskAcceptorFactory {

    TaskAcceptor create(AgeiPort ageiPort);

}
