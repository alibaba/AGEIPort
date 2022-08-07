package com.alibaba.ageiport.processor.core.spi.task.stage;

import com.alibaba.ageiport.ext.arch.SPI;


/**
 * @author lingyi
 */
@SPI
public interface MainTaskStageProvider extends StageProvider {
    Stage mainTaskCreated();

    Stage mainTaskDispatchStart();

    Stage mainTaskDispatchEnd();

    Stage mainTaskStart();

    Stage mainTaskSliceStart();

    Stage mainTaskSliceEnd();

    Stage mainTaskSaveSliceStart();

    Stage mainTaskSaveSliceEnd();

    Stage subTaskDispatchStart();

    Stage subTaskDispatchEnd();

    Stage subTaskExecuteStart();

    Stage subTaskExecuteEnd();

    Stage mainTaskReduceStart();

    Stage mainTaskReduceEnd();

    Stage mainTaskFinished();

    Stage mainTaskError();
}
