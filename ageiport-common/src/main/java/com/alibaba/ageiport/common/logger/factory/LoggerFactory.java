package com.alibaba.ageiport.common.logger.factory;

import com.alibaba.ageiport.common.logger.Logger;

/**
 * LoggerFactory接口
 *
 * @author lingyi
 */
//@SPI
public interface LoggerFactory {
    Logger getLogger(String name);
}
