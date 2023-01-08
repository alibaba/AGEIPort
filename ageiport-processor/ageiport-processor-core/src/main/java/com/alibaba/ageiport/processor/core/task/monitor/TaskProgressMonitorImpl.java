package com.alibaba.ageiport.processor.core.task.monitor;

import com.alibaba.ageiport.common.collections.map.ConcurrentHashSet;
import com.alibaba.ageiport.common.constants.ConstValues;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.publisher.ManageablePublisher;
import com.alibaba.ageiport.processor.core.spi.publisher.PublishPayload;
import com.alibaba.ageiport.processor.core.spi.task.monitor.MainTaskProgress;
import com.alibaba.ageiport.processor.core.spi.task.monitor.SubTaskProgress;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskProgressMonitor;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.task.event.TaskStageChangedEvent;

import java.util.EventObject;
import java.util.Map;
import java.util.Set;

/**
 * @author lingyi
 */
public class TaskProgressMonitorImpl implements TaskProgressMonitor {

    public static Logger log = LoggerFactory.getLogger(TaskProgressServiceImpl.class);

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
        Class<? extends EventObject> triggerEvent = newStage.getTriggerEvent();
        if (triggerEvent != null) {
            ManageablePublisher<? extends EventObject> publisher = ageiPort.getPublisherManager().getPublisher(triggerEvent);
            PublishPayload payload = new PublishPayload();
            payload.setMainTaskId(mainTaskProgress.getMainTaskId());
            publisher.publish(payload);
        }
        ManageablePublisher<TaskStageChangedEvent> publisher = ageiPort.getPublisherManager().getPublisher(TaskStageChangedEvent.class);
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
        Stage subTaskExecuteStart = mainStageProvider.subTaskExecuteStart();
        Stage subTaskExecuteEnd = mainStageProvider.subTaskExecuteEnd();

        Integer totalSubTaskCount = mainTaskProgress.getTotalSubTaskCount();
        if (totalSubTaskCount != null) {
            Integer currentFinishedSubTaskCount = 0;
            Integer currentErrorSubTaskCount = 0;
            Double currentSubTaskPercentSum = 0D;

            for (SubTaskProgress value : subTaskProgressMap.values()) {
                if (value.getIsFinished()) {
                    currentFinishedSubTaskCount += 1;
                } else if (value.getIsError()) {
                    currentErrorSubTaskCount += 1;
                }
                currentSubTaskPercentSum += value.getPercent();
            }
            mainTaskProgress.setFinishedSubTaskCount(currentFinishedSubTaskCount);
            mainTaskProgress.setErrorSubTaskCount(currentErrorSubTaskCount);

            Double subTaskExecuteWeight = subTaskExecuteEnd.getMaxPercent() - subTaskExecuteStart.getMinPercent();
            Double avgSubTaskPercent = currentSubTaskPercentSum / totalSubTaskCount;
            Double subTaskExecutePercentOfMain = avgSubTaskPercent * subTaskExecuteWeight;
            Double mainTaskPercent = subTaskExecuteStart.getMinPercent() + subTaskExecutePercentOfMain;
            if (mainTaskProgress.getPercent() < mainTaskPercent) {
                mainTaskProgress.setPercent(mainTaskPercent);
            }

            log.info("onSubTaskChanged, main:{}, total:{}, finished:{}, error:{}", mainTaskProgress.getMainTaskId(), subTaskProgress.getSubTaskId(), totalSubTaskCount, currentFinishedSubTaskCount, currentErrorSubTaskCount);

            if (totalSubTaskCount.equals(currentFinishedSubTaskCount + currentErrorSubTaskCount)) {
                Class<? extends EventObject> triggerEvent = subTaskExecuteEnd.getTriggerEvent();
                ManageablePublisher<? extends EventObject> publisher = ageiPort.getPublisherManager().getPublisher(triggerEvent);
                PublishPayload payload = new PublishPayload();
                payload.setMainTaskId(mainTaskProgress.getMainTaskId());
                publisher.publish(payload);
            }
        } else {
            log.info("onSubTaskChanged totalSubTaskCount is null, main:{}", mainTaskProgress.getMainTaskId());
        }

        ManageablePublisher<TaskStageChangedEvent> publisher = ageiPort.getPublisherManager().getPublisher(TaskStageChangedEvent.class);
        if (publisher != null) {
            PublishPayload payload = new PublishPayload();
            payload.setMainTaskId(mainTaskProgress.getMainTaskId());
            publisher.publish(payload);
        }
    }

}
