package com.alibaba.ageiport.processor.core.spi.task.factory;

import com.alibaba.ageiport.processor.core.Context;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;

/**
 * @author lingyi
 */
public interface SubTaskWorker extends Runnable, Context {
    SubTask getSubTask();

    void doMappingProcess();

    @Override
    default void run() {
        doMappingProcess();
    }
}
