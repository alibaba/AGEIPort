package com.alibaba.ageiport.processor.core.task.exporter.context;

import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.task.AbstractSubTaskContext;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskSpecification;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class ExportSubTaskContextImpl<QUERY, DATA, VIEW> extends AbstractSubTaskContext<QUERY, DATA, VIEW> implements ExportSubTaskContext<QUERY, DATA, VIEW> {

    private Class<QUERY> queryClass;

    private Class<DATA> dataClass;

    private Class<VIEW> viewClass;

    private QUERY query;

    private ExportTaskRuntimeConfig exportTaskRuntimeConfig;

    private ColumnHeaders columnHeaders;


    @Override
    public ExportTaskSpecification<QUERY, DATA, VIEW> getExportTaskSpec() {
        return (ExportTaskSpecification<QUERY, DATA, VIEW>) getTaskSpec();
    }
}
