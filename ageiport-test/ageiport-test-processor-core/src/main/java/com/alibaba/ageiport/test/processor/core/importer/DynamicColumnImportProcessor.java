package com.alibaba.ageiport.test.processor.core.importer;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.DateUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.annotation.ImportSpecification;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.model.api.BizColumnHeader;
import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeader;
import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeaders;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.api.impl.BizColumnHeaderImpl;
import com.alibaba.ageiport.processor.core.model.api.impl.BizDynamicColumnHeaderImpl;
import com.alibaba.ageiport.processor.core.model.api.impl.BizDynamicColumnHeadersImpl;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResult;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResultImpl;
import com.alibaba.ageiport.test.processor.core.model.Data;
import com.alibaba.ageiport.test.processor.core.model.DynamicColumnView;
import com.alibaba.ageiport.test.processor.core.model.Query;

import java.util.*;


//1.实现ImportProcessor接口
@ImportSpecification(code = "DynamicColumnImportProcessor", name = "DynamicColumnImportProcessor")
public class DynamicColumnImportProcessor implements ImportProcessor<Query, Data, DynamicColumnView> {

    Logger logger = LoggerFactory.getLogger(DynamicColumnImportProcessor.class);


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


    //2.实现ImportProcessor接口的convertAndCheck方法
    @Override
    public BizImportResult<DynamicColumnView, Data> convertAndCheck(BizUser user, Query query, List<DynamicColumnView> views) {
        BizImportResultImpl<DynamicColumnView, Data> result = new BizImportResultImpl<>();

        List<Data> data = new ArrayList<>();
        for (DynamicColumnView view : views) {
            Data datum = new Data();
            datum.setId(view.getId());
            datum.setName(view.getName());
            Map<String, Object> dynamicColumns = new HashMap<>();
            dynamicColumns.putAll(view.getDynamicColumnByDate());
            dynamicColumns.putAll(view.getDynamicColumnByQueryParams());
            datum.setDynamicColumns(dynamicColumns);
            data.add(datum);
        }

        result.setData(data);
        return result;
    }

    //3.实现ExportProcessor接口的write方法，此方法负责执行写入业务逻辑。
    @Override
    public BizImportResult<DynamicColumnView, Data> write(BizUser user, Query query, List<Data> data) {
        BizImportResultImpl<DynamicColumnView, Data> result = new BizImportResultImpl<>();
        for (Data datum : data) {
            logger.info(JsonUtil.toJsonString(datum));
        }
        if (query.isErrorWhenWriteData()) {
            throw new IllegalStateException("Error when write");
        }
        return result;
    }

    @Override
    public BizImportTaskRuntimeConfig taskRuntimeConfig(BizUser user, Query query) throws BizException {
        BizImportTaskRuntimeConfigImpl runtimeConfig = new BizImportTaskRuntimeConfigImpl();
        runtimeConfig.setExecuteType("STANDALONE");
        return runtimeConfig;
    }
}
