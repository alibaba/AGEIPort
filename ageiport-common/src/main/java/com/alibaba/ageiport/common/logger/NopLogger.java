package com.alibaba.ageiport.common.logger;

import com.alibaba.ageiport.common.exception.ExceptionUtils;
import com.alibaba.ageiport.common.utils.StringUtils;

import java.util.Date;

/**
 * NopLogger
 *
 * @author lingyi
 */
public class NopLogger implements Logger {

    private String name;

    private static String DEBUG = "DEBUG";
    private static String INFO = "INFO";
    private static String WARN = "WARN";
    private static String ERROR = "ERROR";

    public NopLogger(String name) {
        this.name = name;
    }

    private String getFixPrefix(String tag) {
        return "NopLogger " + tag + " - " + name + " - " + new Date() + " - ";
    }

    @Override
    public void debug(String message, Throwable t) {
        System.out.println(getFixPrefix(DEBUG) + message);
        System.out.println(ExceptionUtils.stackOf(t));
    }

    @Override
    public void debug(String message) {
        System.out.println(getFixPrefix(DEBUG) + message);
    }

    @Override
    public void debug(String message, Object... args) {
        System.out.println(getFixPrefix(DEBUG) + StringUtils.format(message, args));
    }

    @Override
    public void info(String message, Throwable t) {
        System.out.println(getFixPrefix(INFO) + message);
        System.out.println(ExceptionUtils.stackOf(t));
    }

    @Override
    public void info(String message) {
        System.out.println(getFixPrefix(INFO) + message);
    }

    @Override
    public void info(String message, Object... args) {
        System.out.println(getFixPrefix(INFO) + StringUtils.format(message, args));
    }

    @Override
    public void warn(String message, Throwable t) {
        System.out.println(getFixPrefix(WARN) + message);
        System.out.println(ExceptionUtils.stackOf(t));
    }

    @Override
    public void warn(String message) {
        System.out.println(getFixPrefix(WARN) + message);
    }

    @Override
    public void warn(String message, Object... args) {
        System.out.println(getFixPrefix(WARN) + StringUtils.format(message, args));
    }

    @Override
    public void error(String message, Throwable t) {
        System.out.println(getFixPrefix(ERROR) + message);
        System.out.println(ExceptionUtils.stackOf(t));
    }

    @Override
    public void error(String message) {
        System.out.println(getFixPrefix(ERROR) + message);
    }

    @Override
    public void error(Throwable t) {
        System.out.println(getFixPrefix(ERROR));
        System.out.println(ExceptionUtils.stackOf(t));
    }

    @Override
    public void error(String message, Object... args) {
        System.out.println(getFixPrefix(ERROR) + StringUtils.format(message, args));
    }
}
