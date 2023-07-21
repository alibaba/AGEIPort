package com.alibaba.ageiport.processor.core.task.importer;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.TypeUtils;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.Processor;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.model.api.*;
import com.alibaba.ageiport.processor.core.model.api.impl.BizDataGroupImpl;
import com.alibaba.ageiport.processor.core.spi.Adapter;
import com.alibaba.ageiport.processor.core.spi.convertor.Model;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.task.importer.adapter.StandardImportProcessorAdapter;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public interface ImportProcessor<QUERY, DATA, VIEW> extends Processor {

    @Override
    default String resolver() {
        return "ImportSpecificationResolver";
    }

    default Adapter getConcreteAdapter() {
        String name = StandardImportProcessorAdapter.class.getSimpleName();
        return ExtensionLoader.getExtensionLoader(Adapter.class).getExtension(name);
    }

    default QUERY resetQuery(BizUser bizUser, QUERY query) throws BizException {
        return null;
    }

    default BizImportTaskRuntimeConfig taskRuntimeConfig(BizUser user, QUERY query) throws BizException {
        return null;
    }

    default BizColumnHeaders getHeaders(BizUser user, QUERY query) throws BizException {
        return null;
    }

    default BizDynamicColumnHeaders getDynamicHeaders(BizUser user, QUERY query) throws BizException {
        return null;
    }

    default DataGroup checkHeaders(BizUser user, QUERY query, DataGroup group) throws BizException {
        return group;
    }

    default BizDataGroup<VIEW> getBizDataGroup(BizUser user, QUERY query, DataGroup group) throws BizException {
        List<Type> genericParamType = TypeUtils.getGenericParamType(this.getClass(), ImportProcessor.class);
        Class<VIEW> viewClass = genericParamType.size() > 2 ? (Class<VIEW>) genericParamType.get(2) : null;

        BizDataGroupImpl<VIEW> bizDataGroup = new BizDataGroupImpl<>();
        List<BizData<VIEW>> bizDataList = new ArrayList<>();
        bizDataGroup.setData(bizDataList);

        for (DataGroup.Data data : group.getData()) {
            List<BizDataItem<VIEW>> items = new ArrayList<>();
            BizDataGroupImpl.Data<VIEW> bizData = new BizDataGroupImpl.Data<>();
            bizDataList.add(bizData);
            bizData.setCode(data.getCode());
            bizData.setMeta(data.getMeta());
            bizData.setItems(items);
            for (DataGroup.Item item : data.getItems()) {
                VIEW view = JsonUtil.toObject("{}", viewClass);
                Map<String, Object> values = new HashMap<>(item.getValues());
                values.putAll(data.getMeta());
                Model.toModel(values, view);
                BizDataGroupImpl.Item<VIEW> dataItem = new BizDataGroupImpl.Item<>();
                dataItem.setCode(item.getCode());
                dataItem.setData(view);
                items.add(dataItem);
            }
        }
        return bizDataGroup;
    }

    default List<VIEW> flat(BizUser user, QUERY query, BizDataGroup<VIEW> views) throws BizException {
        List<VIEW> result = new ArrayList<>();
        List<BizData<VIEW>> bizData = views.getBizData();
        for (BizData<VIEW> bizDatum : bizData) {
            List<BizDataItem<VIEW>> items = bizDatum.getItems();
            for (BizDataItem<VIEW> item : items) {
                VIEW data = item.getData();
                result.add(data);
            }
        }
        return result;
    }

    BizImportResult<VIEW, DATA> convertAndCheck(BizUser user, QUERY query, List<VIEW> views);

    BizImportResult<VIEW, DATA> write(BizUser user, QUERY query, List<DATA> data);

    default BizDataGroup<VIEW> group(BizUser user, QUERY query, List<VIEW> views) {
        BizDataGroupImpl<VIEW> group = new BizDataGroupImpl<>();
        BizDataGroupImpl.Data<VIEW> data = new BizDataGroupImpl.Data<>();
        List<BizData<VIEW>> dataList = new ArrayList<>();
        dataList.add(data);
        group.setData(dataList);
        List<BizDataItem<VIEW>> items = new ArrayList<>();
        data.setItems(items);
        for (VIEW view : views) {
            BizDataGroupImpl.Item<VIEW> item = new BizDataGroupImpl.Item<>();
            item.setData(view);
            items.add(item);
        }
        return group;
    }

    default DataGroup getDataGroup(BizUser user, QUERY query, BizDataGroup<VIEW> bizDataGroup) {
        DataGroup dataGroup = new DataGroup();
        List<DataGroup.Data> dataList = new ArrayList<>();
        dataGroup.setData(dataList);
        List<BizData<VIEW>> bizData = bizDataGroup.getBizData();
        for (BizData<VIEW> bizDatum : bizData) {
            DataGroup.Data data = new DataGroup.Data();
            dataList.add(data);
            data.setCode(bizDatum.getCode());
            data.setMeta(bizDatum.getMeta());
            List<DataGroup.Item> items = new ArrayList<>();
            data.setItems(items);
            List<BizDataItem<VIEW>> bizDataItems = bizDatum.getItems();
            for (BizDataItem<VIEW> bizDataItem : bizDataItems) {
                Map<String, Object> map = Model.toMap(bizDataItem.getData());
                DataGroup.Item item = new DataGroup.Item();
                item.setValues(map);
                items.add(item);
            }
        }
        return dataGroup;
    }


}
