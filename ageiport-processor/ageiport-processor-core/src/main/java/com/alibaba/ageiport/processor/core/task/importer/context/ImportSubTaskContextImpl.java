package com.alibaba.ageiport.processor.core.task.importer.context;

import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.task.AbstractSubTaskContext;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskSpecification;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class ImportSubTaskContextImpl<QUERY, DATA, VIEW> extends AbstractSubTaskContext<QUERY, DATA, VIEW> implements ImportSubTaskContext<QUERY, DATA, VIEW> {

    private Class<QUERY> queryClass;

    private Class<DATA> dataClass;

    private Class<VIEW> viewClass;

    private QUERY query;

    private ImportTaskRuntimeConfig ImportTaskRuntimeConfig;

    private ColumnHeaders columnHeaders;

    private DataGroup dataGroup;


    @Override
    public ImportTaskSpecification<QUERY, DATA, VIEW> getImportTaskSpec() {
        return (ImportTaskSpecification<QUERY, DATA, VIEW>) getTaskSpec();
    }
}
