package com.alibaba.ageiport.common;

import com.alibaba.ageiport.common.utils.CollectionUtils;
import com.alibaba.ageiport.common.utils.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lingyi
 */
public class URL implements Serializable {

    private final Map<String, String> parameters;

    public URL() {
        this.parameters = new HashMap<>();
    }

    public URL(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public URL addParameter(String key, String value) {
        if (StringUtils.isEmpty(key)
            || StringUtils.isEmpty(value)) {
            return this;
        }
        // if value doesn't change, return immediately
        // value != null
        if (value.equals(getParameters().get(key))) {
            return this;
        }
        getParameters().put(key, value);
        return this;
    }

    public URL removeParameter(String key) {
        if (StringUtils.isEmpty(key)) {
            return this;
        }
        return removeParameters(key);
    }

    public URL removeParameters(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return this;
        }
        return removeParameters(keys.toArray(new String[0]));
    }

    public URL removeParameters(String... keys) {
        if (keys == null || keys.length == 0) {
            return this;
        }
        Map<String, String> map = new HashMap<>(getParameters());
        for (String key : keys) {
            map.remove(key);
        }
        if (map.size() == getParameters().size()) {
            return this;
        }
        return new URL(map);
    }
}
