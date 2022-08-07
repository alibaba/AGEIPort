package com.alibaba.ageiport.common.logger.factory;

import com.alibaba.ageiport.common.constants.ConstValues;
import com.alibaba.ageiport.common.logger.Log4j2Logger;
import com.alibaba.ageiport.common.logger.Logger;
import org.apache.logging.log4j.LogManager;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Log4j2
 *
 * @author lingyi
 */
public class Log4j2LoggerFactory implements LoggerFactory {
    private static org.apache.logging.log4j.spi.LoggerContext LOG_CONTEXT;

    public Log4j2LoggerFactory() throws Exception {
        Class.forName("org.apache.logging.log4j.core.Logger");
        URL url = Log4j2LoggerFactory.class.getResource("/log4j2-" + ConstValues.PROJECT_LOWER_KEY + ".xml");
        LOG_CONTEXT = LogManager.getContext(new MyClassLoader(new URL[]{},
                Thread.currentThread().getContextClassLoader()), false, null, url.toURI());
    }

    @Override
    public Logger getLogger(String name) {
        return new Log4j2Logger(LOG_CONTEXT.getLogger(name));
    }

    public static class MyClassLoader extends URLClassLoader {

        public MyClassLoader(URL[] urls) {
            super(urls);
        }

        public MyClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        public void addJar(URL url) {
            this.addURL(url);
        }

    }
}
