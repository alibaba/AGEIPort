package com.alibaba.ageiport.common.exception;


import com.alibaba.ageiport.common.constants.HttpStatus;
import com.alibaba.ageiport.common.lang.BizCode;

/**
 * Not found exception
 *
 * @author lingyi 2020-08-03
 **/
public class NotFoundException extends AbstractStandardException {

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(Throwable e) {
        super(e, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message, Throwable e) {
        super(message, e, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message, BizCode bizCode, Throwable e) {
        super(message, bizCode, e, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(BizCode bizCode, Throwable e) {
        super(bizCode, e, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(BizCode bizCode, Throwable e, Object... userMessageParams) {
        super(bizCode, e, HttpStatus.NOT_FOUND, userMessageParams);
    }
}
