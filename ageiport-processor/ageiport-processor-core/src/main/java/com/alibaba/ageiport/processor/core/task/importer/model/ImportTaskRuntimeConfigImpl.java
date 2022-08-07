package com.alibaba.ageiport.processor.core.task.importer.model;

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
public class ImportTaskRuntimeConfigImpl implements ImportTaskRuntimeConfig {

    private String executeType;

    private String taskSliceStrategy;

    private Integer no;

    private Integer pageSize;

    private String fileType;

    private List<ColumnHeader> columnHeaders;

    private Map<String, String> attributes;
}
