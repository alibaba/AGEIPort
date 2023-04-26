package com.alibaba.ageiport.test.processor.core.importer;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.CollectionUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.annotation.ImportSpecification;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResult;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResultImpl;
import com.alibaba.ageiport.test.processor.core.model.Data;
import com.alibaba.ageiport.test.processor.core.model.Query;
import com.alibaba.ageiport.test.processor.core.model.View;

import java.util.ArrayList;
import java.util.List;


//1.实现ImportProcessor接口
@ImportSpecification(code = "CSVImportProcessor", name = "CSVImportProcessor", fileType = "csv")
public class CSVImportProcessor implements ImportProcessor<Query, Data, View> {

    Logger logger = LoggerFactory.getLogger(CSVImportProcessor.class);

    //2.实现ImportProcessor接口的convertAndCheck方法
    @Override
    public BizImportResult<View, Data> convertAndCheck(BizUser user, Query query, List<View> views) {
        BizImportResultImpl<View, Data> result = new BizImportResultImpl<>();

        List<Data> data = new ArrayList<>();
        for (View view : views) {
            Data datum = new Data();
            datum.setId(view.getId());
            datum.setName(view.getName());
            datum.setGender(view.getGender());
            if (CollectionUtils.isNotEmpty(query.getCheckErrorDataWhenIdIn())) {
                if (query.getCheckErrorDataWhenIdIn().contains(view.getId().toString())) {
                    result.setView(query.getCheckErrorData());
                }
            }

            data.add(datum);
        }

        result.setData(data);

        return result;
    }

    //3.实现ExportProcessor接口的write方法，此方法负责执行写入业务逻辑。
    @Override
    public BizImportResult<View, Data> write(BizUser user, Query query, List<Data> data) {
        BizImportResultImpl<View, Data> result = new BizImportResultImpl<>();
        logger.info(JsonUtil.toJsonString(data));
        result.setView(query.getWriteErrorData());
        return result;
    }

    @Override
    public BizImportTaskRuntimeConfig taskRuntimeConfig(BizUser user, Query query) throws BizException {
        BizImportTaskRuntimeConfigImpl runtimeConfig = new BizImportTaskRuntimeConfigImpl();
        runtimeConfig.setExecuteType("STANDALONE");
        return runtimeConfig;
    }
}
