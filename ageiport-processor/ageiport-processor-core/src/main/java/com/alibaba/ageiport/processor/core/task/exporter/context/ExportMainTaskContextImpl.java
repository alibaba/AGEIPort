package com.alibaba.ageiport.processor.core.task.exporter.context;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.task.exporter.slice.ExportSlice;
import com.alibaba.ageiport.processor.core.task.AbstractMainTaskContext;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskSpecification;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class ExportMainTaskContextImpl<QUERY, DATA, VIEW> extends AbstractMainTaskContext implements ExportMainTaskContext<QUERY, DATA, VIEW> {

    private Class<QUERY> queryClass;

    private Class<DATA> dataClass;

    private Class<VIEW> viewClass;

    private QUERY query;

    private ExportTaskRuntimeConfig exportTaskRuntimeConfig;

    private ColumnHeaders columnHeaders;


    @Override
    public void load(BizExportTaskRuntimeConfig bizRuntimeConfig) {
        ExportTaskRuntimeConfigImpl taskRuntimeConfig = new ExportTaskRuntimeConfigImpl();

        ExportTaskSpecification<QUERY, DATA, VIEW> exportTaskSpec = getExportTaskSpec();

        if (bizRuntimeConfig != null && bizRuntimeConfig.getPageSize() != null && bizRuntimeConfig.getPageSize() > 0) {
            taskRuntimeConfig.setPageSize(bizRuntimeConfig.getPageSize());
        } else {
            taskRuntimeConfig.setPageSize(exportTaskSpec.getPageSize());
        }

        if (bizRuntimeConfig != null && StringUtils.isNotBlank(bizRuntimeConfig.getExecuteType())) {
            taskRuntimeConfig.setExecuteType(bizRuntimeConfig.getExecuteType());
        } else {
            taskRuntimeConfig.setExecuteType(exportTaskSpec.getExecuteType());
        }

        if (bizRuntimeConfig != null && StringUtils.isNotBlank(bizRuntimeConfig.getTaskSliceStrategy())) {
            taskRuntimeConfig.setTaskSliceStrategy(bizRuntimeConfig.getTaskSliceStrategy());
        } else {
            taskRuntimeConfig.setTaskSliceStrategy(exportTaskSpec.getTaskSliceStrategy());
        }

        if (bizRuntimeConfig != null && StringUtils.isNotBlank(bizRuntimeConfig.getFileType())) {
            taskRuntimeConfig.setFileType(bizRuntimeConfig.getFileType());
        } else {
            taskRuntimeConfig.setFileType(exportTaskSpec.getFileType());
        }
        this.exportTaskRuntimeConfig = taskRuntimeConfig;
    }

    @Override
    public ExportTaskRuntimeConfig getExportTaskRuntimeConfig() {
        return exportTaskRuntimeConfig;
    }

    @Override
    public void load(QUERY query) {
        if (query == null) {
            return;
        }
        this.query = query;
        MainTask mainTask = this.getMainTask();
        String bizQuery = JsonUtil.toJsonString(query);
        mainTask.setBizQuery(bizQuery);
    }

    @Override
    public void load(ColumnHeaders columnHeaders) {
        this.columnHeaders = columnHeaders;
        List<ColumnHeader> columnHeaderList = columnHeaders.getColumnHeaders();
        ((ExportTaskRuntimeConfigImpl) this.exportTaskRuntimeConfig).setColumnHeaders(columnHeaderList);
    }

    @Override
    public void load(Integer totalCount) {
        ((ExportTaskRuntimeConfigImpl) exportTaskRuntimeConfig).setTotalCount(totalCount);
    }

    @Override
    public void load(List<ExportSlice> exportSlices) {
        MainTask mainTask = this.getMainTask();
        mainTask.setSubTotalCount(exportSlices.size());
        mainTask.setSubSuccessCount(0);
        mainTask.setSubFailedCount(0);
        mainTask.setSubFinishedCount(0);
    }

    @Override
    public void save() {
        AgeiPort ageiPort = this.getAgeiPort();
        TaskServerClient client = ageiPort.getTaskServerClient();

        MainTask mainTask = this.getMainTask();
        String runtimeParam = JsonUtil.toJsonString(this.exportTaskRuntimeConfig);
        mainTask.setRuntimeParam(runtimeParam);

        client.updateMainTask(mainTask);
    }

    @Override
    public ExportTaskSpecification<QUERY, DATA, VIEW> getExportTaskSpec() {
        return (ExportTaskSpecification<QUERY, DATA, VIEW>) getTaskSpec();
    }
}
