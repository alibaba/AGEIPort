package com.alibaba.ageiport.processor.core.model.api;

import java.util.List;

/**
 * @author lingyi
 */
public interface BizDynamicColumnHeaders {

    List<BizDynamicColumnHeader> getBizDynamicColumnHeaders();

    BizDynamicColumnHeader getBizDynamicColumnHeaderByFiledName(String filedName);
}
