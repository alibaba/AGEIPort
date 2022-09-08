package com.alibaba.ageiport.processor.core.task.exporter.context;

import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.api.impl.BizUserImpl;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.impl.ColumnHeadersImpl;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskContext;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskContextFactory;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskSpecHolder;

import java.util.List;

/**
 * @author lingyi
 */
public class ExportSubTaskContextFactory<QUERY, DATA, VIEW> implements SubTaskContextFactory {

    @Override
    public SubTaskContext create(AgeiPort ageiPort, String subTaskId) {
        ExportSubTaskContextImpl<QUERY, DATA, VIEW> context = new ExportSubTaskContextImpl<>();

        TaskServerClient client = ageiPort.getTaskServerClient();
        SubTask subTask = client.getSubTask(subTaskId);
        context.setAgeiPort(ageiPort);
        context.setSubTask(subTask);
        MainTask mainTask = client.getMainTask(subTask.getMainTaskId());
        context.setMainTask(mainTask);

        TaskSpecificationRegistry registry = ageiPort.getSpecificationRegistry();
        ExportTaskSpecHolder<QUERY, DATA, VIEW> taskSpec = (ExportTaskSpecHolder) registry.get(subTask.getCode());

        BizUserImpl bizUserImpl = BeanUtils.cloneProp(subTask, BizUserImpl.class);
        context.setSubTask(subTask);
        context.setAgeiPort(ageiPort);
        context.setTaskSpec(taskSpec);
        context.setBizUser(bizUserImpl);
        context.setQueryClass(taskSpec.getQueryClass());
        context.setDataClass(taskSpec.getDataClass());
        context.setViewClass(taskSpec.getViewClass());

        QUERY query = JsonUtil.toObject(subTask.getBizQuery(), taskSpec.getQueryClass());
        context.setQuery(query);

        String runtimeParamString = subTask.getRuntimeParam();
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
