package com.alibaba.ageiport.processor.core.model.api.impl;

import com.alibaba.ageiport.processor.core.model.api.BizData;
import com.alibaba.ageiport.processor.core.model.api.BizDataGroup;
import com.alibaba.ageiport.processor.core.model.api.BizDataItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class BizDataGroupImpl<T> implements BizDataGroup<T> {

    List<BizData<T>> data;

    @Override
    public List<BizData<T>> getBizData() {
        return data;
    }

    @Getter
    @Setter
    public static class Data<T> implements BizData<T> {

        private String name;

        private List<BizDataItem<T>> items;
    }

    @Getter
    @Setter
    public static class Item<T> implements BizDataItem<T> {

        private String code;

        private T data;
    }

}