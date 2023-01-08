package com.alibaba.ageiport.processor.core.model.core;

import java.io.Serializable;
import java.util.List;

/**
 * @author lingyi
 */
public interface ColumnHeaders extends Serializable {

    List<ColumnHeader> getColumnHeaders();

    ColumnHeader getColumnHeaderByFieldName(String fieldName);

    ColumnHeader getColumnHeaderByHeaderName(String headerName);

    ColumnHeader getHeaderByIndex(Integer index);

    Integer getHeaderRowCount();

    void fromJson(String json);

    String toJson();
}
