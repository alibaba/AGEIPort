package com.alibaba.ageiport.common.exception;


import com.alibaba.ageiport.common.constants.HttpStatus;
import com.alibaba.ageiport.common.lang.BizCode;

/**
 * Invalid param exception
 *
 * @author lingyi 2020-08-03
 **/
public class InvalidParamException extends AbstractStandardException {

    public InvalidParamException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public InvalidParamException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidParamException(Throwable e) {
        super(e, HttpStatus.BAD_REQUEST);
    }

    public InvalidParamException(String message, Throwable e) {
        super(message, e, HttpStatus.BAD_REQUEST);
    }

    public InvalidParamException(String message, BizCode bizCode, Throwable e) {
        super(message, bizCode, e, HttpStatus.BAD_REQUEST);
    }

    public InvalidParamException(BizCode bizCode, Throwable e) {
        super(bizCode, e, HttpStatus.BAD_REQUEST);
    }

    public InvalidParamException(BizCode bizCode, Throwable e, Object... userMessageParams) {
        super(bizCode, e, HttpStatus.BAD_REQUEST, userMessageParams);
    }
}
