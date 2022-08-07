package com.alibaba.ageiport.processor.core.task;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskWorker;

/**
 * @author lingyi
 */
public abstract class AbstractMainTaskWorker implements MainTaskWorker {

    protected boolean isReduce;

    protected AgeiPort ageiPort;

    protected MainTask mainTask;

    @Override
    public AgeiPort getAgei() {
        return ageiPort;
    }

    @Override
    public MainTask getMainTask() {
        return mainTask;
    }

    @Override
    public boolean isReduce() {
        return isReduce;
    }

    @Override
    public void isReduce(boolean isReduce) {
        this.isReduce = isReduce;
    }

}
