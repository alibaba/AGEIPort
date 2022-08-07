package com.alibaba.ageiport.common.model;

import com.alibaba.ageiport.common.exception.AbstractStandardException;
import com.alibaba.ageiport.common.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * 基础结果
 *
 * @author lingyi
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -8056357560350402602L;

    protected boolean success;

    protected String code;
    protected String msg;
    protected T data;

    public Result(boolean success) {
        this.success = success;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>(true);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> faled(Throwable e, T data) {
        Result<T> faled = faled(e);
        faled.setData(data);
        return faled;
    }

    public static <T> Result<T> faled(Throwable e) {
        Result<T> result = new Result<T>(false);
        if (e instanceof AbstractStandardException) {
            result.setCode(((AbstractStandardException) e).getCode());
            result.setMsg(StringUtils.isBlank(e.getMessage()) ? "no message" : e.getMessage());
        } else {
            result.setCode("unknown");
            result.setMsg(StringUtils.isBlank(e.getMessage()) ? "no message" : e.getMessage());
        }
        return result;
    }

    public static <T> Result<T> faled(String code, String msg) {
        Result<T> result = new Result<T>(false);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> faled(String code, String msg, T data) {
        Result<T> result = new Result<T>(false);
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public boolean isSuccessAndNotNull() {
        return success && data != null;
    }

    public boolean isSuccessAndPageNotEmpty() {
        Page page = (Page) data;
        return success && !page.isEmpty();
    }
}
