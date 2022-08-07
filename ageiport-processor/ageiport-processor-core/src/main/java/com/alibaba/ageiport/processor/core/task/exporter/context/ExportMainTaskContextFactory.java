package com.alibaba.ageiport.processor.core.task.exporter.context;

import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.api.impl.BizUserImpl;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.impl.ColumnHeadersImpl;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskContext;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskContextFactory;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskSpecHolder;

import java.util.List;

/**
 * @author lingyi
 */
public class ExportMainTaskContextFactory<QUERY, DATA, VIEW> implements MainTaskContextFactory {
    @Override
    public MainTaskContext create(AgeiPort ageiPort, MainTask mainTask) {
        String taskCode = mainTask.getCode();

        TaskSpecificationRegistry registry = ageiPort.getSpecificationRegistry();
        ExportTaskSpecHolder<QUERY, DATA, VIEW> taskSpec = (ExportTaskSpecHolder) registry.get(taskCode);

        BizUserImpl bizUserImpl = BeanUtils.cloneProp(mainTask, BizUserImpl.class);
        ExportMainTaskContextImpl<QUERY, DATA, VIEW> context = new ExportMainTaskContextImpl<>();
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
            ExportTaskRuntimeConfigImpl runtimeConfig = JsonUtil.toObject(runtimeParamString, ExportTaskRuntimeConfigImpl.class);
            context.setExportTaskRuntimeConfig(runtimeConfig);
            List<ColumnHeader> columnHeaderList = runtimeConfig.getColumnHeaders();
            ColumnHeadersImpl columnHeaders = new ColumnHeadersImpl(columnHeaderList);
            context.setColumnHeaders(columnHeaders);
        }
        return context;
    }
}
