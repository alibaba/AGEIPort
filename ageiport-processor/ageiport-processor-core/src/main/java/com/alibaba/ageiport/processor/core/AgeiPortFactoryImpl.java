package com.alibaba.ageiport.processor.core;

import com.alibaba.ageiport.processor.core.spi.AgeiPortFactory;

/**
 * @author lingyi
 */
public class AgeiPortFactoryImpl implements AgeiPortFactory {

    @Override
    public AgeiPort ageiPort(AgeiPortOptions options) {
        return AgeiPortImpl.agei(options);
    }
}
