package com.alibaba.ageiport.processor.core.spi.cache;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;

/**
 * @author lingyi
 */
@SPI
public interface BigDataCache {


    void init(AgeiPort ageiPort);

    <T> void put(String key, T value);

    <T> T get(String key, Class<T> clazz);

    <T> T remove(String key, Class<T> clazz);

    boolean exist(String key);
}
