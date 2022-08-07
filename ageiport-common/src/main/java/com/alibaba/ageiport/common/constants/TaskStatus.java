package com.alibaba.ageiport.common.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum TaskStatus {
    NEW("NEW", "新建"),
    
    EXECUTING("EXECUTING", "执行中"),
    FINISHED("FINISHED", "已完成"),
    ERROR("ERROR", "执行错误");

    private static final Map<String, TaskStatus> ENUM_MAP = Arrays.stream(values()).collect(
            Collectors.toMap(s -> s.code, s -> s));

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    TaskStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TaskStatus of(String code) {
        if (code == null) {
            return null;
        }
        return ENUM_MAP.get(code);
    }

    public static Boolean isFinished(String code) {
        return FINISHED.code.equals(code);
    }

    public static Boolean isError(String code) {
        return ERROR.code.equals(code);
    }

    public static Boolean isFinalStatus(String code) {
        return isFinished(code) || isError(code);
    }

    public static Boolean isNew(String code) {
        return NEW.code.equals(code);
    }
}