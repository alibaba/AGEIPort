package com.alibaba.ageiport.processor.core.spi.task.slice;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.Context;

import java.util.List;

/**
 * @author lingyi
 */
@SPI
public interface SliceStrategy<T extends Slice, CONTEXT extends Context> {

    List<T> slice(CONTEXT context);

}
