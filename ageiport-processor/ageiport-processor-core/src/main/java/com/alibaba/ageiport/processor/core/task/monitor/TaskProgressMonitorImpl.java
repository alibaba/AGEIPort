package com.alibaba.ageiport.processor.core.task.monitor;

import com.alibaba.ageiport.common.collections.map.ConcurrentHashSet;
import com.alibaba.ageiport.common.constants.ConstValues;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.publisher.ManageablePublisher;
import com.alibaba.ageiport.processor.core.spi.publisher.PublishPayload;
import com.alibaba.ageiport.processor.core.spi.publisher.PublisherManager;
import com.alibaba.ageiport.processor.core.spi.task.monitor.MainTaskProgress;
import com.alibaba.ageiport.processor.core.spi.task.monitor.SubTaskProgress;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskProgressMonitor;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.task.event.TaskStageChangedEvent;

import java.util.EventObject;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author lingyi
 */
public class TaskProgressMonitorImpl implements TaskProgressMonitor {

    public static Logger logger = LoggerFactory.getLogger(TaskProgressServiceImpl.class);

    private AgeiPort ageiPort;

    private Set<String> taskToClear;

    private ClearTask clearTask;


    public TaskProgressMonitorImpl(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        this.taskToClear = new ConcurrentHashSet<>();
        this.clearTask = new ClearTask("TaskProgress Clear Task");
    }


    @Override
    public void onMainTaskChanged(MainTaskProgress mainTaskProgress, Stage oldStage, Stage newStage) {

        logger.info("onMainTaskChanged, main:{}, old:{}, new:{} ", mainTaskProgress.getMainTaskId(), oldStage == null ? null : oldStage.getCode(), newStage.getCode());

        Class<? extends EventObject> triggerEvent = newStage.getTriggerEvent();
        PublisherManager publisherManager = ageiPort.getPublisherManager();
        if (triggerEvent != null) {
            ManageablePublisher<? extends EventObject> publisher = publisherManager.getPublisher(triggerEvent);
            PublishPayload payload = new PublishPayload();
            payload.setMainTaskId(mainTaskProgress.getMainTaskId());
            publisher.publish(payload);
        }

        ManageablePublisher<TaskStageChangedEvent> publisher = publisherManager.getPublisher(TaskStageChangedEvent.class);
        if (publisher != null) {
            PublishPayload payload = new PublishPayload();
            payload.setMainTaskId(mainTaskProgress.getMainTaskId());
            publisher.publish(payload);
        }

        if (!taskToClear.contains(mainTaskProgress.getMainTaskId())) {
            taskToClear.add(mainTaskProgress.getMainTaskId());
            clearTask.addClearTask(mainTaskProgress.getMainTaskId(), ConstValues.MAX_TIMEOUT_TIME_MS, () -> {
                ageiPort.getTaskProgressService().removeTaskProgress(mainTaskProgress.getMainTaskId());
                taskToClear.remove(mainTaskProgress.getMainTaskId());
            });
        }
    }


    @Override
    public void onSubTaskChanged(MainTaskProgress mainTaskProgress, SubTaskProgress subTaskProgress, Stage oldStage, Stage newStage, Stage mainTaskStage) {
        Map<String, SubTaskProgress> subTaskProgressMap = mainTaskProgress.getSubTaskProgressMap();

        MainTaskStageProvider mainStageProvider = (MainTaskStageProvider) mainTaskStage.getStageProvider();
        Stage allSubTaskExecuteStart = mainStageProvider.subTaskExecuteStart();
        Stage allSubTaskExecuteEnd = mainStageProvider.subTaskExecuteEnd();

        Integer totalSubTaskCount = mainTaskProgress.getTotalSubTaskCount();
        if (totalSubTaskCount != null) {
            Integer currentSuccessSubTaskCount = 0;
            Integer currentErrorSubTaskCount = 0;
            Double currentSubTaskPercentSum = 0D;

            for (SubTaskProgress value : subTaskProgressMap.values()) {
                if (value.getIsFinished()) {
                    currentSuccessSubTaskCount += 1;
                } else if (value.getIsError()) {
                    currentErrorSubTaskCount += 1;
                }
                currentSubTaskPercentSum += value.getPercent();
            }
            Integer currentFinishedSubTaskCount = currentSuccessSubTaskCount + currentErrorSubTaskCount;

            Integer oldErrorSubTaskCount = mainTaskProgress.getErrorSubTaskCount();
            Integer oldSuccessSubTaskCount = mainTaskProgress.getSuccessSubTaskCount();
            Integer oldFinishedSubTaskCount = mainTaskProgress.getFinishedSubTaskCount();
            if (!Objects.equals(oldErrorSubTaskCount, currentErrorSubTaskCount) || !Objects.equals(oldSuccessSubTaskCount, currentSuccessSubTaskCount) || !currentFinishedSubTaskCount.equals(oldFinishedSubTaskCount)) {
                MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(mainTaskProgress.getMainTaskId());
                mainTask.setSubFinishedCount(currentFinishedSubTaskCount);
                mainTask.setSubFailedCount(currentErrorSubTaskCount);
                mainTask.setSubSuccessCount(currentSuccessSubTaskCount);
                ageiPort.getTaskServerClient().updateMainTask(mainTask);
            }
            
            mainTaskProgress.setFinishedSubTaskCount(currentFinishedSubTaskCount);
            mainTaskProgress.setSuccessSubTaskCount(currentSuccessSubTaskCount);
            mainTaskProgress.setErrorSubTaskCount(currentErrorSubTaskCount);

            Double subTaskExecuteWeight = allSubTaskExecuteEnd.getMaxPercent() - allSubTaskExecuteStart.getMinPercent();
            Double avgSubTaskPercent = currentSubTaskPercentSum / totalSubTaskCount;
            Double subTaskExecutePercentOfMain = avgSubTaskPercent * subTaskExecuteWeight;
            Double mainTaskPercent = allSubTaskExecuteStart.getMinPercent() + subTaskExecutePercentOfMain;
            if (mainTaskProgress.getPercent() < mainTaskPercent) {
                mainTaskProgress.setPercent(mainTaskPercent);
            }

            logger.info("onSubTaskChanged, main:{}, sub:{}, total:{}, finished:{}, error:{}", mainTaskProgress.getMainTaskId(), subTaskProgress.getSubTaskId(), totalSubTaskCount, currentSuccessSubTaskCount, currentErrorSubTaskCount);

            if (totalSubTaskCount.equals(currentSuccessSubTaskCount + currentErrorSubTaskCount)) {
                Class<? extends EventObject> triggerEvent = allSubTaskExecuteEnd.getTriggerEvent();
                ManageablePublisher<? extends EventObject> publisher = ageiPort.getPublisherManager().getPublisher(triggerEvent);
                PublishPayload payload = new PublishPayload();
                payload.setMainTaskId(mainTaskProgress.getMainTaskId());
                publisher.publish(payload);
            }
        } else {
            logger.info("onSubTaskChanged totalSubTaskCount is null, main:{}", mainTaskProgress.getMainTaskId());
        }

        ManageablePublisher<TaskStageChangedEvent> publisher = ageiPort.getPublisherManager().getPublisher(TaskStageChangedEvent.class);
        if (publisher != null) {
            PublishPayload payload = new PublishPayload();
            payload.setMainTaskId(mainTaskProgress.getMainTaskId());
            publisher.publish(payload);
        }
    }

}
