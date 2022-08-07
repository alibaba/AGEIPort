package com.alibaba.ageiport.processor.core.spi.convertor;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public interface Model {
    default Map<String, Object> toMap() {
        return changeToMap(this, true);
    }

    default Map<String, Object> toMap(Boolean exceptStream) {
        return changeToMap(this, exceptStream);
    }


    static Map<String, Object> toMap(Object object) {
        return toMap(object, true);
    }

    static Map<String, Object> toMap(Object object, Boolean exceptStream) {
        Map<String, Object> map = new HashMap<>();
        if (null != object && object instanceof Map) {
            return (Map<String, Object>) object;
        }
//        if (null == object || !Model.class.isAssignableFrom(object.getClass())) {
//            return map;
//        }
        if (null == object) {
            return map;
        }
        map = changeToMap(object, exceptStream);
        return map;
    }


    static Map<String, Object> changeToMap(Object object, Boolean exceptStream) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                String key;
                key = field.getName();
                if (null != field.get(object) && List.class.isAssignableFrom(field.get(object).getClass())) {
                    List<Object> arrayField = (List<Object>) field.get(object);
                    List<Object> fieldList = new ArrayList<>();
                    for (int i = 0; i < arrayField.size(); i++) {
                        fieldList.add(parseObject(arrayField.get(i)));
                    }
                    map.put(key, fieldList);
                } else if (null != field.get(object) && Model.class.isAssignableFrom(field.get(object).getClass())) {
                    map.put(key, toMap(field.get(object), exceptStream));
                } else if (null != field.get(object) && Map.class.isAssignableFrom(field.get(object).getClass())) {
                    Map<String, Object> valueMap = (Map<String, Object>) field.get(object);
                    Map<String, Object> result = new HashMap<>();
                    for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                        result.put(entry.getKey(), parseObject(entry.getValue()));
                    }
                    map.put(key, result);
                } else if (exceptStream && null != field.get(object) && InputStream.class.isAssignableFrom(field.get(object).getClass())) {
                    continue;
                } else if (exceptStream && null != field.get(object) && OutputStream.class.isAssignableFrom(field.get(object).getClass())) {
                    continue;
                } else {
                    map.put(key, field.get(object));
                }
                field.setAccessible(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return map;
    }


    static Object parseObject(Object o) {
        if (null == o) {
            return o;
        }
        Class clazz = o.getClass();
        if (List.class.isAssignableFrom(clazz)) {
            List<Object> list = (List<Object>) o;
            List<Object> result = new ArrayList<>();
            for (Object object : list) {
                result.add(parseObject(object));
            }
            return result;
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<String, Object> map = (Map<String, Object>) o;
            Map<String, Object> result = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                result.put(entry.getKey(), parseObject(entry.getValue()));
            }
            return result;
        } else if (Model.class.isAssignableFrom(clazz)) {
            return ((Model) o).toMap(false);
        } else {
            return o;
        }
    }

    static Object buildObject(Object o, Class self, Type subType) {
        Class valueClass = o.getClass();
        if (Map.class.isAssignableFrom(self) && Map.class.isAssignableFrom(valueClass)) {
            Map<String, Object> valueMap = (Map<String, Object>) o;
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                if (null == subType || subType instanceof WildcardType) {
                    result.put(entry.getKey(), entry.getValue());
                } else if (subType instanceof Class) {
                    result.put(entry.getKey(), buildObject(entry.getValue(), (Class) subType, null));
                } else {
                    ParameterizedType parameterizedType = (ParameterizedType) subType;
                    Type[] types = parameterizedType.getActualTypeArguments();
                    result.put(entry.getKey(), buildObject(entry.getValue(), (Class) parameterizedType.getRawType(), types[types.length - 1]));
                }
            }
            return result;
        } else if (List.class.isAssignableFrom(self) && List.class.isAssignableFrom(valueClass)) {
            List<Object> valueList = (List<Object>) o;
            List<Object> result = new ArrayList<>();
            for (Object object : valueList) {
                if (null == subType || subType instanceof WildcardType) {
                    result.add(object);
                } else if (subType instanceof Class) {
                    result.add(buildObject(object, (Class) subType, null));
                } else {
                    ParameterizedType parameterizedType = (ParameterizedType) subType;
                    Type[] types = parameterizedType.getActualTypeArguments();
                    result.add(buildObject(object, (Class) parameterizedType.getRawType(), types[types.length - 1]));
                }
            }
            return result;
        } else if (Model.class.isAssignableFrom(self) && Map.class.isAssignableFrom(valueClass)) {
            try {
                return toModel((Map<String, Object>) o, (Model) self.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            return o;
        }
    }

    static Type getType(Field field, int index) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        Type actualTypeArgument = actualTypeArguments[index];
        return actualTypeArgument;
    }


    @SuppressWarnings("unchecked")
    static <T> T toModel(Map<String, ?> map, T model) {
        T result = model;
        for (Field field : result.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String key;
            key = field.getName();
            Object value = map.get(key);
            if (value == null) {
                continue;
            }
            field.setAccessible(true);
            result = setModelField(result, field, value, false);
            field.setAccessible(false);
        }
        return result;
    }

    static <T> T setModelField(T model, Field field, Object value, boolean userBuild) {
        try {
            Class<?> clazz = field.getType();
            Object resultValue = parseNumber(value, clazz);
            T result = model;
            if (Model.class.isAssignableFrom(clazz)) {
                Object data = clazz.getDeclaredConstructor().newInstance();
                if (userBuild) {
                    field.set(result, build(toMap(resultValue, false), (Model) data));
                } else if (!userBuild && Map.class.isAssignableFrom(resultValue.getClass())) {
                    field.set(result, toModel((Map<String, Object>) resultValue, (Model) data));
                } else {
                    field.set(result, resultValue);
                }
            } else if (Map.class.isAssignableFrom(clazz)) {
                field.set(result, buildObject(resultValue, Map.class, getType(field, 1)));
            } else if (List.class.isAssignableFrom(clazz)) {
                field.set(result, buildObject(resultValue, List.class, getType(field, 0)));
            } else {
                field.set(result, confirmType(clazz, resultValue));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static <T> T build(Map<String, ?> map, T model) {
        T result = model;
        for (Field field : model.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            String key = field.getName();
            Object value = map.get(key);
            result = setModelField(result, field, value, true);
            field.setAccessible(false);
        }
        return result;
    }

    static Object parseNumber(Object value, Class clazz) {
        BigDecimal bigDecimal;
        if (value instanceof Double && (clazz == Long.class || clazz == long.class)) {
            bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.longValue();
        }
        if (value instanceof Double && (clazz == Integer.class || clazz == int.class)) {
            bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.intValue();
        }
        if (value instanceof Double && (clazz == Float.class || clazz == float.class)) {
            bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.floatValue();
        }
        return value;
    }


    static Map<String, Object> buildMap(Model model) {
        if (null == model) {
            return null;
        } else {
            return model.toMap();
        }
    }


    static Object confirmType(Class expect, Object object) {
        if (String.class.isAssignableFrom(expect)) {
            if (object instanceof Number || object instanceof Boolean) {
                return object.toString();
            }
        } else if (Boolean.class.isAssignableFrom(expect)) {
            if (object instanceof String) {
                return Boolean.parseBoolean(String.valueOf(object));
            } else if (object instanceof Integer) {
                if (object.toString().equals("1")) {
                    return true;
                } else if (object.toString().equals("0")) {
                    return false;
                }
            }
        } else if (Integer.class.isAssignableFrom(expect)) {
            if (object instanceof String) {
                return Integer.parseInt(object.toString());
            }
            // 判断数值大小是否超过期望数据类型的上限，如果不超过则强制转换，如果超过则报错
            if (object instanceof Long && ((Long) object).longValue() <= Integer.MAX_VALUE) {
                return Integer.parseInt(object.toString());
            }
        } else if (Long.class.isAssignableFrom(expect)) {
            if (object instanceof String || object instanceof Integer) {
                return Long.parseLong(object.toString());
            }
        } else if (Float.class.isAssignableFrom(expect)) {
            if (object instanceof String || object instanceof Integer || object instanceof Long) {
                return Float.parseFloat(object.toString());
            }
            // 判断数值大小是否超过期望数据类型的上限，如果不超过则强制转换，如果超过则报错
            if (object instanceof Double && ((Double) object).doubleValue() <= Float.MAX_VALUE) {
                return Float.parseFloat(object.toString());
            }
        } else if (Double.class.isAssignableFrom(expect)) {
            if (object instanceof String || object instanceof Integer || object instanceof Long || object instanceof Float) {
                return Double.parseDouble(object.toString());
            }
        }
        return object;
    }
}
