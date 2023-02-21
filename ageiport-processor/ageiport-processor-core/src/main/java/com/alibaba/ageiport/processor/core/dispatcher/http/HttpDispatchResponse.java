package com.alibaba.ageiport.processor.core.dispatcher.http;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lingyi
 */
@Getter
@Setter
public class HttpDispatchResponse implements Serializable {

    private static final long serialVersionUID = 6755612788072553919L;

    private Boolean success;

    private String errorMessage;

    public HttpDispatchResponse(Boolean success) {
        this.success = success;
    }
}
