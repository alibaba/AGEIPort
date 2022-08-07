package com.alibaba.ageiport.processor.core.model.api.impl;

import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeader;
import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeaders;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class BizDynamicColumnHeadersImpl implements BizDynamicColumnHeaders {

    private List<BizDynamicColumnHeader> bizDynamicColumnHeaders;

    @Override
    public BizDynamicColumnHeader getBizDynamicColumnHeaderByFiledName(String filedName) {
        for (BizDynamicColumnHeader bizDynamicColumnHeader : bizDynamicColumnHeaders) {
            if (bizDynamicColumnHeader.getFieldName().equals(filedName)) {
                return bizDynamicColumnHeader;
            }
        }
        return null;
    }
}
