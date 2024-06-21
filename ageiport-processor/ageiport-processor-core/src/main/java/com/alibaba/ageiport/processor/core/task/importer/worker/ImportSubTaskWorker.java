package com.alibaba.ageiport.processor.core.task.importer.worker;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.CollectionUtils;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.ConstValues;
import com.alibaba.ageiport.processor.core.model.api.BizDataGroup;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.cache.BigDataCache;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskContextFactory;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;
import com.alibaba.ageiport.processor.core.task.AbstractSubTaskWorker;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;
import com.alibaba.ageiport.processor.core.task.importer.adapter.ImportProcessorAdapter;
import com.alibaba.ageiport.processor.core.task.importer.context.ImportSubTaskContext;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResult;
import com.alibaba.ageiport.processor.core.task.importer.model.BizImportResultImpl;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskSpecification;
import com.alibaba.ageiport.processor.core.task.importer.stage.ImportSubTaskStageProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingyi
 */
public class ImportSubTaskWorker<QUERY, DATA, VIEW> extends AbstractSubTaskWorker {

    public static Logger log = LoggerFactory.getLogger(ImportMainTaskWorker.class);

    @Override
    public void doMappingProcess() {
        AgeiPort ageiPort = this.getAgeiPort();
        SubTask subTask = getSubTask();
        try {
            String subTaskId = subTask.getSubTaskId();
            String executeType = subTask.getExecuteType();
            String taskType = subTask.getType();
            String taskCode = subTask.getCode();

            TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
            SubTaskContextFactory contextFactory = spiSelector.selectExtension(executeType, taskType, taskCode, SubTaskContextFactory.class);
            SubTaskStageProvider stageProvider = spiSelector.selectExtension(executeType, taskType, taskCode, SubTaskStageProvider.class);

            EventBus eventBus = ageiPort.getEventBusManager().getEventBus(executeType);
            eventBus.post(TaskStageEvent.subTaskEvent(subTaskId, stageProvider.subTaskDispatchedOnNode()));

            ImportSubTaskContext<QUERY, DATA, VIEW> context = (ImportSubTaskContext) contextFactory.create(ageiPort, subTaskId);
            context.setStage(stageProvider.subTaskStart());

            ImportTaskSpecification<QUERY, DATA, VIEW> importTaskSpec = context.getImportTaskSpec();
            ImportProcessor<QUERY, DATA, VIEW> processor = importTaskSpec.getImportProcessor();
            ImportProcessorAdapter<QUERY, DATA, VIEW> adapter = (ImportProcessorAdapter) processor.getConcreteAdapter();

            BizUser bizUser = context.getBizUser();
            QUERY query = context.getQuery();

            context.goNextStageEventNew(ImportSubTaskStageProvider.S04_EXECUTE_GET_SLICE_DATA_START);
            BigDataCache cache = ageiPort.getBigDataCacheManager().getBigDataCacheCache(executeType);
            String key = subTaskId + ConstValues.CACHE_SUFFIX_INPUT_DATA_SLICE;
            DataGroup dataGroup = cache.get(key, DataGroup.class);
            context.goNextStageEventNew(ImportSubTaskStageProvider.S05_EXECUTE_GET_SLICE_DATA_END);

            context.goNextStageEventNew(ImportSubTaskStageProvider.S06_CHECK_HEADERS_START);
            DataGroup group = adapter.checkHeaders(bizUser, query, dataGroup, processor, context);
            context.goNextStageEventNew(ImportSubTaskStageProvider.S07_CHECK_HEADERS_END);

            context.goNextStageEventNew(ImportSubTaskStageProvider.S08_EXECUTE_BIZ_DATA_GROUP_START);
            BizDataGroup<VIEW> bizDataGroup = adapter.getBizDataGroup(bizUser, query, group, processor, context);
            context.goNextStageEventNew(ImportSubTaskStageProvider.S09_EXECUTE_BIZ_DATA_GROUP_END);

            context.goNextStageEventNew(ImportSubTaskStageProvider.S10_EXECUTE_FLAT_START);
            List<VIEW> views = adapter.flat(bizUser, query, bizDataGroup, processor, context);
            context.goNextStageEventNew(ImportSubTaskStageProvider.S11_EXECUTE_FLAT_END);

            context.goNextStageEventNew(ImportSubTaskStageProvider.S12_EXECUTE_CONVERT_AND_CHECK_START);
            BizImportResult<VIEW, DATA> convertAndCheckResult = adapter.convertAndCheck(bizUser, query, views, processor, context);
            context.goNextStageEventNew(ImportSubTaskStageProvider.S13_EXECUTE_CONVERT_AND_CHECK_END);

            context.goNextStageEventNew(ImportSubTaskStageProvider.S14_EXECUTE_WRITE_START);
            List<DATA> data = convertAndCheckResult.getData();
            BizImportResult<VIEW, DATA> writeResult = adapter.write(bizUser, query, data, processor, context);
            context.goNextStageEventNew(ImportSubTaskStageProvider.S15_EXECUTE_WRITE_END);

            BizImportResultImpl<VIEW, DATA> importResult = new BizImportResultImpl<>();
            List<VIEW> resultView = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(convertAndCheckResult.getView())) {
                resultView.addAll(convertAndCheckResult.getView());
            }
            if (CollectionUtils.isNotEmpty(writeResult.getView())) {
                resultView.addAll(writeResult.getView());
            }
            importResult.setView(resultView);

            context.goNextStageEventNew(ImportSubTaskStageProvider.S16_EXECUTE_GROUP_START);
            BizDataGroup<VIEW> viewBizDataGroup = adapter.group(bizUser, query, resultView, processor, context);
            context.goNextStageEventNew(ImportSubTaskStageProvider.S17_EXECUTE_GROUP_END);

            context.goNextStageEventNew(ImportSubTaskStageProvider.S18_EXECUTE_DATA_GROUP_START);
            DataGroup outputDataGroup = adapter.getDataGroup(bizUser, query, viewBizDataGroup, processor, context);
            context.goNextStageEventNew(ImportSubTaskStageProvider.S19_EXECUTE_DATA_GROUP_END);

            context.goNextStageEventNew(ImportSubTaskStageProvider.S20_SAVE_DATA_START);
            List<DataGroup.Data> groupData = outputDataGroup.getData();
            if (CollectionUtils.isNotEmpty(groupData)) {
                boolean hasItems = false;
                for (DataGroup.Data groupDatum : groupData) {
                    if (CollectionUtils.isNotEmpty(groupDatum.getItems())) {
                        hasItems = true;
                    }
                }
                if (hasItems) {
                    cache.put(subTaskId, outputDataGroup);
                }
            }
            context.goNextStageEventNew(ImportSubTaskStageProvider.S21_SAVE_DATA_END);

            context.goNextStageEventNew(ImportSubTaskStageProvider.S22_FINISHED);
            context.assertCurrentStage(stageProvider.subTaskFinished());
        } catch (Throwable e) {
            log.info("doMappingProcess failed, main:{}, sub:{}", subTask.getMainTaskId(), subTask.getSubTaskId(), e);
            ageiPort.onMainError(subTask.getMainTaskId(), e);
            ageiPort.onSubError(subTask, e);
        }

    }
}
