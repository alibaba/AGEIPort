package com.alibaba.ageiport.processor.core.task.exporter.context;

import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;

/**
 * @author lingyi
 */
public interface ExportSubTaskContext<QUERY, DATA, VIEW> extends ExportTaskContext<QUERY, DATA, VIEW> {

    MainTask getMainTask();

    SubTask getSubTask();
}
