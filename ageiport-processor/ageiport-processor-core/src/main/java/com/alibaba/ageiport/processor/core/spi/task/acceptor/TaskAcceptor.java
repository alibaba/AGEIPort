package com.alibaba.ageiport.processor.core.spi.task.acceptor;

import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;

/**
 * @author lingyi
 */
public interface TaskAcceptor {

    void accept(MainTask mainTask);

}
