package com.alibaba.ageiport.processor.core.spi.task.factory;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.processor.core.Context;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.spi.task.stage.StageProvider;

public interface TaskContext extends Context {

    Logger logger = LoggerFactory.getLogger(TaskContext.class);

    MainTask getMainTask();

    SubTask getSubTask();

    void setMainTask(MainTask mainTask);

    void setTaskSpec(TaskSpec taskSpec);

    TaskSpec getTaskSpec();

    BizUser getBizUser();

    void setBizUser(BizUser bizUser);

    void save();

    Stage getStage();

    void setStage(Stage stage);

    default Stage goNextStage() {
        Stage current = getStage();
        Stage next = current.next();
        setStage(next);

        return current;
    }

    Long getStageTimestamp(String code);

    default void goNextStageEventNew() {
        MainTask mainTask = getMainTask();
        String executeType = mainTask.getExecuteType();
        EventBus eventBus = getAgeiPort().getEventBusManager().getEventBus(executeType);

        Stage oldStage = goNextStage();
        Stage newStage = getStage();
        Long oldStageTimestamp = getStageTimestamp(oldStage.getCode());
        Long newStageTimestamp = getStageTimestamp(newStage.getCode());
        Long cost = null;
        if (oldStageTimestamp != null && newStageTimestamp != null) {
            cost = System.currentTimeMillis() - oldStageTimestamp;
        }
        SubTask subTask = getSubTask();
        if (subTask == null) {
            eventBus.post(TaskStageEvent.mainTaskEvent(mainTask.getMainTaskId(), newStage, cost, mainTask.getSubTotalCount()));
        } else {
            eventBus.post(TaskStageEvent.subTaskEvent(subTask.getSubTaskId(), newStage, cost));
        }
    }

    default void eventCurrentStage() {
        MainTask mainTask = getMainTask();
        String executeType = mainTask.getExecuteType();
        EventBus eventBus = getAgeiPort().getEventBusManager().getEventBus(executeType);
        Stage newStage = getStage();

        SubTask subTask = getSubTask();
        if (subTask == null) {
            eventBus.post(TaskStageEvent.mainTaskEvent(mainTask.getMainTaskId(), newStage, null, mainTask.getSubTotalCount()));
        } else {
            eventBus.post(TaskStageEvent.subTaskEvent(subTask.getSubTaskId(), newStage, null));
        }
    }

    default void assertCurrentStage(String stageCode) {
        if (!stageCode.equals(this.getStage().getCode())) {
            String error = StringUtils.format("error task stage, context:{}, expect:{}", this.getStage().getCode(), stageCode);
            throw new IllegalStateException(error);
        }
    }

    default void assertCurrentStage(Stage stage) {
        assertCurrentStage(stage.getCode());
    }


}
