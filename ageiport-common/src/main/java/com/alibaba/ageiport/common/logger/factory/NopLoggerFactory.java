package com.alibaba.ageiport.common.logger.factory;


import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.NopLogger;

/**
 * no provider
 *
 * @author xuechao.sxc
 */
public class NopLoggerFactory implements LoggerFactory {

    @Override
    public Logger getLogger(String name) {
        return new NopLogger(name);
    }
}
