package com.alibaba.ageiport.processor.core.model.api;

import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public interface BizData<VIEW> {

    String getCode();

    Map<String, String> getMeta();

    List<BizDataItem<VIEW>> getItems();
}
