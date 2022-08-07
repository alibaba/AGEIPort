package com.alibaba.ageiport.processor.core.model.core;

import org.jetbrains.annotations.NotNull;

/**
 * @author lingyi
 */
public interface ColumnHeader extends Comparable<ColumnHeader> {

    Integer getIndex();

    void setIndex(Integer headerName);

    String getHeaderName();

    void setHeaderName(String headerName);

    String getFieldName();

    void setFieldName(String fieldName);

    String getType();

    void setType(String type);

    Boolean getDynamicColumn();

    void setDynamicColumn(Boolean dynamicColumn);

    String getDynamicColumnKey();

    void setDynamicColumnKey(String dynamicColumnKey);

    void setGroupName(String groupName);

    String getGroupName();

    void setGroupIndex(Integer groupIndex);

    Integer getGroupIndex();

    void setErrorHeader(Boolean errorHeader);

    Boolean getErrorHeader();

    Boolean getIgnoreHeader();

    void setIgnoreHeader(Boolean ignoreHeader);

    Boolean getRequired();

    void setRequired(Boolean required);


    @Override
    default int compareTo(@NotNull ColumnHeader o) {
        return this.getIndex().compareTo(o.getIndex());
    }
}
