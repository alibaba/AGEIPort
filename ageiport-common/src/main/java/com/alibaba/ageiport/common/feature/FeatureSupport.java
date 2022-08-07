package com.alibaba.ageiport.common.feature;

import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

/**
 * feature标
 *
 * @author lingyi
 */
public interface FeatureSupport {

    default void addFeatures(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        JSONObject jsonObject = JSON.parseObject(getFeature());
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        jsonObject.putAll(map);
        setFeature(jsonObject.toJSONString());
    }

    default void addFeature(String key, String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        JSONObject jsonObject = JSON.parseObject(getFeature());
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        jsonObject.put(key, value);
        setFeature(jsonObject.toJSONString());
    }

    default void removeFeature(String key) {
        JSONObject jsonObject = JSON.parseObject(getFeature());
        if (jsonObject == null) {
            return;
        }
        jsonObject.remove(key);
        setFeature(jsonObject.toJSONString());
    }

    @JSONField(serialize = false)
    default String getFeature(String key) {
        String features = getFeature();
        JSONObject jsonObject = JSON.parseObject(features);
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.getString(key);
    }

    @JSONField(serialize = false)
    default JSONObject getFeatureJson() {
        String features = getFeature();
        JSONObject jsonObject = JSON.parseObject(features);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }

    String getFeature();

    void setFeature(String features);
}
