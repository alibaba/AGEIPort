package com.alibaba.ageiport.processor.core.task.mapreduce.api;

import java.io.Serializable;

/**
 * @author lingyi
 */
public interface BizMapReduceTaskRuntimeConfig extends Serializable {

    String getExecuteType();

    String getOutputFileType();
}
