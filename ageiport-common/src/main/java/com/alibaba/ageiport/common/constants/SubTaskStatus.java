package com.alibaba.ageiport.common.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lingyi
 */

public enum SubTaskStatus {
    NEW("NEW", "新建"),

    EXECUTING("EXECUTING", "执行中"),
    FINISHED("FINISHED", "已完成"),
    ERROR("ERROR", "执行错误");

    private static final Map<String, SubTaskStatus> ENUM_MAP = Arrays.stream(values()).collect(
            Collectors.toMap(s -> s.code, s -> s));

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    SubTaskStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SubTaskStatus of(String code) {
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