package com.alibaba.ageiport.processor.core.spi.task.specification;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;

import java.util.List;

/**
 * @author lingyi
 */
@SPI
public interface TaskSpecificationProvider {

    List<TaskSpec> provide(AgeiPort ageiPort);

}
