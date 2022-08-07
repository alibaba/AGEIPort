package com.alibaba.ageiport.common.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author lingyi
 */
public interface Fn<T, R> extends Function<T, R>, Serializable {
}
