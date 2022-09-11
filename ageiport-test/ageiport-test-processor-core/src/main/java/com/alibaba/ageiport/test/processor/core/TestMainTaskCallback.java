package com.alibaba.ageiport.test.processor.core;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.task.callback.MainTaskCallback;

public class TestMainTaskCallback implements MainTaskCallback {

    private static Logger logger = LoggerFactory.getLogger(TestMainTaskCallback.class);

    @Override
    public void afterCreated(MainTask mainTask) {
        logger.info("---afterCreated:{}", mainTask.getMainTaskId());
    }

    @Override
    public void beforeFinished(MainTask mainTask) {
        logger.info("---beforeFinished:{}", mainTask.getMainTaskId());
    }

    @Override
    public void afterFinished(MainTask mainTask) {
        logger.info("---afterFinished:{}", mainTask.getMainTaskId());
    }

    @Override
    public void beforeError(MainTask mainTask) {
        logger.info("---beforeError:{}", mainTask.getMainTaskId());
    }

    @Override
    public void afterError(MainTask mainTask) {
        logger.info("---afterError:{}", mainTask.getMainTaskId());
    }
}
