package com.alibaba.ageiport.processor.core.model.api.impl;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.processor.core.model.api.BizColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class BizColumnHeaderImpl implements BizColumnHeader {

    private List<String> headerName;
    private String headerNameKey;

    private String fieldName;

    private String dataType;

    private boolean dynamicColumn;

    private String dynamicColumnKey;

    private String type;

    private String groupName;

    private Integer groupIndex;

    private boolean errorHeader;

    private boolean required;

    private Integer columnWidth;

    private List<String> values;

    public void setHeaderName(List<String> headerName) {
        this.headerName = headerName;
    }

    public void setHeaderName(String headerName) {
        this.setHeaderName(Lists.newArrayList(headerName));
    }

    public void setHeaderNameKey(String headerNameKey) {
        this.headerNameKey = headerNameKey;
        this.setHeaderNameKey(this.headerName);
    }

    public void setHeaderNameKey(List<String> headerName) {
        this.headerNameKey = String.join(ColumnHeader.headerSplit, headerName);
    }

    @Override
    public Integer getHeaderRowCount() {
        return this.headerName.size();
    }
}
