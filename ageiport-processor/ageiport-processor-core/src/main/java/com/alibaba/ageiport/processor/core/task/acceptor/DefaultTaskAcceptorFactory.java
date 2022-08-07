package com.alibaba.ageiport.processor.core.task.acceptor;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.task.acceptor.TaskAcceptor;
import com.alibaba.ageiport.processor.core.spi.task.acceptor.TaskAcceptorFactory;

/**
 * @author lingyi
 */
public class DefaultTaskAcceptorFactory implements TaskAcceptorFactory {
    @Override
    public TaskAcceptor create(AgeiPort ageiPort) {
        return new DefaultTaskAcceptor(ageiPort);
    }
}
