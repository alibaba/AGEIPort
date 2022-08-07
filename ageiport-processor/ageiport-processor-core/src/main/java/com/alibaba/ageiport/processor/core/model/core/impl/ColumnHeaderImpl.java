package com.alibaba.ageiport.processor.core.model.core.impl;

import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class ColumnHeaderImpl implements ColumnHeader {

    private Integer index;

    private String headerName;

    private String fieldName;

    private String type;

    private Boolean dynamicColumn;

    private String dynamicColumnKey;

    private String groupName;

    private Integer groupIndex;

    private Boolean errorHeader;

    private Boolean ignoreHeader;

    private Boolean required;
}
