package com.alibaba.ageiport.common.logger;


import com.alibaba.ageiport.common.logger.factory.*;
import com.alibaba.ageiport.common.logger.factory.Log4j2LoggerFactory;
import com.alibaba.ageiport.common.logger.factory.Log4jLoggerFactory;
import com.alibaba.ageiport.common.logger.factory.LogbackLoggerFactory;
import com.alibaba.ageiport.common.logger.factory.NopLoggerFactory;
import com.alibaba.ageiport.common.logger.support.FailsafeLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LoggerFactory
 *
 * @author lingyi
 */
public class LoggerFactory {
    private static com.alibaba.ageiport.common.logger.factory.LoggerFactory LOGGER_FACTORY;
    private static Map<String, Logger> loggerCache;

    public static Logger getLogger(String name) {
        Logger logger = loggerCache.get(name);
        if (logger == null) {
            synchronized (LOGGER_FACTORY) {
                logger = loggerCache.get(name);
                if (logger == null) {
                    logger = LOGGER_FACTORY.getLogger(name);
                    loggerCache.put(name, new FailsafeLogger(logger));
                }
            }
        }

        return logger;
    }

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }


    // 查找常用的日志框架
    static {
        try {
            LOGGER_FACTORY = new LogbackLoggerFactory();
            LogLog.info("GEI init JM logger with LogbackLoggerFactory success, " + LoggerFactory.class.getClassLoader());
        } catch (Throwable e1) {
            try {
                LOGGER_FACTORY = new Log4jLoggerFactory();
                LogLog.info("GEI init JM logger with Log4jLoggerFactory success, " + LoggerFactory.class.getClassLoader());
            } catch (Throwable e2) {
                try {
                    LOGGER_FACTORY = new Log4j2LoggerFactory();
                    LogLog.info("GEI init JM logger with Log4j2LoggerFactory success, " + LoggerFactory.class.getClassLoader());
                } catch (Throwable e3) {
                    LOGGER_FACTORY = new NopLoggerFactory();
                    LogLog.warn("GEI init JM logger with NopLoggerFactory, pay attention. "
                        + LoggerFactory.class.getClassLoader(), e2);
                }
            }
        }

        loggerCache = new ConcurrentHashMap<>();
    }
}
