package com.alibaba.ageiport.common.exception;


import com.alibaba.ageiport.common.trace.TraceUtils;
import com.alibaba.ageiport.common.utils.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具
 *
 * @author lingyi
 */
public class ExceptionUtils {
    private ExceptionUtils() {
    }

    public static <E extends Throwable> void throwException(Throwable t) throws E {
        throw (E) t;
    }


    public static String stackOf(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        stringWriter.append(throwable.getClass().getName());
        stringWriter.append(":");
        stringWriter.append(throwable.getMessage());
        stringWriter.append(StringUtils.format(", traceId:{}", TraceUtils.getTraceId()));
        stringWriter.append(System.lineSeparator());
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 获得完整消息，包括异常名，消息格式为：{SimpleClassName}: {ThrowableMessage}
     *
     * @param e 异常
     * @return 完整消息
     */
    public static String getMessage(Throwable e) {
        if (null == e) {
            return StringUtils.NULL;
        }
        return StringUtils.format("{}: {}", e.getClass().getSimpleName(), e.getMessage());
    }
}
