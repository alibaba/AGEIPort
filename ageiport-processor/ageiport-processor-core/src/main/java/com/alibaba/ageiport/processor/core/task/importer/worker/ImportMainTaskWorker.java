package com.alibaba.ageiport.processor.core.task.importer.worker;

import com.alibaba.ageiport.common.feature.FeatureUtils;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.CollectionUtils;
import com.alibaba.ageiport.common.utils.IOUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.TaskIdUtil;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.ConstValues;
import com.alibaba.ageiport.processor.core.constants.MainTaskFeatureKeys;
import com.alibaba.ageiport.processor.core.constants.TaskStatus;
import com.alibaba.ageiport.processor.core.model.api.BizColumnHeaders;
import com.alibaba.ageiport.processor.core.model.api.BizDynamicColumnHeaders;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeaders;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.cache.BigDataCache;
import com.alibaba.ageiport.processor.core.spi.client.CreateSubTasksRequest;
import com.alibaba.ageiport.processor.core.spi.convertor.Model;
import com.alibaba.ageiport.processor.core.spi.file.*;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskContextFactory;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.slice.SliceStrategy;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;
import com.alibaba.ageiport.processor.core.task.AbstractMainTaskWorker;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;
import com.alibaba.ageiport.processor.core.task.importer.adapter.ImportProcessorAdapter;
import com.alibaba.ageiport.processor.core.task.importer.api.BizImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.context.ImportMainTaskContext;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskSpecification;
import com.alibaba.ageiport.processor.core.task.importer.slice.ImportSlice;
import com.alibaba.ageiport.processor.core.task.importer.slice.ImportSliceStrategy;
import com.alibaba.ageiport.processor.core.utils.HeadersUtil;

import java.io.InputStream;
import java.util.*;

/**
 * @author lingyi
 */
public class ImportMainTaskWorker<QUERY, DATA, VIEW> extends AbstractMainTaskWorker {

    public static Logger log = LoggerFactory.getLogger(ImportMainTaskWorker.class);

    public ImportMainTaskWorker(AgeiPort ageiPort, MainTask mainTask) {
        this.ageiPort = ageiPort;
        this.mainTask = mainTask;
    }


