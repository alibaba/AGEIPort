package com.alibaba.ageiport.common.logger;

/**
 * Log4jLogger
 *
 * @author lingyi
 */
public class Log4jLogger implements Logger {
    private final org.apache.log4j.Logger logger;

    public Log4jLogger(org.apache.log4j.Logger logger) {
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
        String msg = formatString(message, args);
        if (args != null) {
            if (args[args.length - 1] instanceof Throwable) {
                logger.debug(msg, (Throwable) args[args.length - 1]);
                return;
            }
        }
        logger.debug(msg);
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
        String msg = formatString(message, args);
        if (args != null) {
            if (args[args.length - 1] instanceof Throwable) {
                logger.info(msg, (Throwable) args[args.length - 1]);
                return;
            }
        }
        logger.info(msg);
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
        String msg = formatString(message, args);
        if (args != null) {
            if (args[args.length - 1] instanceof Throwable) {
                logger.warn(msg, (Throwable) args[args.length - 1]);
                return;
            }
        }
        logger.warn(msg);
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
        logger.error(t);
    }

    @Override
    public void error(String message, Object... args) {
        String msg = formatString(message, args);
        if (args != null) {
            if (args[args.length - 1] instanceof Throwable) {
                logger.error(msg, (Throwable) args[args.length - 1]);
                return;
            }
        }
        logger.error(msg);
    }

    private static String formatString(String message, Object... args) {
        StringBuilder builder = new StringBuilder();
        int argsIndex = 0;
        for (int index = 0; index < message.length(); index++) {
            if (message.charAt(index) == '{') {
                if ((index + 1) < message.length() && message.charAt(index + 1) == '}' && argsIndex < args.length) {
                    builder.append(args[argsIndex]);
                    index++;
                    argsIndex++;
                    continue;
                }
            }

            builder.append(message.charAt(index));
        }

        return builder.toString();
    }
}
