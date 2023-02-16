package com.alibaba.ageiport.test.processor.core.exporter;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.annotation.ExportSpecification;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.file.excel.ExcelConstants;
import com.alibaba.ageiport.processor.core.model.api.BizDataGroup;
import com.alibaba.ageiport.processor.core.model.api.BizDataItem;
import com.alibaba.ageiport.processor.core.model.api.BizExportPage;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.api.impl.BizDataGroupImpl;
import com.alibaba.ageiport.processor.core.task.exporter.ExportProcessor;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.test.processor.core.model.Data;
import com.alibaba.ageiport.test.processor.core.model.Query;
import com.alibaba.ageiport.test.processor.core.model.View;

import java.util.*;

//1.实现ExportProcessor接口
@ExportSpecification(code = "UdfSliceStrategyExportProcessor", name = "UdfSliceStrategyExportProcessor", sliceStrategy = "UdfExportSliceStrategy")
public class UdfSliceStrategyExportProcessor implements ExportProcessor<Query, Data, View> {

    //2.实现ExportProcessor接口的TotalCount方法
    @Override
    public Integer totalCount(BizUser bizUser, Query query) throws BizException {
        return query.getTotalCount();
    }

    //3.实现ExportProcessor接口的queryData方法
    @Override
    public List<Data> queryData(BizUser user, Query query, BizExportPage bizExportPage) throws BizException {
        List<Data> data = new ArrayList<>();
        String sliceKey = query.getSliceKey();
        if (Objects.equals(sliceKey, "男")) {
            data = queryMan(user, query, bizExportPage);
        } else if (Objects.equals(sliceKey, "女")) {
            data = queryWomen(user, query, bizExportPage);
        } else if (Objects.equals(sliceKey, "其他")) {
            data = queryOthers(user, query, bizExportPage);
        }
        return data;
    }

    private List<Data> queryOthers(BizUser user, Query query, BizExportPage bizExportPage) {
        List<Data> dataList = new ArrayList<>();
        Integer offset = bizExportPage.getOffset();
        Integer size = bizExportPage.getSize();
        for (int i = 1; i <= size; i++) {
            int index = offset + i;
            final Data data = new Data();
            data.setId(index);
            data.setName("name" + index);
            data.setGender("其他");
            data.setGroupIndex(2);
            data.setGroupName("其他性别问卷");
            data.setOtherQuestion1("其他性别问题回答1");
            data.setOtherQuestion2("其他性别问题回答2");
            dataList.add(data);
        }
        return dataList;
    }

    private List<Data> queryWomen(BizUser user, Query query, BizExportPage bizExportPage) {
        List<Data> dataList = new ArrayList<>();
        Integer offset = bizExportPage.getOffset();
        Integer size = bizExportPage.getSize();
        for (int i = 1; i <= size; i++) {
            int index = offset + i;
            final Data data = new Data();
            data.setId(index);
            data.setName("name" + index);
            data.setGender("女");
            data.setGroupIndex(1);
            data.setGroupName("女性性别问卷");
            data.setWomenQuestion1("女性性别问题回答1");
            data.setWomenQuestion2("女性性别问题回答2");
            dataList.add(data);
        }
        return dataList;
    }

    private List<Data> queryMan(BizUser user, Query query, BizExportPage bizExportPage) {
        List<Data> dataList = new ArrayList<>();
        Integer offset = bizExportPage.getOffset();
        Integer size = bizExportPage.getSize();
        for (int i = 1; i <= size; i++) {
            int index = offset + i;
            final Data data = new Data();
            data.setId(index);
            data.setName("name" + index);
            data.setGender("男");
            data.setGroupIndex(0);
            data.setGroupName("男性性别问卷");
            data.setManQuestion1("男性别问题回答1");
            data.setManQuestion2("男性别问题回答2");
            dataList.add(data);
        }
        return dataList;
    }

    //4.实现ExportProcessor接口的convert方法
    @Override
    public List<View> convert(BizUser user, Query query, List<Data> data) throws BizException {
        List<View> dataList = new ArrayList<>();
        for (Data datum : data) {
            View view = BeanUtils.cloneProp(datum, View.class);
            dataList.add(view);
        }
        return dataList;
    }

    @Override
    public BizDataGroup<View> group(BizUser user, Query query, List<View> views) {
        BizDataGroupImpl<View> group = new BizDataGroupImpl<>();

        View view = views.stream().distinct().findFirst().get();
        BizDataGroupImpl.Data<View> data = new BizDataGroupImpl.Data<>();
        List<BizDataItem<View>> items = new ArrayList<>();
        data.setItems(items);
        Map<String, String> meta = new HashMap<>();
        meta.put(ExcelConstants.sheetNoKey, view.getGroupIndex().toString());
        meta.put(ExcelConstants.sheetNameKey, view.getGroupName());
        data.setMeta(meta);
        data.setCode(JsonUtil.toJsonString(meta));
        group.setData(Lists.newArrayList(data));

        for (View v : views) {
            BizDataGroupImpl.Item<View> item = new BizDataGroupImpl.Item<>();
            item.setData(v);
            items.add(item);
        }

        return group;
    }

    @Override
    public BizExportTaskRuntimeConfig taskRuntimeConfig(BizUser user, Query query) throws BizException {
        final BizExportTaskRuntimeConfigImpl runtimeConfig = new BizExportTaskRuntimeConfigImpl();
        runtimeConfig.setExecuteType("STANDALONE");
        return runtimeConfig;
    }

}
