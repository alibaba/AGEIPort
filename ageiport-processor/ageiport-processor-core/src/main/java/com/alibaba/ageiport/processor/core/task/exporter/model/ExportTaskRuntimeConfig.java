package com.alibaba.ageiport.processor.core.task.exporter.model;

import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.TaskRuntimeConfig;

import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */

public interface ExportTaskRuntimeConfig extends TaskRuntimeConfig {

    Integer getNo();

    Integer getPageOffset();

    Integer getPageSize();

    Integer getTotalCount();

    String getFileType();

    List<ColumnHeader> getColumnHeaders();

    Map<String, String> getAttributes();

}
