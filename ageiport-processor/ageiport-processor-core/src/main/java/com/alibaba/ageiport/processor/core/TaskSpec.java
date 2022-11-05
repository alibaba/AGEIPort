package com.alibaba.ageiport.processor.core;

import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecification;

/**
 * @author lingyi
 */
public interface TaskSpec {

    String getExecuteType();

    String getTaskType();

    String getTaskCode();

    Processor getProcessor();

    Class<?> getProcessorClass();

    String getTaskName();

    String getTaskDesc();

    TaskSpecification getTaskSpecification();

    String getTaskSliceStrategy();
}
