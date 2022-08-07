package com.alibaba.ageiport.processor.core;

import com.alibaba.ageiport.ext.arch.SPI;

/**
 * @author lingyi
 */
@SPI
public interface Processor {

    ThreadLocal<Context> threadLocalContext = new ThreadLocal<>();


    default <CONTEXT extends Context> void setContext(CONTEXT context) {
        threadLocalContext.set(context);
    }

    default <CONTEXT extends Context> CONTEXT getContext() {
        return (CONTEXT) threadLocalContext.get();
    }

    default <CONTEXT extends Context> void clearContext() {
        threadLocalContext.remove();
    }

    String resolver();
}
