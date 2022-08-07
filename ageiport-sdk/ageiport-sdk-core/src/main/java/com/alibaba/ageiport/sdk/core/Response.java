package com.alibaba.ageiport.sdk.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lingyi
 */
@Getter
@Setter
public abstract class Response implements Serializable {

    private static final long serialVersionUID = -513736316230400170L;

    private String requestId;

    private String code;

    private String message;

    private Boolean success;
}
