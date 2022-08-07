package com.alibaba.ageiport.common.logger.factory;

import com.alibaba.ageiport.common.constants.ConstValues;
import com.alibaba.ageiport.common.logger.Log4jLogger;
import com.alibaba.ageiport.common.logger.Logger;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;

import java.net.URL;

/**
 * Log4j
 *
 * @author xuechao.sxc
 */
public class Log4jLoggerFactory implements LoggerFactory {

    private static LoggerRepository repository;

    public Log4jLoggerFactory() throws Exception {
        Class.forName("org.apache.log4j.Level");

        repository = new Hierarchy(new RootLogger(Level.DEBUG));

        URL url = Log4j2LoggerFactory.class.getResource("/log4j-" + ConstValues.PROJECT_LOWER_KEY + ".xml");

        new DOMConfigurator().doConfigure(url, repository);
    }

    @Override
    public Logger getLogger(String name) {
        return new Log4jLogger(repository.getLogger(name));
    }
}
