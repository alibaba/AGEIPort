package com.alibaba.ageiport.common.utils;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;

class StringUtilsTest {

    static Logger logger = LoggerFactory.getLogger(StringUtilsTest.class);

    @org.junit.jupiter.api.Test
    void format() {
        logger.info(StringUtils.format("{},{}", 1, 2));
        logger.info((StringUtils.format("{},{}", ArrayUtils.of(1, 2))));
    }
}
