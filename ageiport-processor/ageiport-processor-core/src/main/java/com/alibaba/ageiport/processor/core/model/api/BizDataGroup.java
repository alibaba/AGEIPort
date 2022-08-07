package com.alibaba.ageiport.processor.core.model.api;

import java.util.List;

/**
 * @author lingyi
 */
public interface BizDataGroup<VIEW> {

    List<BizData<VIEW>> getBizData();

}
