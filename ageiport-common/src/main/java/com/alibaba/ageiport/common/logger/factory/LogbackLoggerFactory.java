package com.alibaba.ageiport.common.logger.factory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.alibaba.ageiport.common.constants.ConstValues;
import com.alibaba.ageiport.common.logger.LogbackLogger;
import com.alibaba.ageiport.common.logger.Logger;

import java.net.URL;

/**
 * Logback
 *
 * @author xuechao.sxc
 */
public class LogbackLoggerFactory implements LoggerFactory {
    private static LoggerContext loggerContext;

    public LogbackLoggerFactory() throws Exception {
        loggerContext = new LoggerContext();
        loggerContext.setName("logback-gei");
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);

        URL url = Log4j2LoggerFactory.class.getResource("/logback-" + ConstValues.PROJECT_LOWER_KEY + ".xml");
        configurator.doConfigure(url);
    }

    @Override
    public Logger getLogger(String name) {
        return new LogbackLogger(loggerContext.getLogger(name));
    }
}
