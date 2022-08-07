package com.alibaba.ageiport.common.convert.impl;


import com.alibaba.ageiport.common.convert.AbstractConverter;
import com.alibaba.ageiport.common.utils.BooleanUtils;

/**
 * 布尔转换器
 *
 * @author xuechao.sxc
 */
public class BooleanConverter extends AbstractConverter<Boolean> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Boolean convertInternal(Object value) {
        if (null == value) {
            return Boolean.FALSE;
        }
        if (Boolean.class == value.getClass()) {
            return (Boolean) value;
        }
        String valueStr = convertToStr(value);
        return BooleanUtils.toBoolean(valueStr);
    }

}