    @Override
    public void doPrepare() {
        AgeiPort ageiPort = getAgeiPort();
        String mainTaskId = mainTask.getMainTaskId();
        String executeType = mainTask.getExecuteType();
        String taskType = mainTask.getType();
        String taskCode = mainTask.getCode();
        FileReader fileReader = null;

        try {
            TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();

            MainTaskStageProvider stageProvider = spiSelector.selectExtension(executeType, taskType, taskCode, MainTaskStageProvider.class);
            MainTaskContextFactory contextFactory = spiSelector.selectExtension(executeType, taskType, taskCode, MainTaskContextFactory.class);
            ImportMainTaskContext<QUERY, DATA, VIEW> context = (ImportMainTaskContext) contextFactory.create(ageiPort, mainTask);
            context.getMainTask().setStatus(TaskStatus.EXECUTING);
            context.getMainTask().setGmtStart(new Date());
            context.save();
            context.setStage(stageProvider.mainTaskStart());

            ImportTaskSpecification<QUERY, DATA, VIEW> taskSpec = context.getImportTaskSpec();
            ImportProcessor<QUERY, DATA, VIEW> processor = taskSpec.getImportProcessor();
            ImportProcessorAdapter<QUERY, DATA, VIEW> adapter = (ImportProcessorAdapter) processor.getConcreteAdapter();

            BizUser bizUser = context.getBizUser();
            QUERY query = context.getQuery();


            context.goNextStageEventNew();
            BizImportTaskRuntimeConfig runtimeConfig = adapter.taskRuntimeConfig(bizUser, query, processor, context);
            context.load(runtimeConfig);
            context.save();

            context.goNextStageEventNew();
            ImportTaskRuntimeConfig importTaskRuntimeConfig = context.getImportTaskRuntimeConfig();
            String fileType = importTaskRuntimeConfig.getFileType();

            context.goNextStageEventNew();
            QUERY resetQuery = adapter.resetQuery(bizUser, query, processor, context);
            context.load(resetQuery);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            BizColumnHeaders bizColumnHeaders = adapter.getHeaders(bizUser, query, processor, context);
            context.goNextStageEventNew();
            context.goNextStageEventNew();
            BizDynamicColumnHeaders bizDynamicColumnHeaders = adapter.getDynamicHeaders(bizUser, query, processor, context);
            context.goNextStageEventNew();
            ColumnHeaders columnHeaders = HeadersUtil.buildHeaders(bizColumnHeaders, taskSpec.getViewClass(), bizDynamicColumnHeaders);
            for (ColumnHeader columnHeader : columnHeaders.getColumnHeaders()) {
                if (columnHeader.getIgnoreHeader() == null) {
                    columnHeader.setIgnoreHeader(false);
                }
            }
            context.load(columnHeaders);

            context.goNextStageEventNew();
            String inputFileKey = FeatureUtils.getFeature(mainTask.getFeature(), MainTaskFeatureKeys.INPUT_FILE_KEY);
            InputStream inputStream = ageiPort.getFileStore().get(inputFileKey, new HashMap<>());
            String fileReaderFactoryName = ageiPort.getOptions().getFileTypeReaderSpiMappings().get(fileType);
            FileReaderFactory fileReaderFactory = ExtensionLoader.getExtensionLoader(FileReaderFactory.class).getExtension(fileReaderFactoryName);

            FileContext fileContext = new FileContext();
            fileContext.setBizQuery(JsonUtil.toJsonString(mainTask.getBizQuery()));
            fileContext.setTaskSpec(context.getImportTaskSpec());
            fileContext.setMainTask(mainTask);
            fileReader = fileReaderFactory.create(ageiPort, columnHeaders, fileContext);
            fileReader.read(inputStream);
            DataGroup dataGroup = fileReader.finish();
            context.load(dataGroup);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            String sliceStrategyName = importTaskRuntimeConfig.getTaskSliceStrategy();
            ImportSliceStrategy<QUERY, DATA, VIEW> sliceStrategy = (ImportSliceStrategy) ExtensionLoader.getExtensionLoader(SliceStrategy.class).getExtension(sliceStrategyName);
            List<ImportSlice> slices = sliceStrategy.slice(context);
            context.load(slices);
            context.goNextStageEventNew();

            context.save();

            context.goNextStageEventNew();
            BigDataCache cache = ageiPort.getBigDataCacheManager().getBigDataCacheCache(mainTask.getExecuteType());
            List<CreateSubTasksRequest.SubTaskInstance> subTaskInstances = new ArrayList<>();
            for (ImportSlice slice : slices) {
                CreateSubTasksRequest.SubTaskInstance subTaskInstance = new CreateSubTasksRequest.SubTaskInstance();
                subTaskInstance.setSubTaskNo(slice.getNo());
                subTaskInstance.setBizQuery(slice.getQueryJson());
                Map<String, Object> mainRuntimeParam = Model.toMap(importTaskRuntimeConfig);
                ImportTaskRuntimeConfigImpl sliceRuntimeConfig = Model.toModel(mainRuntimeParam, new ImportTaskRuntimeConfigImpl());
                sliceRuntimeConfig.setNo(slice.getNo());
                sliceRuntimeConfig.setPageSize(slice.getPageSize());
                String runtimeParam = FeatureUtils.merge(JsonUtil.toJsonString(mainRuntimeParam), JsonUtil.toJsonString(sliceRuntimeConfig));
                subTaskInstance.setRuntimeParam(runtimeParam);
                subTaskInstances.add(subTaskInstance);

                String subTaskId = TaskIdUtil.genSubTaskId(mainTaskId, slice.getNo());
                String key = subTaskId + ConstValues.CACHE_SUFFIX_INPUT_DATA_SLICE;
                cache.put(key, slice.getDataGroup());
            }
            CreateSubTasksRequest createSubTasksRequest = new CreateSubTasksRequest();
            createSubTasksRequest.setMainTaskId(mainTaskId);
            createSubTasksRequest.setSubTaskInstances(subTaskInstances);
            ageiPort.getTaskServerClient().createSubTask(createSubTasksRequest);
            context.goNextStageEventNew();

            context.assertCurrentStage(stageProvider.mainTaskSaveSliceEnd());

            SubTaskStageProvider subTaskStageProvider = spiSelector.selectExtension(executeType, taskType, taskCode, SubTaskStageProvider.class);
            Stage subTaskCreated = subTaskStageProvider.subTaskCreated();
            for (ImportSlice slice : slices) {
                String subTaskId = TaskIdUtil.genSubTaskId(mainTaskId, slice.getNo());
                ageiPort.getEventBusManager().getEventBus(executeType).post(TaskStageEvent.subTaskEvent(subTaskId, subTaskCreated));
            }

        } catch (Throwable e) {
            log.error("doPrepare failed, main:{}", mainTaskId, e);
            ageiPort.onError(mainTask, e);
        } finally {
            IOUtils.closeQuietly(fileReader);
        }

    }

