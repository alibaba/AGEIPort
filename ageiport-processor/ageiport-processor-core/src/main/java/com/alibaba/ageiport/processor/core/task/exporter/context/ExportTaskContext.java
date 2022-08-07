package com.alibaba.ageiport.processor.core.task.exporter.context;

import com.alibaba.ageiport.processor.core.spi.task.factory.TaskContext;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskSpecification;

/**
 * @author lingyi
 */
public interface ExportTaskContext<QUERY, DATA, VIEW> extends TaskContext {


    ExportTaskSpecification<QUERY, DATA, VIEW> getExportTaskSpec();

    Class<QUERY> getQueryClass();


    Class<DATA> getDataClass();


    Class<VIEW> getViewClass();


    QUERY getQuery();

    ExportTaskRuntimeConfig getExportTaskRuntimeConfig();


}
