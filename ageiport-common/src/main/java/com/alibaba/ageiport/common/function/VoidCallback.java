package com.alibaba.ageiport.common.function;

/**
 * 无参数和返回的函数对象<br>
 *
 * @author xuechao.sxc
 */
@FunctionalInterface
public interface VoidCallback {

    /**
     * 执行
     */
    void call();
}
