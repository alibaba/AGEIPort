package com.alibaba.ageiport.common.function;

/**
 * 无参数的函数对象<br>
 * 一个函数接口代表一个一个函数，用于包装一个函数为对象<br>
 *
 * @param <R> 返回值类型
 * @author xuechao.sxc
 */
@FunctionalInterface
public interface Func0<R> {
    /**
     * 执行函数
     *
     * @return 函数执行结果
     * @throws Exception 自定义异常
     */
    R call() throws Exception;
}
