package com.alibaba.ageiport.task.server.repository.query;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lingyi
 */
public class BaseQuery {

    protected Map<String, Object> parameters = new HashMap<>();

    public String query() {
        return parameters.keySet().stream()
                .map(o -> o + "=:" + o)
                .collect(Collectors.joining(" and "));
    }

    public Map<String, Object> parameters() {
        return parameters;
    }

    public BaseQuery addIgnoreNull(String key, Object value) {
        if (value == null) {
            return this;
        }
        this.parameters.put(key, value);
        return this;
    }

    public static BaseQuery merge(BaseQuery query, String key, Object value) {
        Map<String, Object> parameters = new HashMap<>(query.parameters());
        parameters.put(key, value);
        BaseQuery baseQuery = new BaseQuery();
        baseQuery.setParameters(parameters);
        return baseQuery;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
