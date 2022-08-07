package com.alibaba.ageiport.processor.core.task.monitor;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.task.monitor.*;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingyi
 */
public class TaskProgressServiceImpl implements TaskProgressService {

    public static Logger log = LoggerFactory.getLogger(TaskProgressServiceImpl.class);

    private Map<String, MainTaskProgress> mainTaskProgressMap = new ConcurrentHashMap<>();

    private AgeiPort ageiPort;

    private TaskProgressMonitor monitor;

    public TaskProgressServiceImpl(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        this.monitor = ageiPort.getTaskProgressMonitor();
    }

    @Override
    public void updateTaskProgress(TaskStageEvent event) {
        try {
            if (event.isMainTaskEvent()) {
                log.info("progress, main:{}, stage:{}", event.getMainTaskId(), event.getName());
                updateMainTaskProgress(event);
            } else {
                log.info("progress, main:{}, sub:{}, stage:{}", event.getMainTaskId(), event.getSubTaskId(), event.getName());
                updateSubTaskProgress(event);
            }
        } catch (Throwable e) {
            log.error("progress failed, main:{}, sub:{}, stage:{}", event.getMainTaskId(), event.getSubTaskId(), event.getName(), e);
        }

    }

    public void updateMainTaskProgress(TaskStageEvent event) {
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(event.getMainTaskId());
        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        MainTaskStageProvider mainTaskStageProvider = spiSelector.selectExtension(mainTask.getExecuteType(), mainTask.getType(), mainTask.getCode(), MainTaskStageProvider.class);

        Stage newStage = mainTaskStageProvider.getStage(event.getStage());
        Stage oldStage = null;
        TaskProgressLog log = createLog(event, newStage);
        MainTaskProgress taskProgress = getTaskProgress(event.getMainTaskId());
        if (taskProgress == null) {
            taskProgress = createMainTaskProgress(event.getMainTaskId(), newStage, event);
            mainTaskProgressMap.put(taskProgress.getMainTaskId(), taskProgress);
        } else {
            oldStage = mainTaskStageProvider.getStage(taskProgress.getStageCode());
            if (!newStage.isAfterThan(oldStage.getCode())) {
                return;
            }
            taskProgress.setStageCode(newStage.getCode());
            taskProgress.setStageName(newStage.getName());
            taskProgress.setIsFinished(newStage.isFinished());
            taskProgress.setIsError(newStage.isError());
            taskProgress.setIsFinished(newStage.isFinished());
            taskProgress.setPercent(newStage.getMaxPercent());
            if (event.getSubCount() != null) {
                taskProgress.setTotalSubTaskCount(event.getSubCount());
                if (taskProgress.getErrorSubTaskCount() == null) {
                    taskProgress.setErrorSubTaskCount(0);
                }
                if (taskProgress.getFinishedSubTaskCount() == null) {
                    taskProgress.setFinishedSubTaskCount(0);
                }
            }

        }
        taskProgress.addLog(log);
        monitor.onMainTaskChanged(taskProgress, oldStage, newStage);
    }

