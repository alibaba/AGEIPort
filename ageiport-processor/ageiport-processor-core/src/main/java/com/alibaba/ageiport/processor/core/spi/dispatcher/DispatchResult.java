package com.alibaba.ageiport.processor.core.spi.dispatcher;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class DispatchResult {

    private Boolean success;

    private String message;

    public DispatchResult() {
    }

    public DispatchResult(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
