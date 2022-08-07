package com.alibaba.ageiport.processor.core.task.importer.model;

import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;

/**
 * @author lingyi
 */
public interface ImportTaskSpecification<QUERY, DATA, VIEW> extends TaskSpec {

    ImportProcessor<QUERY, DATA, VIEW> getImportProcessor();

    Class<QUERY> getQueryClass();

    Class<DATA> getDataClass();

    Class<VIEW> getViewClass();

    Integer getPageSize();

    String getFileType();
}
