package com.alibaba.ageiport.processor.core.task.importer.api;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class BizImportTaskRuntimeConfigImpl implements BizImportTaskRuntimeConfig {

    private static final long serialVersionUID = 7699460813867423756L;

    private Integer pageSize;

    private String executeType;

    private String taskSliceStrategy;

    private String fileType;
}
