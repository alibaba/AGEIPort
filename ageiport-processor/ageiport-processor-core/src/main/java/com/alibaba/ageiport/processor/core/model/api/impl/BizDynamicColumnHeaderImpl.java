package com.alibaba.ageiport.processor.core.model.api.impl;

import com.alibaba.ageiport.processor.core.model.api.BizColumnHeader;
import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeader;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class BizDynamicColumnHeaderImpl implements BizDynamicColumnHeader {

    private String fieldName;

    private List<BizColumnHeader> flatColumnHeaders;

}
