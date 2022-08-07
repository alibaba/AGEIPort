package com.alibaba.ageiport.processor.core.model.api.impl;

import com.alibaba.ageiport.processor.core.model.api.BizColumnHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class BizColumnHeaderImpl implements BizColumnHeader {

    private String headerName;

    private String fieldName;

    private String dataType;

    private boolean dynamicColumn;

    private String dynamicColumnKey;

    private String type;

    private String groupName;

    private Integer groupIndex;

    private boolean errorHeader;

    private boolean required;
}
