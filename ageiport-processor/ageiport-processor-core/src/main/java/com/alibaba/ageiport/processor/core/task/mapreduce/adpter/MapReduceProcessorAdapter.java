package com.alibaba.ageiport.processor.core.task.mapreduce.adpter;

import com.alibaba.ageiport.processor.core.model.api.*;
import com.alibaba.ageiport.processor.core.spi.Adapter;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.task.exporter.ExportProcessor;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.context.ExportMainTaskContext;
import com.alibaba.ageiport.processor.core.task.exporter.context.ExportSubTaskContext;

import java.util.List;

/**
 * @author lingyi
 */
public interface MapReduceProcessorAdapter<QUERY, DATA, VIEW> extends Adapter {

    default BizExportTaskRuntimeConfig taskRuntimeConfig(BizUser user, QUERY query,
                                                         ExportProcessor<QUERY, DATA, VIEW> processor,
                                                         ExportMainTaskContext<QUERY, DATA, VIEW> context) {
        try {
            processor.setContext(context);
            BizExportTaskRuntimeConfig bizExportTaskRuntimeConfig = processor.taskRuntimeConfig(user, query);
            return bizExportTaskRuntimeConfig;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default QUERY resetQuery(BizUser bizUser, QUERY query,
                             ExportProcessor<QUERY, DATA, VIEW> processor,
                             ExportMainTaskContext<QUERY, DATA, VIEW> context) {
        try {
            processor.setContext(context);
            return processor.resetQuery(bizUser, query);
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

}
