package com.alibaba.ageiport.processor.core.task.exporter.adapter;

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
public interface ExportProcessorAdapter<QUERY, DATA, VIEW> extends Adapter {

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

    default BizColumnHeaders getHeaders(BizUser bizUser, QUERY query,
                                        ExportProcessor<QUERY, DATA, VIEW> processor,
                                        ExportMainTaskContext<QUERY, DATA, VIEW> context) {
        try {
            processor.setContext(context);
            return processor.getHeaders(bizUser, query);
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default BizDynamicColumnHeaders getDynamicHeaders(BizUser bizUser, QUERY query,
                                                      ExportProcessor<QUERY, DATA, VIEW> processor,
                                                      ExportMainTaskContext<QUERY, DATA, VIEW> context) {
        try {
            processor.setContext(context);
            return processor.getDynamicHeaders(bizUser, query);
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default Integer totalCount(BizUser bizUser, QUERY query,
                               ExportProcessor<QUERY, DATA, VIEW> processor,
                               ExportMainTaskContext<QUERY, DATA, VIEW> context) {
        try {
            processor.setContext(context);
            return processor.totalCount(bizUser, query);
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default List<DATA> queryData(BizUser bizUser, QUERY query, BizExportPage bizExportPage,
                                 ExportProcessor<QUERY, DATA, VIEW> processor,
                                 ExportSubTaskContext<QUERY, DATA, VIEW> context) {
        try {
            processor.setContext(context);
            return processor.queryData(bizUser, query, bizExportPage);
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default List<VIEW> convert(BizUser bizUser, QUERY query, List<DATA> data,
                               ExportProcessor<QUERY, DATA, VIEW> processor,
                               ExportSubTaskContext<QUERY, DATA, VIEW> context) {

        try {
            processor.setContext(context);
            return processor.convert(bizUser, query, data);
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default BizDataGroup<VIEW> group(BizUser bizUser, QUERY query, List<VIEW> views,
                                     ExportProcessor<QUERY, DATA, VIEW> processor,
                                     ExportSubTaskContext<QUERY, DATA, VIEW> context) {

        try {
            processor.setContext(context);
            return processor.group(bizUser, query, views);
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default DataGroup getDataGroup(BizUser bizUser, QUERY query, BizDataGroup<VIEW> group,
                                   ExportProcessor<QUERY, DATA, VIEW> processor,
                                   ExportSubTaskContext<QUERY, DATA, VIEW> context) {

        try {
            processor.setContext(context);
            return processor.getDataGroup(bizUser, query, group);
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

}
