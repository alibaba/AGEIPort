package com.alibaba.ageiport.processor.core.dispatcher.http;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.dispatcher.Dispatcher;
import com.alibaba.ageiport.processor.core.spi.dispatcher.DispatcherFactory;
import com.alibaba.ageiport.processor.core.spi.dispatcher.DispatcherOptions;

/**
 * @author lingyi
 */
public class HttpDispatcherFactory implements DispatcherFactory {
    @Override
    public Dispatcher create(AgeiPort ageiPort, DispatcherOptions options) {
        return new HttpDispatcher(ageiPort, (HttpDispatcherOptions) options);
    }
}
