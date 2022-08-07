package com.alibaba.ageiport.processor.core.spi.task.specification;

import com.alibaba.ageiport.processor.core.TaskSpec;

/**
 * @author lingyi
 */
public interface TaskSpecificationRegistry {

    void add(TaskSpec taskSpec);

    TaskSpec get(String taskCode);
}
