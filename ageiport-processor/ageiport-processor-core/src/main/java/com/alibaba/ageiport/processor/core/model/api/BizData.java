package com.alibaba.ageiport.processor.core.model.api;

import java.util.List;

/**
 * @author lingyi
 */
public interface BizData<VIEW> {

    String getName();

    List<BizDataItem<VIEW>> getItems();
}
