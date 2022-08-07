package com.alibaba.ageiport.common.feature;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingyi
 */
public class FeatureKey<T> {
    private static Map<String, FeatureKey> keysMap = new ConcurrentHashMap<>();
    private String key;
    private Class<T> clazz;
    private int length = -1;

    public String getKey() {
        return key;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public int getLength() {
        return length;
    }

    public FeatureKey(String key, Class<T> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    public FeatureKey(String key, Class<T> clazz, int length) {
        this.key = key;
        this.clazz = clazz;
        this.length = length;
    }

    public static <T> FeatureKey<T> create(String key, Class<T> clazz) {
        return keysMap.computeIfAbsent(key, k -> new FeatureKey(k, clazz));
    }


    public static <T> FeatureKey<T> create(String key, Class<T> clazz, int length) {
        return keysMap.computeIfAbsent(key, k -> new FeatureKey(k, clazz, length));
    }
}
