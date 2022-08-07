package com.alibaba.ageiport.processor.core.spi.task.stage;

import com.alibaba.ageiport.ext.arch.SPI;

/**
 * @author lingyi
 */
@SPI
public interface SubTaskStageProvider extends StageProvider{
    Stage subTaskCreated();

    Stage subTaskDispatchedOnNode();

    Stage subTaskStart();

    Stage subTaskFinished();

    Stage subTaskError();

}
