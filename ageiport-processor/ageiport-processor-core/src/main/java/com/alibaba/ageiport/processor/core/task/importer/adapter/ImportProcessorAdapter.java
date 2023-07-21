package com.alibaba.ageiport.processor.core.task.importer.adapter;

import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.model.api.BizColumnHeaders;
import com.alibaba.ageiport.processor.core.model.api.BizDataGroup;
import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeaders;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.spi.Adapter;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResult;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.context.ImportMainTaskContext;
import com.alibaba.ageiport.processor.core.task.importer.context.ImportSubTaskContext;

import java.util.List;

/**
 * @author lingyi
 */
public interface ImportProcessorAdapter<QUERY, DATA, VIEW> extends Adapter {

    default BizImportTaskRuntimeConfig taskRuntimeConfig(BizUser bizUser, QUERY query,
                                                         ImportProcessor<QUERY, DATA, VIEW> processor,
                                                         ImportMainTaskContext<QUERY, DATA, VIEW> context) throws BizException {
        try {
            processor.setContext(context);
            BizImportTaskRuntimeConfig bizImportTaskRuntimeConfig = processor.taskRuntimeConfig(bizUser, query);
            return bizImportTaskRuntimeConfig;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default QUERY resetQuery(BizUser bizUser, QUERY query,
                             ImportProcessor<QUERY, DATA, VIEW> processor,
                             ImportMainTaskContext<QUERY, DATA, VIEW> context) throws BizException {
        try {
            processor.setContext(context);
            QUERY newQuery = processor.resetQuery(bizUser, query);
            return newQuery;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default BizColumnHeaders getHeaders(BizUser bizUser, QUERY query,
                                        ImportProcessor<QUERY, DATA, VIEW> processor,
                                        ImportMainTaskContext<QUERY, DATA, VIEW> context) throws BizException {
        try {
            processor.setContext(context);
            BizColumnHeaders bizColumnHeaders = processor.getHeaders(bizUser, query);
            return bizColumnHeaders;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default BizDynamicColumnHeaders getDynamicHeaders(BizUser bizUser, QUERY query,
                                                      ImportProcessor<QUERY, DATA, VIEW> processor,
                                                      ImportMainTaskContext<QUERY, DATA, VIEW> context) throws BizException {
        try {
            processor.setContext(context);
            BizDynamicColumnHeaders dynamicColumnHeaders = processor.getDynamicHeaders(bizUser, query);
            return dynamicColumnHeaders;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default DataGroup checkHeaders(BizUser bizUser, QUERY query, DataGroup group,
                                   ImportProcessor<QUERY, DATA, VIEW> processor,
                                   ImportSubTaskContext<QUERY, DATA, VIEW> context) throws BizException {
        try {
            processor.setContext(context);
            DataGroup dataGroup = processor.checkHeaders(bizUser, query, group);
            return dataGroup;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }


    default BizDataGroup<VIEW> getBizDataGroup(BizUser bizUser, QUERY query, DataGroup group,
                                               ImportProcessor<QUERY, DATA, VIEW> processor,
                                               ImportSubTaskContext<QUERY, DATA, VIEW> context) throws BizException {
        try {
            processor.setContext(context);
            BizDataGroup<VIEW> bizDataGroup = processor.getBizDataGroup(bizUser, query, group);
            return bizDataGroup;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default List<VIEW> flat(BizUser bizUser, QUERY query, BizDataGroup<VIEW> group,
                            ImportProcessor<QUERY, DATA, VIEW> processor,
                            ImportSubTaskContext<QUERY, DATA, VIEW> context) throws BizException {
        try {
            processor.setContext(context);
            List<VIEW> bizDataGroup = processor.flat(bizUser, query, group);
            return bizDataGroup;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default BizImportResult<VIEW, DATA> convertAndCheck(BizUser bizUser, QUERY query, List<VIEW> views,
                                                        ImportProcessor<QUERY, DATA, VIEW> processor,
                                                        ImportSubTaskContext<QUERY, DATA, VIEW> context) throws BizException {
        try {
            processor.setContext(context);
            BizImportResult<VIEW, DATA> importResult = processor.convertAndCheck(bizUser, query, views);
            return importResult;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default BizImportResult<VIEW, DATA> write(BizUser bizUser, QUERY query, List<DATA> data,
                                              ImportProcessor<QUERY, DATA, VIEW> processor,
                                              ImportSubTaskContext<QUERY, DATA, VIEW> context) throws BizException {
        try {
            processor.setContext(context);
            BizImportResult<VIEW, DATA> importResult = processor.write(bizUser, query, data);
            return importResult;
        } catch (Throwable e) {
            throw e;
        } finally {
            processor.clearContext();
        }
    }

    default BizDataGroup<VIEW> group(BizUser bizUser, QUERY query, List<VIEW> views,
                                     ImportProcessor<QUERY, DATA, VIEW> processor,
                                     ImportSubTaskContext<QUERY, DATA, VIEW> context) throws BizException {
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
                                   ImportProcessor<QUERY, DATA, VIEW> processor,
                                   ImportSubTaskContext<QUERY, DATA, VIEW> context) throws BizException {
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
