package com.alibaba.ageiport.processor.core.model.core.impl;

import com.alibaba.ageiport.common.feature.FeatureUtils;
import com.alibaba.ageiport.processor.core.Processor;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.constants.TaskSpecificationFeatureKeys;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class TaskSpecHolder implements TaskSpec {

    private TaskSpecification taskSpecification;

    private Processor processor;

    @Override
    public String getExecuteType() {
        return taskSpecification.getTaskExecuteType();
    }

    @Override
    public String getTaskType() {
        return taskSpecification.getTaskType();
    }

    @Override
    public String getTaskCode() {
        return taskSpecification.getTaskCode();
    }

    @Override
    public Processor getProcessor() {
        return processor;
    }

    @Override
    public Class<?> getProcessorClass() {
        return processor.getClass();
    }

    @Override
    public String getTaskName() {
        return taskSpecification.getTaskName();
    }

    @Override
    public String getTaskDesc() {
        return taskSpecification.getTaskDesc();
    }

    @Override
    public TaskSpecification getTaskSpecification() {
        return taskSpecification;
    }

    @Override
    public String getTaskSliceStrategy() {
        return FeatureUtils.getFeature(taskSpecification.getFeature(), TaskSpecificationFeatureKeys.TASK_SLICE_STRATEGY);
    }


}
