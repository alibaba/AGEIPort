package com.alibaba.ageiport.processor.core.task.importer.context;

import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.api.impl.BizUserImpl;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.impl.ColumnHeaderImpl;
import com.alibaba.ageiport.processor.core.model.core.impl.ColumnHeadersImpl;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskContext;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskContextFactory;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskSpecHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingyi
 */
public class ImportMainTaskContextFactory<QUERY, DATA, VIEW> implements MainTaskContextFactory {
    @Override
    public MainTaskContext create(AgeiPort ageiPort, MainTask mainTask) {
        String taskCode = mainTask.getCode();

        TaskSpecificationRegistry registry = ageiPort.getSpecificationRegistry();
        ImportTaskSpecHolder<QUERY, DATA, VIEW> taskSpec = (ImportTaskSpecHolder) registry.get(taskCode);

        BizUserImpl bizUserImpl = BeanUtils.cloneProp(mainTask, BizUserImpl.class);
        ImportMainTaskContextImpl<QUERY, DATA, VIEW> context = new ImportMainTaskContextImpl<>();
        context.setMainTask(mainTask);
        context.setAgeiPort(ageiPort);
        context.setTaskSpec(taskSpec);
        context.setBizUser(bizUserImpl);
        context.setQueryClass(taskSpec.getQueryClass());
        context.setDataClass(taskSpec.getDataClass());
        context.setViewClass(taskSpec.getViewClass());

        QUERY query = JsonUtil.toObject(mainTask.getBizQuery(), taskSpec.getQueryClass());
        context.setQuery(query);

        String runtimeParamString = mainTask.getRuntimeParam();
        if (JsonUtil.isJson(runtimeParamString)) {
            ImportTaskRuntimeConfigImpl runtimeConfig = JsonUtil.toObject(runtimeParamString, ImportTaskRuntimeConfigImpl.class);
            context.setImportTaskRuntimeConfig(runtimeConfig);
            List<ColumnHeader> columnHeaders = runtimeConfig.getColumnHeaders();
            List<ColumnHeaderImpl> headers = JsonUtil.toArrayObject(JsonUtil.toJsonString(runtimeConfig.getColumnHeaders()), ColumnHeaderImpl.class);

            List<ColumnHeader> columnHeaderList = new ArrayList<>();
            for (ColumnHeaderImpl columnHeader : headers) {
                columnHeaderList.add(columnHeader);
            }
            runtimeConfig.setColumnHeaders(columnHeaderList);

            ColumnHeadersImpl columnHeadersImpl = new ColumnHeadersImpl(columnHeaderList);
            context.setColumnHeaders(columnHeadersImpl);
        }
        return context;
    }
}
