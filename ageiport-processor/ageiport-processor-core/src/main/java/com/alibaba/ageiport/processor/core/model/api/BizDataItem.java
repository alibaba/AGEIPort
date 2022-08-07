package com.alibaba.ageiport.processor.core.model.api;

/**
 * @author lingyi
 */
public interface BizDataItem<VIEW> {

    String getCode();

    VIEW getData();
}
