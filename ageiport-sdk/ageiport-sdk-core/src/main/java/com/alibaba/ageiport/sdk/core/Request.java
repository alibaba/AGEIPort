package com.alibaba.ageiport.sdk.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author lingyi
 */
@Getter
@Setter
public abstract class Request<T extends Response> implements Serializable {

    private static final long serialVersionUID = -7457093999706192598L;
    /**
     * 域名
     */
    private String domain;

    /**
     * 租户
     */
    private String tenant;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 应用
     */
    private String app;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * requestId
     */
    private String requestId;

    /**
     * accessKeyId
     */
    private String accessKeyId;

    /**
     * 签名
     */
    private String signature;

    /**
     * nonce
     */
    private String nonce;


    /**
     * uid
     */
    private String uid;

    /**
     * attributes
     */
    private String attributes;

    public String getAction() {
        return this.getClass().getSimpleName().replace("Request", "");
    }

    public String getVersion() {
        return "v1";
    }

    public String getUrl() {
        return "/" + getVersion() + "/" + getAction();
    }

    public Class<T> getResponseClass() {
        Type[] arguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        if (arguments.length == 0) {
            return null;
        }
        return (Class<T>) arguments[0];
    }

}
