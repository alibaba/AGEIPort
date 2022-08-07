package com.alibaba.ageiport.common.utils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Type的工具类封装
 *
 * @author lingyi
 */
public class TypeUtils {

    public static List<Type> getGenericParamType(Class source, Class target) {
        Type directTargetInterface = findDirectTargetInterface(source, target);
        if (directTargetInterface != null) {
            return findActualTypeArguments(directTargetInterface);
        } else {
            Map<String, Type> parentTypeArgumentMap = findParentTypeArgumentMap(source);
            Type targetInterface = findTargetInterfaceRecursive(source, target);
            List<Type> actualTypeArguments = findActualTypeArguments(targetInterface);
            return actualTypeArguments.stream().map(type -> {
                if (type instanceof TypeVariable) {
                    String name = ((TypeVariable) type).getName();
                    return parentTypeArgumentMap.get(name);
                }
                return type;
            }).collect(Collectors.toList());
        }
    }

    /**
     * 获取直接继承目标接口, 此接口是泛型接口
     *
     * @param source
     * @param target
     * @return
     */
    public static ParameterizedType findDirectTargetInterface(Class source, Class target) {
        // 类实现的接口(直接实现), 返回类型包含了泛型信息
        Type[] genericInterfaces = source.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) genericInterface).getRawType();
                // 是否是目标接口
                if (rawType.equals(target)) {
                    return (ParameterizedType) genericInterface;
                }
            }
        }
        return null;
    }


    public static ParameterizedType findTargetInterfaceRecursive(Class source, Class target) {
        while (source != null && !source.equals(Object.class)) {
            ParameterizedType directTargetInterface = findDirectTargetInterface(source, target);
            if (directTargetInterface != null) {
                return directTargetInterface;
            }
            source = source.getSuperclass();
        }
        return null;
    }

    /**
     * <pre>
     * 获取泛型的实际类型
     * 如果泛型是 List<D> 这种, 继续找到D
     * Type 可能是实际类型, 也可能是TypeVariableImpl
     * </pre>
     *
     * @param type
     * @return
     */
    public static List<Type> findActualTypeArguments(Type type) {
        List<Type> list = new ArrayList();
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                if (actualTypeArgument instanceof ParameterizedType) {
                    list.add(findActualTypeArgumentIfRawTypeIfList(actualTypeArgument));
                } else {
                    list.add(actualTypeArgument);
                }
            }
        }
        return list;
    }

    public static Type findActualTypeArgumentIfRawTypeIfList(Type type) {
        // 如果是 ParameterizedType, 说明泛型参数是有嵌套的
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            // 获取本层的泛型, 判断是否List, 是则递归
            Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (List.class.isAssignableFrom((Class<?>) rawType)) {
                return findActualTypeArgumentIfRawTypeIfList(actualTypeArgument);
            } else {
                return rawType;
            }
        } else {
            return type;
        }
    }

    /**
     * @param source
     * @return {{"T", ParameterizedType}}
     */
    public static Map<String, Type> findParentTypeArgumentMap(Class source) {
        Map<String, Type> map = new HashMap(1 << 3);
        while (source != null && !source.equals(Object.class)) {
            // 返回直接父类, 会带上泛型参数
            Type genericSuperclass = source.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
                TypeVariable[] typeParameters = source.getSuperclass().getTypeParameters();
                for (int i = 0; i < typeParameters.length; i++) {
                    Type actualTypeArgument = actualTypeArguments[i];
                    TypeVariable typeParameter = typeParameters[i];
                    Type actualTypeArgumentIfRawType = findActualTypeArgumentIfRawTypeIfList(actualTypeArgument);
                    // 不包含并且类型是Class类
                    if (!map.containsKey(typeParameter.getName()) && actualTypeArgumentIfRawType instanceof Class) {
                        map.put(typeParameter.getName(), actualTypeArgumentIfRawType);
                    }
                }
            }
            source = source.getSuperclass();
        }
        return map;
    }

    /**
     * 获取所有的字段，包括父类
     *
     * @param clazz
     * @return
     */
    public static List<Field> getDeclaredFields(Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz param can not be null");
        }
        List<Class> classes = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            classes.add(clazz);
            clazz = clazz.getSuperclass();
        }
        List<Field> fields = new ArrayList<>();
        for (int i = classes.size() - 1; i >= 0; i--) {
            final Class aClass = classes.get(i);
            fields.addAll(new ArrayList<>(Arrays.asList(aClass.getDeclaredFields())));
        }
        return fields;
    }

    public static Set<Class<?>> flattenHierarchy(Class<?> concreteClass) {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(concreteClass);
        while (concreteClass != null && !concreteClass.equals(Object.class)) {
            Class<?>[] interfaces = concreteClass.getInterfaces();
            if (interfaces != null) {
                for (Class<?> anInterface : interfaces) {
                    classes.addAll(flattenHierarchy(anInterface));
                }
            }
            concreteClass = concreteClass.getSuperclass();
            if (concreteClass != null) {
                classes.add(concreteClass);
            }
        }
        return classes;
    }
    // ----------------------------------------------------------------------------------- Type Argument

    /**
     * 是否未知类型<br>
     * type为null或者{@link TypeVariable} 都视为未知类型
     *
     * @param type Type类型
     * @return 是否未知类型
     */
    public static boolean isUnknow(Type type) {
        return null == type || type instanceof TypeVariable;
    }

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param type 被检查的类型，必须是已经确定泛型类型的类型
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getTypeArgument(Type type) {
        return getTypeArgument(type, 0);
    }

    /**
     * 获得给定类的泛型参数
     *
     * @param type  被检查的类型，必须是已经确定泛型类型的类
     * @param index 泛型类型的索引号，即第几个泛型类型
     * @return {@link Type}
     */
    public static Type getTypeArgument(Type type, int index) {
        final Type[] typeArguments = getTypeArguments(type);
        if (null != typeArguments && typeArguments.length > index) {
            return typeArguments[index];
        }
        return null;
    }

    /**
     * 获得指定类型中所有泛型参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到String
     *
     * @param type 指定类型
     * @return 所有泛型参数类型
     */
    public static Type[] getTypeArguments(Type type) {
        if (null == type) {
            return null;
        }

        final ParameterizedType parameterizedType = toParameterizedType(type);
        return (null == parameterizedType) ? null : parameterizedType.getActualTypeArguments();
    }

    /**
     * 获得Type对应的原始类
     *
     * @param type {@link Type}
     * @return 原始类，如果无法获取原始类，返回{@code null}
     */
    public static Class<?> getClass(Type type) {
        if (null != type) {
            if (type instanceof Class) {
                return (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) type).getRawType();
            } else if (type instanceof TypeVariable) {
                return (Class<?>) ((TypeVariable<?>) type).getBounds()[0];
            } else if (type instanceof WildcardType) {
                final Type[] upperBounds = ((WildcardType) type).getUpperBounds();
                if (upperBounds.length == 1) {
                    return getClass(upperBounds[0]);
                }
            }
        }
        return null;
    }


    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到B{@link ParameterizedType}，从而获取到String
     *
     * @param type {@link Type}
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType toParameterizedType(Type type) {
        ParameterizedType result = null;
        if (type instanceof ParameterizedType) {
            result = (ParameterizedType) type;
        } else if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            Type genericSuper = clazz.getGenericSuperclass();
            if (null == genericSuper || Object.class.equals(genericSuper)) {
                // 如果类没有父类，而是实现一些定义好的泛型接口，则取接口的Type
                final Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (ArrayUtils.isNotEmpty(genericInterfaces)) {
                    // 默认取第一个实现接口的泛型Type
                    genericSuper = genericInterfaces[0];
                }
            }
            result = toParameterizedType(genericSuper);
        }
        return result;
    }

}
