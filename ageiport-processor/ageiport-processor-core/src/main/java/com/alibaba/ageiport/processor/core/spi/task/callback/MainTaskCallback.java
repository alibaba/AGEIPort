package com.alibaba.ageiport.processor.core.spi.task.callback;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;

@SPI
public interface MainTaskCallback {

    void afterCreated(MainTask mainTask);

    void beforeFinished(MainTask mainTask);

    void afterFinished(MainTask mainTask);

    void beforeError(MainTask mainTask);

    void afterError(MainTask mainTask);
}
