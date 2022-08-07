package com.alibaba.ageiport.processor.core.task.importer.model;

import java.util.List;

/**
 * @author lingyi
 */
public interface BizImportResult<VIEW, DATA> {

    List<VIEW> getView();

    List<DATA> getData();

}
