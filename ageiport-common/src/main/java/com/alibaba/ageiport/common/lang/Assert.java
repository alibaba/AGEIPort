package com.alibaba.ageiport.common.lang;

import com.alibaba.ageiport.common.exception.AbstractStandardException;
import com.alibaba.ageiport.common.exception.AccessDeniedException;
import com.alibaba.ageiport.common.exception.InvalidParamException;
import com.alibaba.ageiport.common.exception.NotFoundException;
import com.alibaba.ageiport.common.exception.StandardBusinessException;
import com.alibaba.ageiport.common.exception.UnauthorizedException;

/**
 * Business assert
 * 常用的一些断言，不满足断言条件将抛出一些内置的运行时异常，可以指定业务错误码和业务提示消息。<br>
 * 如果常见断言不满足业务，各系统可继承 {@link AbstractStandardException}，可直接抛出。
 *
 * @author lingyi
 **/
public class Assert {
    private Assert() {
        throw new UnsupportedOperationException("This is a util class, cannot be instantiated");
    }

    /*** AccessDeniedException 400 ***/

    /**
     * 是否允许访问，expression 为false时抛出{@link AccessDeniedException},可以指定业务系统提示消息
     *
     * @param expression
     * @param message
     */
    public static void allowed(boolean expression, String message) {
        if (!expression) {
            justDenied(message);
        }
    }

    /**
     * 是否允许访问，expression 为false时抛出{@link AccessDeniedException},可以指定业务系统提示消息
     *
     * @param expression
     * @param message
     * @param bizCode
     */
    public static void allowed(boolean expression, String message, BizCode bizCode) {
        if (!expression) {
            justDenied(message, bizCode);
        }
    }

    /**
     * 是否允许访问，expression 为false时抛出{@link AccessDeniedException},可以指定业务系统提示消息
     *
     * @param expression
     * @param bizCode
     */
    public static void allowed(boolean expression, BizCode bizCode) {
        if (!expression) {
            justDenied(bizCode);
        }
    }

    /**
     * 是否允许访问，expression 为false时抛出{@link AccessDeniedException},可以指定业务系统提示消息
     *
     * @param expression
     * @param bizCode
     * @param userMessageParams
     */
    public static void allowed(boolean expression, BizCode bizCode, Object... userMessageParams) {
        if (!expression) {
            justDenied(bizCode, userMessageParams);
        }
    }

    /**
     * 抛出{@link AccessDeniedException},可以指定业务系统提示消息
     *
     * @param message
     */
    public static void justDenied(String message) {
        throw new AccessDeniedException(message);
    }

    /**
     * 抛出{@link AccessDeniedException},可以指定业务系统提示消息
     *
     * @param message
     * @param bizCode
     */
    public static void justDenied(String message, BizCode bizCode) {
        throw new AccessDeniedException(message, bizCode, null);
    }

    /**
     * 抛出{@link AccessDeniedException},可以指定业务系统提示消息
     *
     * @param bizCode
     */
    public static void justDenied(BizCode bizCode) {
        throw new AccessDeniedException(bizCode, null);
    }

    /**
     * 抛出{@link AccessDeniedException},可以指定业务系统提示消息
     *
     * @param bizCode
     * @param userMessageParams
     */
    public static void justDenied(BizCode bizCode, Object... userMessageParams) {
        throw new AccessDeniedException(bizCode, null, userMessageParams);
    }

    /*** InvalidParamException 400 ***/

    /**
     * 参数是否非法，expression为false时抛出{@link InvalidParamException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param message
     */
    public static void validParam(boolean expression, String message) {
        if (!expression) {
            justInvalidParam(message);
        }
    }

    /**
     * 参数是否非法，expression为false时抛出{@link InvalidParamException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param message
     * @param bizCode
     */
    public static void validParam(boolean expression, String message, BizCode bizCode) {
        if (!expression) {
            justInvalidParam(message, bizCode);
        }
    }

