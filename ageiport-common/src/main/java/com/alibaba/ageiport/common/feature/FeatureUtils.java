package com.alibaba.ageiport.common.feature;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Feature工具
 *
 * @author lingyi
 */
public class FeatureUtils {
    private FeatureUtils() {
    }

    /**
     * 不存在返回null
     */
    public static <T> T getFeature(String feature, FeatureKey<T> key) {
        JSONObject object = JSON.parseObject(feature);
        if (object == null) {
            return null;
        }
        return object.getObject(key.getKey(), key.getClazz());
    }

    public static <T> boolean exists(String feature, FeatureKey<T> key) {
        JSONObject object = JSON.parseObject(feature);
        if (object == null) {
            return false;
        }
        return object.containsKey(key.getKey());
    }


    public static <T> String putFeature(String feature, FeatureKey<T> key, T info) {
        JSONObject object = JSON.parseObject(feature);
        if (object == null) {
            object = new JSONObject();
        }
        if (key.getLength() > 0 && info instanceof String) {
            if (((String) info).length() > key.getLength()) {
                object.put(key.getKey(), ((String) info).substring(0, key.getLength()) + "......");
                return object.toJSONString();
            }
        }

        object.put(key.getKey(), info);
        return object.toJSONString();
    }

    public static String merge(String oldFeature, String newFeature) {
        JSONObject oldFeatureObject = JSON.parseObject(oldFeature);
        if (oldFeatureObject == null) {
            return newFeature;
        }
        JSONObject newFeatureObject = JSON.parseObject(newFeature);
        if (newFeatureObject == null) {
            return oldFeature;
        }
        for (Map.Entry e : newFeatureObject.entrySet()) {
            oldFeatureObject.put((String) e.getKey(), e.getValue());
        }
        return oldFeatureObject.toJSONString();
    }
}
