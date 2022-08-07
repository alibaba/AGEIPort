package com.alibaba.ageiport.processor.core.model.api;

import java.util.List;

/**
 * @author lingyi
 */
public interface BizDynamicColumnHeader {
    String getFieldName();

    List<BizColumnHeader> getFlatColumnHeaders();
}
