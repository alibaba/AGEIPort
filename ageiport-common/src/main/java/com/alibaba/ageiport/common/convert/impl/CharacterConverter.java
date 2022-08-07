package com.alibaba.ageiport.common.convert.impl;


import com.alibaba.ageiport.common.convert.AbstractConverter;
import com.alibaba.ageiport.common.utils.BooleanUtils;
import com.alibaba.ageiport.common.utils.StringUtils;

/**
 * 字符转换器
 *
 * @author xuechao.sxc
 */
public class CharacterConverter extends AbstractConverter<Character> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Character convertInternal(Object value) {
        if (value == null) {
            return null;
        }

        if (Character.class == value.getClass()) {
            return (Character) value;
        } else if (value instanceof Boolean) {
            return BooleanUtils.toCharacter((Boolean) value);
        } else {
            final String valueStr = convertToStr(value);
            if (StringUtils.isNotBlank(valueStr)) {
                return valueStr.charAt(0);
            }
        }
        return null;
    }

}
