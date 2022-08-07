package com.alibaba.ageiport.processor.core.task.exporter.model;

import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
@Setter
@Getter
public class ExportTaskRuntimeConfigImpl implements ExportTaskRuntimeConfig {

    private String executeType;

    private String taskSliceStrategy;

    private Integer no;

    private Integer pageSize;

    private Integer pageOffset;

    private Integer totalCount;

    private String fileType;

    private List<ColumnHeader> columnHeaders;

    private Map<String, String> attributes;
}
