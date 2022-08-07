package com.alibaba.ageiport.processor.core.spi.task.factory;


import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.StageProvider;

/**
 * @author lingyi
 */
public interface MainTaskContext extends TaskContext {

    MainTaskStageProvider getMainTaskStageProvider();

    default StageProvider getStageProvider() {
        return getMainTaskStageProvider();
    }
}
