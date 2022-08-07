package com.alibaba.ageiport.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public class JsonUtils {

    public static <T> T getObjectFromKv(Map<String, Object> properties, String keyPrefix, Class<T> clazz) {
        String jsonStringFromKv = getJsonStringFromKv(properties, keyPrefix);

        T object = JSON.parseObject(jsonStringFromKv, clazz);

        return object;
    }

    public static String getJsonStringFromKv(Map<String, Object> properties, String keyPrefix) {
        //一般情况下接口使用者不喜欢在最后额外加一个点
        if (!StringUtils.endsWithIgnoreCase(keyPrefix, ".")) {
            keyPrefix = keyPrefix + ".";
        }

        Map<String, Object> geiProperties = new HashMap<>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getKey().startsWith(keyPrefix)) {
                String newKey = entry.getKey().replace(keyPrefix, "");
                geiProperties.put(newKey, entry.getValue());
            }
        }
        String jsonStringFromKv = getJsonStringFromKv(geiProperties);
        return jsonStringFromKv;
    }

    /**
     * a.x=1
     * a.y=2
     * b.z=3
     * -->
     * {
     * "a": {
     * "x": 1,
     * "y": 2
     * },
     * "b": {
     * "z": 3
     * }
     * }
     */
    public static String getJsonStringFromKv(Map<String, Object> properties) {
        Map jsonObject = new HashMap();

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            JSONObject jsonCur = getJsonCur(key, value);
            jsonObject = deepMerge(jsonObject, jsonCur);
        }

        return JSON.toJSONString(jsonObject);
    }

    private static JSONObject getJsonCur(String key, Object value) {
        JSONObject jsonObject = new JSONObject();
        String[] split = key.split("\\.");
        if (split.length == 1) {
            jsonObject.put(key, value);
            return jsonObject;
        }
        int firstIndexOfDot = key.indexOf(".");
        String restKey = key.substring(firstIndexOfDot + 1);

        JSONObject jsonCur = getJsonCur(restKey, value);


        String currentKey = split[0];

        jsonObject.put(currentKey, jsonCur);

        return jsonObject;
    }

    private static Map deepMerge(Map original, Map newMap) {
        for (Object key : newMap.keySet()) {
            if (newMap.get(key) instanceof Map && original.get(key) instanceof Map) {
                Map originalChild = (Map) original.get(key);
                Map newChild = (Map) newMap.get(key);
                original.put(key, deepMerge(originalChild, newChild));
            } else if (newMap.get(key) instanceof List && original.get(key) instanceof List) {
                List originalChild = (List) original.get(key);
                List newChild = (List) newMap.get(key);
                for (Object each : newChild) {
                    if (!originalChild.contains(each)) {
                        originalChild.add(each);
                    }
                }
            } else {
                original.put(key, newMap.get(key));
            }
        }
        return original;
    }

    public static void main(String[] args) {
        Map<String, Object> props = new HashMap<>();
        props.put("x.a", "1");
        props.put("x.b", "2");
        props.put("x.n.a", "2");
        props.put("x.n.b", "2");

        String jsonStringFromKv = getJsonStringFromKv(props);
        System.out.println(jsonStringFromKv);
    }
}
