package com.alibaba.ageiport.processor.core.spi.service;

/**
 * @author lingyi
 */
public interface TaskService {

    TaskExecuteResult executeTask(TaskExecuteParam param);

    TaskProgressResult getTaskProgress(GetTaskProgressParam param);

}
