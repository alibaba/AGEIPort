package com.alibaba.ageiport.common.convert.impl;


import com.alibaba.ageiport.common.convert.AbstractConverter;

/**
 * 字符串转换器
 *
 * @author xuechao.sxc
 */
public class StringConverter extends AbstractConverter<String> {
    private static final long serialVersionUID = 1L;

    @Override
    protected String convertInternal(Object value) {
        return convertToStr(value);
    }

}
