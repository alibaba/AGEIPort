package com.alibaba.ageiport.common.logger;

/**
 * Logger interface
 *
 * @author lingyi
 */
public interface Logger {
    /**
     * @param t
     * @param message
     */
    void debug(String message, Throwable t);

    /**
     * @param message
     */
    void debug(String message);

    /**
     * @param message
     * @param args
     */
    void debug(String message, Object... args);

    /**
     * @param t
     * @param message
     */
    void info(String message, Throwable t);

    /**
     * @param message
     */
    void info(String message);

    /**
     * @param message
     * @param args
     */
    void info(String message, Object... args);

    /**
     * @param t
     * @param message
     */
    void warn(String message, Throwable t);

    /**
     * @param message
     */
    void warn(String message);

    /**
     * @param message
     * @param args
     */
    void warn(String message, Object... args);

    /**
     * @param t
     * @param message
     */
    void error(String message, Throwable t);

    /**
     * @param message
     */
    void error(String message);


    /**
     * @param t
     */
    void error(Throwable t);

    /**
     * @param message
     * @param args
     */
    void error(String message, Object... args);

    default boolean isDebugEnabled() {
        return false;
    }
}
