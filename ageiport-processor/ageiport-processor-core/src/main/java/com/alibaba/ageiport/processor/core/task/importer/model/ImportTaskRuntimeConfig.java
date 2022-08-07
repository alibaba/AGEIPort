package com.alibaba.ageiport.processor.core.task.importer.model;

import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.TaskRuntimeConfig;

import java.util.List;

/**
 * @author lingyi
 */

public interface ImportTaskRuntimeConfig extends TaskRuntimeConfig {

    Integer getPageSize();

    String getFileType();

    List<ColumnHeader> getColumnHeaders();

}
