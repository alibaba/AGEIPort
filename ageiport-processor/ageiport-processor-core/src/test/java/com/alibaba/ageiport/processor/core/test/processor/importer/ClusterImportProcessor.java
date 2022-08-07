package com.alibaba.ageiport.processor.core.test.processor.importer;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.annotation.ImportSpecification;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResult;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResultImpl;
import com.alibaba.ageiport.processor.core.test.model.Data;
import com.alibaba.ageiport.processor.core.test.model.Query;
import com.alibaba.ageiport.processor.core.test.model.View;

import java.util.ArrayList;
import java.util.List;


@ImportSpecification(code = "ClusterImportProcessor", name = "StandaloneImportProcessor", executeType = ExecuteType.CLUSTER)
public class ClusterImportProcessor implements ImportProcessor<Query, Data, View> {

    Logger logger = LoggerFactory.getLogger(ClusterImportProcessor.class);

    @Override
    public BizImportResult<View, Data> convertAndCheck(BizUser user, Query query, List<View> views) {
        BizImportResultImpl<View, Data> result = new BizImportResultImpl<>();

        List<Data> data = new ArrayList<>();
        for (View view : views) {
            Data datum = new Data();
            datum.setId(view.getId());
            datum.setName(view.getName());
            data.add(datum);
        }

        result.setData(data);
        result.setView(query.getCheckErrorData());
        return result;
    }

    @Override
    public BizImportResult<View, Data> write(BizUser user, Query query, List<Data> data) {
        BizImportResultImpl<View, Data> result = new BizImportResultImpl<>();
        logger.info(JsonUtil.toJsonString(data));
        result.setView(query.getWriteErrorData());
        return result;
    }
}
