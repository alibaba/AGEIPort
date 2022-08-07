package com.alibaba.ageiport.processor.core.spi.task.specification;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.Processor;
import com.alibaba.ageiport.processor.core.TaskSpec;

/**
 * @author lingyi
 */
@SPI
public interface TaskSpecificationResolver {
    TaskSpec resolve(Processor processor);
}
