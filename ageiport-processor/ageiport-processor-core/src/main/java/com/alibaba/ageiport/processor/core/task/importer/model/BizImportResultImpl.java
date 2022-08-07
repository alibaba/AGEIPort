package com.alibaba.ageiport.processor.core.task.importer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class BizImportResultImpl<VIEW, DATA> implements BizImportResult<VIEW, DATA> {

    List<VIEW> view;

    List<DATA> data;

}
