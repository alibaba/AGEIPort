package com.alibaba.ageiport.test.processor.core.exporter;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.DateUtils;
import com.alibaba.ageiport.processor.core.annotation.ExportSpecification;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.model.api.*;
import com.alibaba.ageiport.processor.core.model.api.impl.BizColumnHeaderImpl;
import com.alibaba.ageiport.processor.core.model.api.impl.BizDynamicColumnHeaderImpl;
import com.alibaba.ageiport.processor.core.model.api.impl.BizDynamicColumnHeadersImpl;
import com.alibaba.ageiport.processor.core.task.exporter.ExportProcessor;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.test.processor.core.model.Data;
import com.alibaba.ageiport.test.processor.core.model.DynamicColumnView;
import com.alibaba.ageiport.test.processor.core.model.Query;

import java.util.*;


//1.实现ExportProcessor接口
@ExportSpecification(code = "DynamicColumnExportProcessor", name = "DynamicColumnExportProcessor")
public class DynamicColumnExportProcessor implements ExportProcessor<Query, Data, DynamicColumnView> {

    //2.实现ExportProcessor接口的TotalCount方法
    @Override
    public Integer totalCount(BizUser bizUser, Query query) throws BizException {
        return query.getTotalCount();
    }

    @Override
    public BizDynamicColumnHeaders getDynamicHeaders(BizUser user, Query query) throws BizException {
        //一般此接口返回值是由query查询数据库or接口获取到的，此处仅为示例，直接由入参传入并构造动态列

        Integer dynamicHeaderCount = query.getDynamicHeaderCount();
        List<BizColumnHeader> flatColumnHeadersForQuery = new ArrayList<>();
        for (int i = 1; i <= dynamicHeaderCount; i++) {
            BizColumnHeaderImpl columnHeader = new BizColumnHeaderImpl();
            columnHeader.setHeaderName("查询参数动态列" + i);
            final String dynamicColumnKey = "key" + i;
            columnHeader.setDynamicColumnKey(dynamicColumnKey);
            flatColumnHeadersForQuery.add(columnHeader);
        }
        BizDynamicColumnHeaderImpl bizDynamicColumnHeaderForQuery = new BizDynamicColumnHeaderImpl();
        bizDynamicColumnHeaderForQuery.setFieldName("dynamicColumnByQueryParams");
        bizDynamicColumnHeaderForQuery.setFlatColumnHeaders(flatColumnHeadersForQuery);

        List<BizColumnHeader> flatColumnHeadersForDate = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        String format = DateUtils.format(date, DateUtils.PURE_DATE_FORMAT);
        int bizdate = Integer.parseInt(format);
        for (int i = 0; i < 5; i++) {
            String headerKey = (bizdate + i) + "";
            BizColumnHeaderImpl columnHeader = new BizColumnHeaderImpl();
            columnHeader.setHeaderName("日期动态列:" + headerKey);
            String dynamicColumnKey = "key" + headerKey;
            columnHeader.setDynamicColumnKey(dynamicColumnKey);
            flatColumnHeadersForDate.add(columnHeader);
        }
        BizDynamicColumnHeaderImpl bizDynamicColumnHeaderForDate = new BizDynamicColumnHeaderImpl();
        bizDynamicColumnHeaderForDate.setFieldName("dynamicColumnByDate");
        bizDynamicColumnHeaderForDate.setFlatColumnHeaders(flatColumnHeadersForDate);


        BizDynamicColumnHeadersImpl bizDynamicColumnHeaders = new BizDynamicColumnHeadersImpl();
        List<BizDynamicColumnHeader> bizDynamicColumnHeaderList = Lists.newArrayList(bizDynamicColumnHeaderForQuery, bizDynamicColumnHeaderForDate);
        bizDynamicColumnHeaders.setBizDynamicColumnHeaders(bizDynamicColumnHeaderList);

        return bizDynamicColumnHeaders;
    }

    //3.实现ExportProcessor接口的queryData方法
    @Override
    public List<Data> queryData(BizUser user, Query query, BizExportPage bizExportPage) throws BizException {
        List<Data> dataList = new ArrayList<>();

        Integer offset = bizExportPage.getOffset();
        Integer size = bizExportPage.getSize();
        for (int i = 1; i <= size; i++) {
            int index = offset + i;
            final Data data = new Data();
            data.setId(index);
            data.setName("name" + index);
            if (index % 3 == 0) {
                data.setGender("男");
            }
            if (index % 3 == 1) {
                data.setGender("女");
            }
            if (index % 3 == 2) {
                data.setGender("其他");
            }
            dataList.add(data);
        }
        return dataList;
    }


    //4.实现ExportProcessor接口的convert方法
    @Override
    public List<DynamicColumnView> convert(BizUser user, Query query, List<Data> data) throws BizException {
        List<DynamicColumnView> dataList = new ArrayList<>();

        int row = 1;

        for (Data datum : data) {
            DynamicColumnView view = BeanUtils.cloneProp(datum, DynamicColumnView.class);
            dataList.add(view);

            Integer dynamicHeaderCount = query.getDynamicHeaderCount();
            Map<String, Object> dynamicColumnByQueryParams = new HashMap<>();
            for (int i = 1; i <= dynamicHeaderCount; i++) {
                String dynamicColumnKey = "key" + i;
                dynamicColumnByQueryParams.put(dynamicColumnKey, "row:" + row + ",col:" + i);
            }
            view.setDynamicColumnByQueryParams(dynamicColumnByQueryParams);

            Date date = new Date(System.currentTimeMillis());
            String format = DateUtils.format(date, DateUtils.PURE_DATE_FORMAT);
            int bizdate = Integer.parseInt(format);
            Map<String, Object> dynamicColumnByDate = new HashMap<>();
            for (int i = 0; i < 5; i++) {
                String headerKey = (bizdate + i) + "";
                String dynamicColumnKey = "key" + headerKey;
                dynamicColumnByDate.put(dynamicColumnKey, "row:" + row + ",col:" + i);
            }
            view.setDynamicColumnByDate(dynamicColumnByDate);

            row++;
        }

        return dataList;
    }

    @Override
    public BizExportTaskRuntimeConfig taskRuntimeConfig(BizUser user, Query query) throws BizException {
        final BizExportTaskRuntimeConfigImpl runtimeConfig = new BizExportTaskRuntimeConfigImpl();
        runtimeConfig.setExecuteType("STANDALONE");
        return runtimeConfig;
    }
}
