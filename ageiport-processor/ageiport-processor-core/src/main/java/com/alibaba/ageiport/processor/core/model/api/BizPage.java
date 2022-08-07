package com.alibaba.ageiport.processor.core.model.api;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lingyi
 */
public interface BizPage extends Serializable {

    Integer getNo();

    Map<String, String> getAttributes();
}
