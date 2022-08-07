package com.alibaba.ageiport.processor.core.task.importer.context;

import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;

/**
 * @author lingyi
 */
public interface ImportSubTaskContext<QUERY, DATA, VIEW> extends ImportTaskContext<QUERY, DATA, VIEW> {

    MainTask getMainTask();

    SubTask getSubTask();
}
