package com.alibaba.ageiport.processor.core.task.importer.context;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.task.AbstractMainTaskContext;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskSpecification;
import com.alibaba.ageiport.processor.core.task.importer.slice.ImportSlice;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class ImportMainTaskContextImpl<QUERY, DATA, VIEW> extends AbstractMainTaskContext implements ImportMainTaskContext<QUERY, DATA, VIEW> {

    private Class<QUERY> queryClass;

    private Class<DATA> dataClass;

    private Class<VIEW> viewClass;

    private QUERY query;

    private ImportTaskRuntimeConfig importTaskRuntimeConfig;

    private ColumnHeaders columnHeaders;

    private DataGroup dataGroup;


    @Override
    public void load(BizImportTaskRuntimeConfig bizRuntimeConfig) {
        ImportTaskRuntimeConfigImpl taskRuntimeConfig = new ImportTaskRuntimeConfigImpl();

        ImportTaskSpecification<QUERY, DATA, VIEW> importTaskSpec = getImportTaskSpec();

        if (bizRuntimeConfig != null && bizRuntimeConfig.getPageSize() != null && bizRuntimeConfig.getPageSize() > 0) {
            taskRuntimeConfig.setPageSize(bizRuntimeConfig.getPageSize());
        } else {
            taskRuntimeConfig.setPageSize(importTaskSpec.getPageSize());
        }

        if (bizRuntimeConfig != null && StringUtils.isNotBlank(bizRuntimeConfig.getExecuteType())) {
            taskRuntimeConfig.setExecuteType(bizRuntimeConfig.getExecuteType());
        } else {
            taskRuntimeConfig.setExecuteType(importTaskSpec.getExecuteType());
        }

        if (bizRuntimeConfig != null && StringUtils.isNotBlank(bizRuntimeConfig.getTaskSliceStrategy())) {
            taskRuntimeConfig.setTaskSliceStrategy(bizRuntimeConfig.getTaskSliceStrategy());
        } else {
            taskRuntimeConfig.setTaskSliceStrategy(importTaskSpec.getTaskSliceStrategy());
        }

        if (bizRuntimeConfig != null && StringUtils.isNotBlank(bizRuntimeConfig.getFileType())) {
            taskRuntimeConfig.setFileType(bizRuntimeConfig.getFileType());
        } else {
            taskRuntimeConfig.setFileType(importTaskSpec.getFileType());
        }
        this.importTaskRuntimeConfig = taskRuntimeConfig;
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
        ((ImportTaskRuntimeConfigImpl) this.importTaskRuntimeConfig).setColumnHeaders(columnHeaderList);
    }

    @Override
    public void load(DataGroup dataGroup) {
        this.dataGroup = dataGroup;
    }

    @Override
    public void load(List<ImportSlice> importSlices) {
        MainTask mainTask = this.getMainTask();
        mainTask.setSubTotalCount(importSlices.size());
        mainTask.setSubSuccessCount(0);
        mainTask.setSubFailedCount(0);
        mainTask.setSubFinishedCount(0);
    }

    @Override
    public void save() {
        AgeiPort ageiPort = this.getAgeiPort();
        TaskServerClient client = ageiPort.getTaskServerClient();

        MainTask mainTask = this.getMainTask();
        String runtimeParam = JsonUtil.toJsonString(this.importTaskRuntimeConfig);
        mainTask.setRuntimeParam(runtimeParam);

        client.updateMainTask(mainTask);
    }

    @Override
    public ImportTaskSpecification<QUERY, DATA, VIEW> getImportTaskSpec() {
        return (ImportTaskSpecification<QUERY, DATA, VIEW>) getTaskSpec();
    }
}
