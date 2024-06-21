package com.alibaba.ageiport.processor.core.task.exporter.worker;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.api.BizDataGroup;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.api.impl.BizExportPageImpl;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.cache.BigDataCache;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskContextFactory;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;
import com.alibaba.ageiport.processor.core.task.AbstractSubTaskWorker;
import com.alibaba.ageiport.processor.core.task.exporter.ExportProcessor;
import com.alibaba.ageiport.processor.core.task.exporter.adapter.ExportProcessorAdapter;
import com.alibaba.ageiport.processor.core.task.exporter.context.ExportSubTaskContext;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskSpecification;
import com.alibaba.ageiport.processor.core.task.exporter.stage.ExportSubTaskStageProvider;

import java.util.List;

/**
 * @author lingyi
 */
public class ExportSubTaskWorker<QUERY, DATA, VIEW> extends AbstractSubTaskWorker {

    public static Logger log = LoggerFactory.getLogger(ExportMainTaskWorker.class);

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

            ExportSubTaskContext<QUERY, DATA, VIEW> context = (ExportSubTaskContext) contextFactory.create(ageiPort, subTaskId);
            context.setStage(stageProvider.subTaskStart());
            context.eventCurrentStage();

            ExportTaskSpecification<QUERY, DATA, VIEW> exportTaskSpec = context.getExportTaskSpec();
            ExportProcessor<QUERY, DATA, VIEW> processor = exportTaskSpec.getProcessor();
            ExportProcessorAdapter<QUERY, DATA, VIEW> adapter = (ExportProcessorAdapter) processor.getConcreteAdapter();

            BizUser bizUser = context.getBizUser();
            QUERY query = context.getQuery();

            context.goNextStageEventNew(ExportSubTaskStageProvider.S04_EXECUTE_QUERY_DATA_START);
            ExportTaskRuntimeConfig runtimeConfig = context.getExportTaskRuntimeConfig();
            BizExportPageImpl page = new BizExportPageImpl();
            page.setNo(runtimeConfig.getNo());
            page.setOffset(runtimeConfig.getPageOffset());
            page.setSize(runtimeConfig.getPageSize());
            page.setAttributes(runtimeConfig.getAttributes());
            List<DATA> dataList = adapter.queryData(bizUser, query, page, exportTaskSpec.getProcessor(), context);
            context.goNextStageEventNew(ExportSubTaskStageProvider.S05_EXECUTE_QUERY_DATA_END);

            context.goNextStageEventNew(ExportSubTaskStageProvider.S06_EXECUTE_CONVERT_START);
            List<VIEW> viewList = adapter.convert(bizUser, query, dataList, exportTaskSpec.getProcessor(), context);
            context.goNextStageEventNew(ExportSubTaskStageProvider.S07_EXECUTE_CONVERT_END);

            context.goNextStageEventNew(ExportSubTaskStageProvider.S08_EXECUTE_GROUP_START);
            BizDataGroup<VIEW> group = adapter.group(bizUser, query, viewList, exportTaskSpec.getProcessor(), context);
            context.goNextStageEventNew(ExportSubTaskStageProvider.S09_EXECUTE_GROUP_END);

            context.goNextStageEventNew(ExportSubTaskStageProvider.S10_EXECUTE_TO_MAP_START);
            DataGroup dataGroup = adapter.getDataGroup(bizUser, query, group, exportTaskSpec.getProcessor(), context);
            context.goNextStageEventNew(ExportSubTaskStageProvider.S11_EXECUTE_TO_MAP_END);

            context.goNextStageEventNew(ExportSubTaskStageProvider.S12_SAVE_DATA_START);
            BigDataCache cache = ageiPort.getBigDataCacheManager().getBigDataCacheCache(subTask.getExecuteType());
            cache.put(subTaskId, dataGroup);
            context.goNextStageEventNew(ExportSubTaskStageProvider.S13_SAVE_DATA_END);

            context.goNextStageEventNew(ExportSubTaskStageProvider.S14_FINISHED);
            context.assertCurrentStage(stageProvider.subTaskFinished());
        } catch (Throwable e) {
            log.error("doMappingProcess failed, main:{}, sub:{}", subTask.getMainTaskId(), subTask.getSubTaskId(), e);
            ageiPort.onMainError(subTask.getMainTaskId(), e);
            ageiPort.onSubError(subTask, e);
        }
    }
}
