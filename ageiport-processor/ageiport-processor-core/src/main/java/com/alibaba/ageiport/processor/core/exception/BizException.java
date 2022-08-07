package com.alibaba.ageiport.processor.core.exception;

/**
 * @author lingyi
 */

public class BizException extends RuntimeException {

    private static final long serialVersionUID = -6011156842049690640L;

    private String errorCode;

    private String errorMessage;

    public BizException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
