package com.alibaba.ageiport.processor.core.model.api;

import java.util.List;

/**
 * @author lingyi
 */
public interface BizColumnHeader {

    String getHeaderName();

    String getFieldName();

    String getDataType();

    boolean isDynamicColumn();

    String getDynamicColumnKey();

    String getType();

    String getGroupName();

    Integer getGroupIndex();

    boolean isErrorHeader();

    boolean isRequired();

    Integer getColumnWidth();

    List<String> getValues();

}
