package com.alibaba.ageiport.processor.core.task.exporter.api;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class BizExportTaskRuntimeConfigImpl implements BizExportTaskRuntimeConfig {

    private static final long serialVersionUID = -1687593314028872326L;

    private Integer pageSize;

    private String executeType;

    private String taskSliceStrategy;

    private String fileType;


}