    @Override
    public void doReduce() {
        FileWriter fileWriter = null;
        InputStream fileStream = null;
        MainTask mainTask = getMainTask();
        try {
            String executeType = mainTask.getExecuteType();
            String taskType = mainTask.getType();
            String taskCode = mainTask.getCode();

            TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
            MainTaskContextFactory contextFactory = spiSelector.selectExtension(executeType, taskType, taskCode, MainTaskContextFactory.class);
            MainTaskStageProvider stageProvider = spiSelector.selectExtension(executeType, taskType, taskCode, MainTaskStageProvider.class);

            ImportMainTaskContext<QUERY, DATA, VIEW> context = (ImportMainTaskContext) contextFactory.create(ageiPort, mainTask);
            context.setStage(stageProvider.mainTaskReduceStart());
            context.eventCurrentStage();

            ImportTaskRuntimeConfig runtimeConfig = context.getImportTaskRuntimeConfig();

            List<String> subTaskExistView = new ArrayList<>();
            BigDataCache cache = ageiPort.getBigDataCacheManager().getBigDataCacheCache(mainTask.getExecuteType());
            for (int i = 1; i <= mainTask.getSubTotalCount(); i++) {
                String subTaskId = TaskIdUtil.genSubTaskId(mainTask.getMainTaskId(), i);
                if (cache.exist(subTaskId)) {
                    subTaskExistView.add(subTaskId);
                }
            }
            if (CollectionUtils.isNotEmpty(subTaskExistView)) {
                String fileWriterFactoryName = ageiPort.getOptions().getFileTypeWriterSpiMappings().get(runtimeConfig.getFileType());
                FileWriterFactory fileWriterFactory = ExtensionLoader.getExtensionLoader(FileWriterFactory.class).getExtension(fileWriterFactoryName);
                ColumnHeaders columnHeaders = context.getColumnHeaders();
                FileContext fileContext = new FileContext();
                fileContext.setBizQuery(JsonUtil.toJsonString(mainTask.getBizQuery()));
                fileContext.setTaskSpec(context.getImportTaskSpec());
                fileContext.setMainTask(mainTask);
                fileWriter = fileWriterFactory.create(ageiPort, columnHeaders, fileContext);

                for (String subTaskId : subTaskExistView) {
                    DataGroup dataGroup = cache.remove(subTaskId, DataGroup.class);
                    fileWriter.write(dataGroup);
                }

                fileStream = fileWriter.finish();
            }
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            String key = mainTask.getMainTaskId() + "." + runtimeConfig.getFileType();
            if (CollectionUtils.isNotEmpty(subTaskExistView)) {
                FileStore fileStore = ageiPort.getFileStore();
                fileStore.save(key, fileStream, new HashMap<>());
            }
            MainTask contextMainTask = context.getMainTask();
            String feature = FeatureUtils.putFeature(contextMainTask.getFeature(), MainTaskFeatureKeys.OUTPUT_FILE_KEY, key);
            contextMainTask.setFeature(feature);
            context.goNextStageEventNew();

            onFinished(context);

            context.goNextStageEventNew();
            context.assertCurrentStage(stageProvider.mainTaskFinished());
        } catch (Throwable e) {
            log.error("StandaloneExportMainTaskWorker#doReduce failed, main:{}", mainTask.getMainTaskId(), e);
            ageiPort.onError(mainTask, e);
        } finally {
            IOUtils.closeQuietly(fileWriter);
            IOUtils.closeQuietly(fileStream);
        }

    }
}
