package com.alibaba.ageiport.processor.core.task.importer.api;

import java.io.Serializable;

/**
 * @author lingyi
 */
public interface BizImportTaskRuntimeConfig extends Serializable {
    Integer getPageSize();

    String getExecuteType();

    String getTaskSliceStrategy();

    String getFileType();
}
