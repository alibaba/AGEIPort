package com.alibaba.ageiport.processor.core.dispatcher.local;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.dispatcher.Dispatcher;
import com.alibaba.ageiport.processor.core.spi.dispatcher.DispatcherFactory;
import com.alibaba.ageiport.processor.core.spi.dispatcher.DispatcherOptions;

/**
 * @author lingyi
 */
public class LocalDispatcherFactory implements DispatcherFactory {
    @Override
    public Dispatcher create(AgeiPort ageiPort, DispatcherOptions dispatcherOptions) {
        return new LocalDispatcher(ageiPort, (LocalDispatcherOptions) dispatcherOptions);
    }
}
