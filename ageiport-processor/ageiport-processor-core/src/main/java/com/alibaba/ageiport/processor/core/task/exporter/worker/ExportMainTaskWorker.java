package com.alibaba.ageiport.processor.core.task.exporter.worker;

import com.alibaba.ageiport.common.feature.FeatureUtils;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.IOUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.TaskIdUtil;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.ext.file.store.FileStore;
import com.alibaba.ageiport.processor.core.AgeiPort;
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
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.file.FileContext;
import com.alibaba.ageiport.processor.core.spi.file.FileWriter;
import com.alibaba.ageiport.processor.core.spi.file.FileWriterFactory;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskContextFactory;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.slice.SliceStrategy;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;
import com.alibaba.ageiport.processor.core.task.AbstractMainTaskWorker;
import com.alibaba.ageiport.processor.core.task.exporter.ExportProcessor;
import com.alibaba.ageiport.processor.core.task.exporter.adapter.ExportProcessorAdapter;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.context.ExportMainTaskContext;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfigImpl;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskSpecification;
import com.alibaba.ageiport.processor.core.task.exporter.slice.ExportSlice;
import com.alibaba.ageiport.processor.core.task.exporter.slice.ExportSliceStrategy;
import com.alibaba.ageiport.processor.core.utils.HeadersUtil;

import java.io.InputStream;
import java.util.*;

/**
 * @author lingyi
 */
public class ExportMainTaskWorker<QUERY, DATA, VIEW> extends AbstractMainTaskWorker {

    public static Logger log = LoggerFactory.getLogger(ExportMainTaskWorker.class);

    public ExportMainTaskWorker(AgeiPort ageiPort, MainTask mainTask) {
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
        try {
            TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();

            MainTaskStageProvider stageProvider = spiSelector.selectExtension(executeType, taskType, taskCode, MainTaskStageProvider.class);
            MainTaskContextFactory contextFactory = spiSelector.selectExtension(executeType, taskType, taskCode, MainTaskContextFactory.class);
            ExportMainTaskContext<QUERY, DATA, VIEW> context = (ExportMainTaskContext) contextFactory.create(ageiPort, mainTask);
            context.getMainTask().setStatus(TaskStatus.EXECUTING);
            context.getMainTask().setGmtStart(new Date());
            context.save();
            context.setStage(stageProvider.mainTaskStart());

            ExportTaskSpecification<QUERY, DATA, VIEW> taskSpec = context.getExportTaskSpec();
            ExportProcessor<QUERY, DATA, VIEW> processor = taskSpec.getProcessor();
            ExportProcessorAdapter<QUERY, DATA, VIEW> adapter = (ExportProcessorAdapter) processor.getConcreteAdapter();

            BizUser bizUser = context.getBizUser();
            QUERY query = context.getQuery();

            context.goNextStageEventNew();
            BizExportTaskRuntimeConfig bizExportTaskRuntimeConfig = adapter.taskRuntimeConfig(bizUser, query, processor, context);
            context.load(bizExportTaskRuntimeConfig);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            QUERY resetQuery = adapter.resetQuery(bizUser, query, processor, context);
            context.load(resetQuery);
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            Integer totalCount = adapter.totalCount(bizUser, query, processor, context);
            context.load(totalCount);
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
                    columnHeader.setIgnoreHeader(columnHeader.getErrorHeader());
                }
            }
            context.load(columnHeaders);

            context.goNextStageEventNew();
            String sliceStrategyName = context.getExportTaskRuntimeConfig().getTaskSliceStrategy();
            ExportSliceStrategy<QUERY, DATA, VIEW> sliceStrategy = (ExportSliceStrategy) ExtensionLoader.getExtensionLoader(SliceStrategy.class).getExtension(sliceStrategyName);
            List<ExportSlice> slices = sliceStrategy.slice(context);
            context.load(slices);
            context.goNextStageEventNew();

            context.save();

