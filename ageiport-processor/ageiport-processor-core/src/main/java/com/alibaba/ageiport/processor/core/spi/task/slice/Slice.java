package com.alibaba.ageiport.processor.core.spi.task.slice;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lingyi
 */
public interface Slice extends Serializable {

    Integer getNo();

    String getQueryJson();
}