    public void updateSubTaskProgress(TaskStageEvent event) {
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(event.getMainTaskId());
        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        SubTaskStageProvider subTaskStageProvider = spiSelector.selectExtension(mainTask.getExecuteType(), mainTask.getType(), mainTask.getCode(), SubTaskStageProvider.class);
        MainTaskStageProvider mainTaskStageProvider = spiSelector.selectExtension(mainTask.getExecuteType(), mainTask.getType(), mainTask.getCode(), MainTaskStageProvider.class);

        Stage newStage = subTaskStageProvider.getStage(event.getStage());
        MainTaskProgress mainTaskProgress = getTaskProgress(event.getMainTaskId());
        if (mainTaskProgress == null) {
            mainTaskProgress = createMainTaskProgress(event.getMainTaskId(), mainTaskStageProvider.mainTaskCreated(), null);
            TaskProgressLog mainLog = createLog(null, mainTaskStageProvider.mainTaskCreated());
            mainTaskProgress.addLog(mainLog);
            mainTaskProgressMap.put(mainTaskProgress.getMainTaskId(), mainTaskProgress);

            SubTaskProgress subTaskProgress = createSubTaskProgress(event.getSubTaskId(), newStage);
            TaskProgressLog subLog = createLog(event, newStage);
            subTaskProgress.addLog(subLog);
            mainTaskProgress.addSubTaskProgress(subTaskProgress);
        }
        Stage mainStage = mainTaskStageProvider.getStage(mainTaskProgress.getStageCode());

        SubTaskProgress subTaskProgress = mainTaskProgress.getSubTaskProgress(event.getSubTaskId());
        if (subTaskProgress == null) {
            subTaskProgress = createSubTaskProgress(event.getSubTaskId(), newStage);
            TaskProgressLog subLog = createLog(event, newStage);
            subTaskProgress.addLog(subLog);
            mainTaskProgress.addSubTaskProgress(subTaskProgress);
        }
        Stage oldStage = subTaskStageProvider.getStage(subTaskProgress.getStageCode());

        TaskProgressLog subLog = createLog(event, newStage);
        subTaskProgress.addLog(subLog);
        subTaskProgress.setStageCode(newStage.getCode());
        subTaskProgress.setStageName(newStage.getName());
        subTaskProgress.setIsFinished(newStage.isFinished());
        subTaskProgress.setIsError(newStage.isError());
        subTaskProgress.setPercent(newStage.getMaxPercent());

        monitor.onSubTaskChanged(mainTaskProgress, oldStage, newStage, mainStage);
    }

    private SubTaskProgress createSubTaskProgress(String subTaskId, Stage newStage) {
        SubTaskProgress subTaskProgress = new SubTaskProgress();
        subTaskProgress.setSubTaskId(subTaskId);
        subTaskProgress.setLogs(new ArrayList<>());
        subTaskProgress.setStageCode(newStage.getCode());
        subTaskProgress.setStageName(newStage.getName());
        subTaskProgress.setIsError(newStage.isError());
        subTaskProgress.setIsFinished(newStage.isFinished());
        subTaskProgress.setPercent(newStage.getMaxPercent());
        return subTaskProgress;
    }


    private MainTaskProgress createMainTaskProgress(String mainTaskId, Stage newStage, TaskStageEvent event) {
        MainTaskProgress mainTaskProgress = new MainTaskProgress();
        mainTaskProgress.setMainTaskId(mainTaskId);
        mainTaskProgress.setStageCode(newStage.getCode());
        mainTaskProgress.setStageName(newStage.getName());
        mainTaskProgress.setIsError(newStage.isError());
        mainTaskProgress.setIsFinished(newStage.isFinished());
        mainTaskProgress.setPercent(newStage.getMaxPercent());
        if (event != null) {
            mainTaskProgress.setTotalSubTaskCount(event.getSubCount());
        }
        if (mainTaskProgress.getErrorSubTaskCount() == null) {
            mainTaskProgress.setErrorSubTaskCount(0);
        }
        if (mainTaskProgress.getFinishedSubTaskCount() == null) {
            mainTaskProgress.setFinishedSubTaskCount(0);
        }
        mainTaskProgress.setLogs(new ArrayList<>());
        mainTaskProgress.setSubTaskProgressMap(new ConcurrentHashMap<>());
        return mainTaskProgress;
    }

    private static TaskProgressLog createLog(TaskStageEvent event, Stage newStage) {
        TaskProgressLog taskProgressLog = new TaskProgressLog();
        taskProgressLog.setStage(newStage.getCode());
        taskProgressLog.setName(newStage.getName());
        taskProgressLog.setOrder(newStage.getOrder());
        if (event != null) {
            taskProgressLog.setCost(event.getCost());
            taskProgressLog.setDate(event.getDate());
        }
        return taskProgressLog;
    }


    @Override
    public MainTaskProgress getTaskProgress(String mainTaskId) {
        return mainTaskProgressMap.get(mainTaskId);
    }

    @Override
    public MainTaskProgress removeTaskProgress(String mainTaskId) {
        return mainTaskProgressMap.remove(mainTaskId);
    }

}
