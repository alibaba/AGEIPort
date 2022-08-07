package com.alibaba.ageiport.common.exception;


import com.alibaba.ageiport.common.constants.HttpStatus;
import com.alibaba.ageiport.common.lang.BizCode;

/**
 * unauthorized exception
 *
 * @author lingyi 2020-08-03
 **/
public class StandardBusinessException extends AbstractStandardException {

    public StandardBusinessException() {
        super(HttpStatus.EXPECTATION_FAILED);
    }

    public StandardBusinessException(String message) {
        super(message, HttpStatus.EXPECTATION_FAILED);
    }

    public StandardBusinessException(Throwable e) {
        super(e, HttpStatus.EXPECTATION_FAILED);
    }

    public StandardBusinessException(String message, Throwable e) {
        super(message, e, HttpStatus.EXPECTATION_FAILED);
    }

    public StandardBusinessException(String message, BizCode bizCode, Throwable e) {
        super(message, bizCode, e, HttpStatus.EXPECTATION_FAILED);
    }

    public StandardBusinessException(BizCode bizCode, Throwable e) {
        super(bizCode, e, HttpStatus.EXPECTATION_FAILED);
    }

    public StandardBusinessException(BizCode bizCode, Throwable e, Object... userMessageParams) {
        super(bizCode, e, HttpStatus.EXPECTATION_FAILED, userMessageParams);
    }
}
