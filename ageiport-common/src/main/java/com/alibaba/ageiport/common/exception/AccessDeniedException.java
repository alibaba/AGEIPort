package com.alibaba.ageiport.common.exception;


import com.alibaba.ageiport.common.constants.HttpStatus;
import com.alibaba.ageiport.common.lang.BizCode;

/**
 * Access has been denied
 *
 * @author lingyi 2020-08-03
 **/
public class AccessDeniedException extends AbstractStandardException {

    public AccessDeniedException() {
        super(HttpStatus.FORBIDDEN);
    }

    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AccessDeniedException(Throwable e) {
        super(e, HttpStatus.FORBIDDEN);
    }

    public AccessDeniedException(String message, Throwable e) {
        super(message, e, HttpStatus.FORBIDDEN);
    }

    public AccessDeniedException(String message, BizCode bizCode, Throwable e) {
        super(message, bizCode, e, HttpStatus.FORBIDDEN);
    }

    public AccessDeniedException(BizCode bizCode, Throwable e) {
        super(bizCode, e, HttpStatus.FORBIDDEN);
    }

    public AccessDeniedException(BizCode bizCode, Throwable e, Object... userMessageParams) {
        super(bizCode, e, HttpStatus.FORBIDDEN, userMessageParams);
    }
}
