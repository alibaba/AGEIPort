package com.alibaba.ageiport.processor.core.spi.task.monitor;

/**
 * @author lingyi
 */
public interface TaskProgressService {

    void updateTaskProgress(TaskStageEvent event);

    MainTaskProgress getTaskProgress(String mainTaskId);

    MainTaskProgress removeTaskProgress(String mainTaskId);
}
