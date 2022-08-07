/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.ageiport.common.logger.support;

import com.alibaba.ageiport.common.logger.Logger;

/**
 * FailsafeLogger
 *
 * @author xuechao.sxc
 */
public class FailsafeLogger implements Logger {

    private Logger logger;

    public FailsafeLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }


    @Override
    public void debug(String message, Throwable t) {
        try {
            logger.debug(message, t);
        } catch (Throwable e) {
        }
    }

    @Override
    public void debug(String message) {
        try {
            logger.debug(message);
        } catch (Throwable e) {
        }
    }

    @Override
    public void debug(String message, Object... args) {
        try {
            logger.debug(message, args);
        } catch (Throwable e) {
        }
    }

    @Override
    public void info(String message, Throwable t) {
        try {
            logger.info(message, t);
        } catch (Throwable e) {
        }
    }

    @Override
    public void info(String message) {
        try {
            logger.info(message);
        } catch (Throwable e) {
        }
    }

    @Override
    public void info(String message, Object... args) {
        try {
            logger.info(message, args);
        } catch (Throwable e) {
        }
    }

    @Override
    public void warn(String message, Throwable t) {
        try {
            logger.warn(message, t);
        } catch (Throwable e) {
        }
    }

    @Override
    public void warn(String message) {
        try {
            logger.warn(message);
        } catch (Throwable e) {
        }
    }

    @Override
    public void warn(String message, Object... args) {
        try {
            logger.warn(message, args);
        } catch (Throwable e) {
        }
    }

    @Override
    public void error(String message, Throwable t) {
        try {
            logger.error(message, t);
        } catch (Throwable e) {
        }
    }

    @Override
    public void error(String message) {
        try {
            logger.error(message);
        } catch (Throwable e) {
        }
    }

    @Override
    public void error(Throwable t) {
        try {
            logger.error(t);
        } catch (Throwable e) {
        }
    }

    @Override
    public void error(String message, Object... args) {
        try {
            logger.error(message, args);
        } catch (Throwable e) {
        }
    }
}
