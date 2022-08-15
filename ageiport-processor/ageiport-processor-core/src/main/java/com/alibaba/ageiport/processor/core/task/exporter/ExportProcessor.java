package com.alibaba.ageiport.processor.core.task.exporter;

import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.Processor;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.model.api.*;
import com.alibaba.ageiport.processor.core.model.api.impl.BizDataGroupImpl;
import com.alibaba.ageiport.processor.core.spi.Adapter;
import com.alibaba.ageiport.processor.core.spi.convertor.Model;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.task.exporter.adapter.StandardExportProcessorAdapter;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public interface ExportProcessor<QUERY, DATA, VIEW> extends Processor {

    default String resolver() {
        return "ExportSpecificationResolver";
    }

    default Adapter getConcreteAdapter() {
        String name = StandardExportProcessorAdapter.class.getSimpleName();
        return ExtensionLoader.getExtensionLoader(Adapter.class).getExtension(name);
    }

    default BizExportTaskRuntimeConfig taskRuntimeConfig(BizUser user, QUERY query) throws BizException {
        return null;
    }

    default QUERY resetQuery(BizUser bizUser, QUERY query) throws BizException {
        return null;
    }

    Integer totalCount(BizUser bizUser, QUERY query) throws BizException;

    default BizColumnHeaders getHeaders(BizUser user, QUERY query) throws BizException {
        return null;
    }

    default BizDynamicColumnHeaders getDynamicHeaders(BizUser user, QUERY query) throws BizException {
        return null;
    }

    List<DATA> queryData(BizUser user, QUERY query, BizExportPage bizExportPage) throws BizException;

    List<VIEW> convert(BizUser user, QUERY query, List<DATA> data) throws BizException;

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
            data.setName(bizDatum.getName());
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