    /**
     * 参数是否非法，expression为false时抛出{@link InvalidParamException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param bizCode
     */
    public static void validParam(boolean expression, BizCode bizCode) {
        if (!expression) {
            justInvalidParam(bizCode);
        }
    }

    /**
     * 参数是否非法，expression为false时抛出{@link InvalidParamException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param bizCode
     * @param userMessageParams
     */
    public static void validParam(boolean expression, BizCode bizCode, Object... userMessageParams) {
        if (!expression) {
            justInvalidParam(bizCode, userMessageParams);
        }
    }

    /**
     * 直接抛出{@link InvalidParamException},可以指定业务系统的错误码和提示消息.
     *
     * @param message
     */
    public static void justInvalidParam(String message) {
        throw new InvalidParamException(message);
    }

    /**
     * 直接抛出{@link InvalidParamException},可以指定业务系统的错误码和提示消息.
     *
     * @param message
     * @param bizCode
     */
    public static void justInvalidParam(String message, BizCode bizCode) {
        throw new InvalidParamException(message, bizCode, null);
    }

    /**
     * 直接抛出{@link InvalidParamException},可以指定业务系统的错误码和提示消息.
     *
     * @param bizCode
     */
    public static void justInvalidParam(BizCode bizCode) {
        throw new InvalidParamException(bizCode, null);
    }

    /**
     * 直接抛出{@link InvalidParamException},可以指定业务系统的错误码和提示消息.
     *
     * @param bizCode
     * @param userMessageParams
     */
    public static void justInvalidParam(BizCode bizCode, Object... userMessageParams) {
        throw new InvalidParamException(bizCode, null, userMessageParams);
    }

    /*** NotFoundException 400 ***/

    /**
     * 是否存在，expression为false时抛出{@link NotFoundException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param message
     */
    public static void found(boolean expression, String message) {
        if (!expression) {
            justNotFound(message);
        }
    }

    /**
     * 是否存在，expression为false时抛出{@link NotFoundException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param message
     * @param bizCode
     */
    public static void found(boolean expression, String message, BizCode bizCode) {
        if (!expression) {
            justNotFound(message, bizCode);
        }
    }

    /**
     * 是否存在，expression为false时抛出{@link NotFoundException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param bizCode
     */
    public static void found(boolean expression, BizCode bizCode) {
        if (!expression) {
            justNotFound(bizCode);
        }
    }

    /**
     * 是否存在，expression为false时抛出{@link NotFoundException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param bizCode
     * @param userMessageParams
     */
    public static void found(boolean expression, BizCode bizCode, Object... userMessageParams) {
        if (!expression) {
            justNotFound(bizCode, userMessageParams);
        }
    }

    /**
     * 直接抛出{@link NotFoundException},可以指定业务系统的错误码和提示消息.
     *
     * @param message
     */
    public static void justNotFound(String message) {
        throw new NotFoundException(message);
    }

    /**
     * 直接抛出{@link NotFoundException},可以指定业务系统的错误码和提示消息.
     *
     * @param message
     * @param bizCode
     */
    public static void justNotFound(String message, BizCode bizCode) {
        throw new NotFoundException(message, bizCode, null);
    }

    /**
     * 直接抛出{@link NotFoundException},可以指定业务系统的错误码和提示消息.
     *
     * @param bizCode
     */
    public static void justNotFound(BizCode bizCode) {
        throw new NotFoundException(bizCode, null);
    }

    /**
     * 直接抛出{@link NotFoundException},可以指定业务系统的错误码和提示消息.
     *
     * @param bizCode
     * @param userMessageParams
     */
    public static void justNotFound(BizCode bizCode, Object... userMessageParams) {
        throw new NotFoundException(bizCode, null, userMessageParams);
    }

    /*** StandardBusinessException 400 ***/

    /**
     * 是否通过校验，expression为false时抛出{@link StandardBusinessException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param message
     */
    public static void pass(boolean expression, String message) {
        if (!expression) {
            justFailed(message);
        }
    }

    /**
     * 是否通过校验，expression为false时抛出{@link StandardBusinessException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param message
     * @param bizCode
     */
    public static void pass(boolean expression, String message, BizCode bizCode) {
        if (!expression) {
            justFailed(message, bizCode);
        }
    }

