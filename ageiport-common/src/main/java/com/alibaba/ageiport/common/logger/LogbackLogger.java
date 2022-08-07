package com.alibaba.ageiport.common.logger;

/**
 * Logback
 *
 * @author lingyi
 */
public class LogbackLogger implements Logger {
    private final ch.qos.logback.classic.Logger logger;

    public LogbackLogger(ch.qos.logback.classic.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String message, Throwable t) {
        logger.debug(message, t);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    @Override
    public void info(String message, Throwable t) {
        logger.info(message, t);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void warn(String message, Throwable t) {
        logger.warn(message, t);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(Throwable t) {
        logger.error("", t);
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(message, args);
    }
}
