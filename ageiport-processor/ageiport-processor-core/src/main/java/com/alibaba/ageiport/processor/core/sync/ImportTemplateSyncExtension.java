package com.alibaba.ageiport.processor.core.sync;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.api.BizColumnHeaders;
import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeaders;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.api.impl.BizUserImpl;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.ageiport.processor.core.spi.file.FileWriter;
import com.alibaba.ageiport.processor.core.spi.file.FileWriterFactory;
import com.alibaba.ageiport.processor.core.spi.service.SyncExtensionApiParam;
import com.alibaba.ageiport.processor.core.spi.service.SyncExtensionApiResult;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.sync.SyncExtension;
import com.alibaba.ageiport.processor.core.spi.task.factory.TaskContext;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskSpecification;
import com.alibaba.ageiport.processor.core.utils.HeadersUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

public class ImportTemplateSyncExtension<QUERY, DATA, VIEW> implements SyncExtension {

    private static Logger log = LoggerFactory.getLogger(TaskContext.class);

    @Override
    public SyncExtensionApiResult execute(AgeiPort ageiPort, SyncExtensionApiParam param) {

        FileWriter fileWriter = null;
        InputStream fileStream = null;
        try {
            String extensionApiParam = param.getSyncExtensionApiParam();
            TaskExecuteParam taskExecuteParam = JsonUtil.toObject(extensionApiParam, TaskExecuteParam.class);

            String code = taskExecuteParam.getTaskSpecificationCode();
            TaskSpecificationRegistry registry = ageiPort.getSpecificationRegistry();
            ImportTaskSpecification<QUERY, DATA, VIEW> importTaskSpec = (ImportTaskSpecification<QUERY, DATA, VIEW>) registry.get(code);
            ImportProcessor<QUERY, DATA, VIEW> processor = (ImportProcessor<QUERY, DATA, VIEW>) importTaskSpec.getProcessor();

            BizUserImpl bizUser = BeanUtils.cloneProp(taskExecuteParam, BizUserImpl.class);
            QUERY query = JsonUtil.toObject(taskExecuteParam.getBizQuery(), importTaskSpec.getQueryClass());

            ImportTaskRuntimeConfigImpl taskRuntimeConfig = getTaskRuntimeConfig(importTaskSpec, processor, bizUser, query);

            QUERY resetQuery = processor.resetQuery(bizUser, query);

            BizColumnHeaders bizColumnHeaders = processor.getHeaders(bizUser, resetQuery);
            BizDynamicColumnHeaders bizDynamicColumnHeaders = processor.getDynamicHeaders(bizUser, query);
            ColumnHeaders columnHeaders = HeadersUtil.buildHeaders(bizColumnHeaders, importTaskSpec.getViewClass(), bizDynamicColumnHeaders);

            String fileWriterFactoryName = ageiPort.getOptions().getFileTypeWriterSpiMappings().get(taskRuntimeConfig.getFileType());
            FileWriterFactory fileWriterFactory = ExtensionLoader.getExtensionLoader(FileWriterFactory.class).getExtension(fileWriterFactoryName);

            FileContext fileContext = new FileContext();
            fileContext.setBizQuery(JsonUtil.toJsonString(resetQuery));
            fileContext.setTaskSpec(importTaskSpec);

            fileWriter = fileWriterFactory.create(ageiPort, columnHeaders, fileContext);
            fileStream = fileWriter.finish();
            FileStore fileStore = ageiPort.getFileStore();
            String key = UUID.randomUUID() + "." + taskRuntimeConfig.getFileType();
            fileStore.save(key, fileStream, new HashMap<>());

            Result result = new Result();
            result.setOutputFileKey(key);

            SyncExtensionApiResult apiResult = new SyncExtensionApiResult();
            apiResult.setSuccess(true);
            apiResult.setSyncExtensionApiResult(JsonUtil.toJsonString(result));
            return apiResult;
        } catch (Throwable e) {
            log.error("ImportTemplateSyncExtension#execute failed, code:{}, param:{}",
                    param.getSyncExtensionApiCode(), param.getSyncExtensionApiParam(), e);
            SyncExtensionApiResult apiResult = new SyncExtensionApiResult();
            apiResult.setSuccess(true);
            apiResult.setErrorMessage(e.getMessage());
            return apiResult;
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @NotNull
    private static <QUERY, DATA, VIEW> ImportTaskRuntimeConfigImpl getTaskRuntimeConfig(ImportTaskSpecification<QUERY, DATA, VIEW> importTaskSpec, ImportProcessor<QUERY, DATA, VIEW> processor, BizUser bizUser, QUERY query) {
        ImportTaskRuntimeConfigImpl taskRuntimeConfig = new ImportTaskRuntimeConfigImpl();
        BizImportTaskRuntimeConfig bizRuntimeConfig = processor.taskRuntimeConfig(bizUser, query);

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
        return taskRuntimeConfig;
    }

    @Data
    public static class Result {
        private String outputFileKey;
    }
}
