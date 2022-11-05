package com.alibaba.ageiport.processor.core.task.exporter.context;

import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.slice.ExportSlice;

import java.util.List;

/**
 * @author lingyi
 */
public interface ExportMainTaskContext<QUERY, DATA, VIEW> extends ExportTaskContext<QUERY, DATA, VIEW> {

    @Override
    MainTask getMainTask();

    @Override
    void setMainTask(MainTask mainTask);

    void load(Integer totalCount);

    void load(List<ExportSlice> exportSlices);

    ColumnHeaders getColumnHeaders();

    void load(BizExportTaskRuntimeConfig runtimeConfig);

    void load(QUERY query);

    void load(ColumnHeaders columnHeaders);
}
