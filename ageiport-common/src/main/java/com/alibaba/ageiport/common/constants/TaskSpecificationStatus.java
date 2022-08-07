package com.alibaba.ageiport.common.constants;


import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum TaskSpecificationStatus {
    ENABLE("ENABLE", "ENABLE"),

    DISABLE("DISABLE", "DISABLE");

    private static final Map<String, TaskSpecificationStatus> ENUM_MAP = Arrays.stream(values()).collect(
            Collectors.toMap(s -> s.code, s -> s));

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    TaskSpecificationStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TaskSpecificationStatus of(String code) {
        if (code == null) {
            return null;
        }
        return ENUM_MAP.get(code);
    }

}