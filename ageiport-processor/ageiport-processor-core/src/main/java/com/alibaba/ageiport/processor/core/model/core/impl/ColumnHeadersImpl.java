package com.alibaba.ageiport.processor.core.model.core.impl;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public class ColumnHeadersImpl implements ColumnHeaders {

    private static final long serialVersionUID = 6643692043129727292L;

    private List<ColumnHeader> columnHeaders;

    private Map<String, ColumnHeader> fieldNameIndexMap;

    private Map<Integer, ColumnHeader> columnIndexIndexMap;

    private Map<String, ColumnHeader> headerNameKeyIndexMap;

    private Map<List<String>, ColumnHeader> headerNameIndexMap;

    public ColumnHeadersImpl() {
    }

    public ColumnHeadersImpl(List<ColumnHeader> columnHeaders) {
        loadColumnHeaders(columnHeaders);
    }

    private void loadColumnHeaders(List<ColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
        this.fieldNameIndexMap = new HashMap<>(columnHeaders.size() * 2);
        this.columnIndexIndexMap = new HashMap<>(columnHeaders.size() * 2);
        this.headerNameIndexMap = new HashMap<>(columnHeaders.size() * 2);
        this.headerNameKeyIndexMap = new HashMap<>(columnHeaders.size() * 2);

        for (ColumnHeader columnHeader : columnHeaders) {
            this.fieldNameIndexMap.put(columnHeader.getFieldName(), columnHeader);
            this.columnIndexIndexMap.put(columnHeader.getIndex(), columnHeader);
            this.headerNameIndexMap.put(columnHeader.getHeaderName(), columnHeader);
            this.headerNameKeyIndexMap.put(columnHeader.getHeaderNameKey(), columnHeader);
        }
    }

    @Override
    public List<ColumnHeader> getColumnHeaders() {
        return this.columnHeaders;
    }

    @Override
    public ColumnHeader getColumnHeaderByFieldName(String fieldName) {
        return this.fieldNameIndexMap.get(fieldName);
    }

    @Override
    public ColumnHeader getColumnHeaderByHeaderName(List<String> headerName) {
        return this.headerNameIndexMap.get(headerName);
    }

    @Override
    public ColumnHeader getColumnHeaderByHeaderNameKey(String headerNameKey) {
        return headerNameKeyIndexMap.get(headerNameKey);
    }


    @Override
    public ColumnHeader getHeaderByIndex(Integer index) {
        return this.columnIndexIndexMap.get(index);
    }

    @Override
    public Integer getHeaderRowCount(Integer groupIndex) {
        Integer max = 1;
        for (ColumnHeader columnHeader : columnHeaders) {
            if ((columnHeader.getGroupIndex() != -1) && !columnHeader.getGroupIndex().equals(groupIndex)) {
                continue;
            }
            if (columnHeader.getHeaderRowCount() > max) {
                max = columnHeader.getHeaderRowCount();
            }
        }
        return max;
    }

    @Override
    public void fromJson(String json) {
        List<ColumnHeader> columnHeaderList = JsonUtil.toArrayObject(json, ColumnHeader.class);
        loadColumnHeaders(columnHeaderList);
    }

    @Override
    public String toJson() {
        return JsonUtil.toJsonString(columnHeaders);
    }

}