            context.goNextStageEventNew();
            List<CreateSubTasksRequest.SubTaskInstance> subTaskInstances = new ArrayList<>();
            for (ExportSlice slice : slices) {
                CreateSubTasksRequest.SubTaskInstance subTaskInstance = new CreateSubTasksRequest.SubTaskInstance();
                subTaskInstance.setSubTaskNo(slice.getNo());
                subTaskInstance.setBizQuery(slice.getQueryJson());
                Map<String, Object> mainRuntimeParam = Model.toMap(context.getExportTaskRuntimeConfig());
                ExportTaskRuntimeConfigImpl sliceRuntimeConfig = Model.toModel(mainRuntimeParam, new ExportTaskRuntimeConfigImpl());
                sliceRuntimeConfig.setNo(slice.getNo());
                sliceRuntimeConfig.setPageOffset(slice.getOffset());
                sliceRuntimeConfig.setPageSize(slice.getSize());
                String runtimeParam = FeatureUtils.merge(JsonUtil.toJsonString(mainRuntimeParam), JsonUtil.toJsonString(sliceRuntimeConfig));
                subTaskInstance.setRuntimeParam(runtimeParam);
                subTaskInstances.add(subTaskInstance);
            }
            CreateSubTasksRequest createSubTasksRequest = new CreateSubTasksRequest();
            createSubTasksRequest.setMainTaskId(mainTaskId);
            createSubTasksRequest.setSubTaskInstances(subTaskInstances);
            ageiPort.getTaskServerClient().createSubTask(createSubTasksRequest);

            SubTaskStageProvider subTaskStageProvider = spiSelector.selectExtension(executeType, taskType, taskCode, SubTaskStageProvider.class);
            Stage subTaskCreated = subTaskStageProvider.subTaskCreated();
            for (ExportSlice slice : slices) {
                String subTaskId = TaskIdUtil.genSubTaskId(mainTaskId, slice.getNo());
                ageiPort.getEventBusManager().getEventBus(executeType).post(TaskStageEvent.subTaskEvent(subTaskId, subTaskCreated));
            }

            context.goNextStageEventNew();
            context.assertCurrentStage(stageProvider.mainTaskSaveSliceEnd());
        } catch (Throwable e) {
            log.error("StandaloneExportMainTaskWorker#doPrepare failed, main:{}", mainTaskId, e);
            ageiPort.onError(mainTask, e);
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

            ExportMainTaskContext<QUERY, DATA, VIEW> context = (ExportMainTaskContext) contextFactory.create(ageiPort, mainTask);
            context.setStage(stageProvider.mainTaskReduceStart());
            context.eventCurrentStage();

            ExportTaskRuntimeConfig runtimeConfig = context.getExportTaskRuntimeConfig();
            String fileWriterFactoryName = ageiPort.getOptions().getFileTypeWriterSpiMappings().get(runtimeConfig.getFileType());
            FileWriterFactory fileWriterFactory = ExtensionLoader.getExtensionLoader(FileWriterFactory.class).getExtension(fileWriterFactoryName);
            ColumnHeaders columnHeaders = context.getColumnHeaders();

            FileContext fileContext = new FileContext();
            fileContext.setBizQuery(JsonUtil.toJsonString(mainTask.getBizQuery()));
            fileContext.setTaskSpec(context.getExportTaskSpec());
            fileContext.setMainTask(mainTask);
            fileWriter = fileWriterFactory.create(ageiPort, columnHeaders, fileContext);

            for (int i = 1; i <= mainTask.getSubTotalCount(); i++) {
                String subTaskId = TaskIdUtil.genSubTaskId(mainTask.getMainTaskId(), i);
                BigDataCache cache = ageiPort.getBigDataCacheManager().getBigDataCacheCache(mainTask.getExecuteType());
                DataGroup dataGroup = cache.remove(subTaskId, DataGroup.class);
                fileWriter.write(dataGroup);
            }
            fileStream = fileWriter.finish();
            context.goNextStageEventNew();

            context.goNextStageEventNew();
            FileStore fileStore = ageiPort.getFileStore();
            String key = mainTask.getMainTaskId() + "." + runtimeConfig.getFileType();
            fileStore.save(key, fileStream, new HashMap<>());
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