    /**
     * 是否通过校验，expression为false时抛出{@link StandardBusinessException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param bizCode
     */
    public static void pass(boolean expression, BizCode bizCode) {
        if (!expression) {
            justFailed(bizCode);
        }
    }

    /**
     * 是否通过校验，expression为false时抛出{@link StandardBusinessException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param bizCode
     * @param userMessageParams
     */
    public static void pass(boolean expression, BizCode bizCode, Object... userMessageParams) {
        if (!expression) {
            justFailed(bizCode, userMessageParams);
        }
    }

    /**
     * 直接抛出{@link StandardBusinessException},可以指定业务系统的错误码和提示消息.
     *
     * @param message
     */
    public static void justFailed(String message) {
        throw new StandardBusinessException(message);
    }

    /**
     * 直接抛出{@link StandardBusinessException},可以指定业务系统的错误码和提示消息.
     *
     * @param message
     * @param bizCode
     */
    public static void justFailed(String message, BizCode bizCode) {
        throw new StandardBusinessException(message, bizCode, null);
    }

    /**
     * 直接抛出{@link StandardBusinessException},可以指定业务系统的错误码和提示消息.
     *
     * @param bizCode
     */
    public static void justFailed(BizCode bizCode) {
        throw new StandardBusinessException(bizCode, null);
    }

    /**
     * 直接抛出{@link StandardBusinessException},可以指定业务系统的错误码和提示消息.
     *
     * @param bizCode
     * @param userMessageParams
     */
    public static void justFailed(BizCode bizCode, Object... userMessageParams) {
        throw new StandardBusinessException(bizCode, null, userMessageParams);
    }

    /*** UnauthorizedException 400 ***/

    /**
     * 是否授权，expression为false时抛出{@link UnauthorizedException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param message
     */
    public static void authorized(boolean expression, String message) {
        if (!expression) {
            justUnauthorized(message);
        }
    }

    /**
     * 是否授权，expression为false时抛出{@link UnauthorizedException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param message
     * @param bizCode
     */
    public static void authorized(boolean expression, String message, BizCode bizCode) {
        if (!expression) {
            justUnauthorized(message, bizCode);
        }
    }

    /**
     * 是否授权，expression为false时抛出{@link UnauthorizedException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param bizCode
     */
    public static void authorized(boolean expression, BizCode bizCode) {
        if (!expression) {
            justUnauthorized(bizCode);
        }
    }

    /**
     * 是否授权，expression为false时抛出{@link UnauthorizedException},可以指定业务系统的错误码和提示消息.
     *
     * @param expression
     * @param bizCode
     * @param userMessageParams
     */
    public static void authorized(boolean expression, BizCode bizCode, Object... userMessageParams) {
        if (!expression) {
            justUnauthorized(bizCode, userMessageParams);
        }
    }

    /**
     * 直接抛出{@link UnauthorizedException},可以指定业务系统的错误码和提示消息.
     *
     * @param message
     */
    public static void justUnauthorized(String message) {
        throw new UnauthorizedException(message);
    }

    /**
     * 直接抛出{@link UnauthorizedException},可以指定业务系统的错误码和提示消息.
     *
     * @param message
     * @param bizCode
     */
    public static void justUnauthorized(String message, BizCode bizCode) {
        throw new UnauthorizedException(message, bizCode, null);
    }

    /**
     * 直接抛出{@link UnauthorizedException},可以指定业务系统的错误码和提示消息.
     *
     * @param bizCode
     */
    public static void justUnauthorized(BizCode bizCode) {
        throw new UnauthorizedException(bizCode, null);
    }

    /**
     * 直接抛出{@link UnauthorizedException},可以指定业务系统的错误码和提示消息.
     *
     * @param bizCode
     * @param userMessageParams
     */
    public static void justUnauthorized(BizCode bizCode, Object... userMessageParams) {
        throw new UnauthorizedException(bizCode, null, userMessageParams);
    }
}
