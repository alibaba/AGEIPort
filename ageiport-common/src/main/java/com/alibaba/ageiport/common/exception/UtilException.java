package com.alibaba.ageiport.common.exception;

import com.alibaba.ageiport.common.constants.HttpStatus;

/**
 * 工具类异常
 *
 * @author lingyi
 */
public class UtilException extends AbstractStandardException {
    private static final long serialVersionUID = 8247610319171014183L;

    public UtilException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public UtilException(String message, Throwable e) {
        super(message, e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
