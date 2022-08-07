package com.alibaba.ageiport.processor.core.task.importer.context;

import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.task.importer.slice.ImportSlice;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfig;

import java.util.List;

/**
 * @author lingyi
 */
public interface ImportMainTaskContext<QUERY, DATA, VIEW> extends ImportTaskContext<QUERY, DATA, VIEW> {

    MainTask getMainTask();

    void setMainTask(MainTask mainTask);

    void load(DataGroup dataGroup);

    void load(List<ImportSlice> importSlices);

    ColumnHeaders getColumnHeaders();

    void load(BizImportTaskRuntimeConfig runtimeConfig);

    void load(QUERY query);

    void load(ColumnHeaders columnHeaders);
}
