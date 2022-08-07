package com.alibaba.ageiport.common.utils;

import com.alibaba.fastjson.JSON;

/**
 * @author lingyi
 */
public class BeanUtils {

    public static <T> T cloneProp(T object) {
        final String jsonString = JSON.toJSONString(object);
        final Object o = JSON.parseObject(jsonString, object.getClass());
        return (T) o;
    }

    public static <T> T cloneProp(Object object, Class<T> targetClass) {
        final String jsonString = JSON.toJSONString(object);
        final Object o = JSON.parseObject(jsonString, targetClass);
        return (T) o;
    }
}
