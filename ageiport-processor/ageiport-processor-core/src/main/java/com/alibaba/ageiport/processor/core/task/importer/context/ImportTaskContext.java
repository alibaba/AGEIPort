package com.alibaba.ageiport.processor.core.task.importer.context;

import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.task.factory.TaskContext;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskSpecification;

/**
 * @author lingyi
 */
public interface ImportTaskContext<QUERY, DATA, VIEW> extends TaskContext {


    ImportTaskSpecification<QUERY, DATA, VIEW> getImportTaskSpec();

    Class<QUERY> getQueryClass();


    Class<DATA> getDataClass();


    Class<VIEW> getViewClass();


    QUERY getQuery();

    DataGroup getDataGroup();

    ImportTaskRuntimeConfig getImportTaskRuntimeConfig();


}
