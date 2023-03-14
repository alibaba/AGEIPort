package com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka;


import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.processor.core.annotation.ExportSpecification;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.model.api.BizExportPage;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.task.exporter.ExportProcessor;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka.model.Data;
import com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka.model.Query;
import com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka.model.View;

import java.util.ArrayList;
import java.util.List;

@ExportSpecification(code = "EurekaClusterExportProcessor", name = "EurekaClusterExportProcessor", executeType = ExecuteType.CLUSTER)
public class EurekaClusterExportProcessor implements ExportProcessor<Query, Data, View> {
    @Override
    public Integer totalCount(BizUser bizUser, Query query) throws BizException {
        return query.getTotalCount();
    }

    @Override
    public List<Data> queryData(BizUser user, Query query, BizExportPage bizExportPage) throws BizException {
        List<Data> dataList = new ArrayList<>();

        Integer totalCount = query.getTotalCount();
        for (int i = 1; i <= totalCount; i++) {
            final Data data = new Data();
            data.setId(i);
            data.setName("name" + i);
            if (i % 3 == 0) {
                data.setGender("男");
            }
            if (i % 3 == 1) {
                data.setGender("女");
            }
            if (i % 3 == 2) {
                data.setGender("其他");
            }
            dataList.add(data);
        }
        return dataList;
    }

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
    public BizExportTaskRuntimeConfig taskRuntimeConfig(BizUser user, Query query) throws BizException {
        final BizExportTaskRuntimeConfigImpl runtimeConfig = new BizExportTaskRuntimeConfigImpl();
        runtimeConfig.setExecuteType("CLUSTER");
        return runtimeConfig;
    }
}
