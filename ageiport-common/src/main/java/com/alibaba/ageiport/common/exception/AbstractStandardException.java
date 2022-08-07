package com.alibaba.ageiport.common.exception;

import com.alibaba.ageiport.common.constants.HttpStatus;
import com.alibaba.ageiport.common.lang.BizCode;
import com.alibaba.ageiport.common.utils.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Abstract class of all exception that should handle
 *
 * @author lingyi
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractStandardException extends RuntimeException {

    private String code = "unknown";
    private int httpStatus;

    public AbstractStandardException(HttpStatus httpStatus) {
        super(null, null, false, true);
        this.httpStatus = httpStatus.value();
    }

    public AbstractStandardException(String message, HttpStatus httpStatus) {
        super(message, null, false, true);
        this.httpStatus = httpStatus.value();
    }

    public AbstractStandardException(Throwable e, HttpStatus httpStatus) {
        super(getMessage(null, e, null), e, false, true);
        this.httpStatus = httpStatus.value();
        //pop root exception
        resolveException(e);
    }

    public AbstractStandardException(String message, Throwable e, HttpStatus httpStatus) {
        super(getMessage(message, e, null), e, false, true);
        this.httpStatus = httpStatus.value();
        //pop root exception
        resolveException(e);
    }

    public AbstractStandardException(String message, BizCode bizCode, Throwable e, HttpStatus httpStatus) {
        super(getMessage(message, e, bizCode), e, false, true);
        this.code = bizCode.getCode();
        this.httpStatus = httpStatus.value();
        //pop root exception
        resolveException(e);
    }

    public AbstractStandardException(BizCode code, Throwable e, HttpStatus httpStatus) {
        super(getMessage(null, e, code), e, false, true);
        this.code = code.getCode();
        this.httpStatus = httpStatus.value();
        //pop root exception
        resolveException(e);
    }

    public AbstractStandardException(BizCode bizCode, Throwable e, HttpStatus httpStatus, Object... userMessageParams) {
        super(getMessage(null, e, bizCode, userMessageParams), e, false, e == null);
        this.code = bizCode.getCode();
        this.httpStatus = httpStatus.value();
        //pop root exception
        resolveException(e);
    }


    public static Throwable causeOf(Throwable e) {
        int count = 0;
        Throwable exception = e;
        while (exception != null && count++ < 50) {
            if (exception instanceof AbstractStandardException) {
                return exception;
            }
            final Throwable cause = e.getCause();
            if (cause == exception) {
                break;
            }
            exception = cause;
        }
        return e;
    }

    private static String getMessage(String existMessage, Throwable e, BizCode bizCode, Object... params) {
        Throwable throwable = causeOf(e);

        String messagePrefix = StringUtils.isBlank(existMessage) ? "" : existMessage + "; ";

        if (throwable instanceof AbstractStandardException) {
            if (StringUtils.isBlank(throwable.getMessage())) {
                if (StringUtils.isBlank(existMessage)) {
                    return "no message";
                } else {
                    return existMessage;
                }
            } else {
                return messagePrefix + throwable.getMessage();
            }
        }

        if (bizCode != null) {
            return messagePrefix + bizCode.parseMessage(params);
        }

        if (StringUtils.isBlank(throwable.getMessage())) {
            if (StringUtils.isBlank(existMessage)) {
                return "no message";
            } else {
                return existMessage;
            }
        } else {
            if (StringUtils.isBlank(existMessage)) {
                return throwable.getMessage();
            } else {
                return messagePrefix + throwable.getMessage();
            }
        }
    }

    private void resolveException(Throwable e) {
        Throwable throwable = causeOf(e);
        if (throwable instanceof AbstractStandardException) {
            this.code = ((AbstractStandardException) e).getCode();
            this.httpStatus = ((AbstractStandardException) e).getHttpStatus();
        }
    }
}
