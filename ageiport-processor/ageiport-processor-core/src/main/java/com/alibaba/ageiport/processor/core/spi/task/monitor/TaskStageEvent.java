package com.alibaba.ageiport.processor.core.spi.task.monitor;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.DateUtils;
import com.alibaba.ageiport.common.utils.TaskIdUtil;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.EventObject;

/**
 * @author lingyi
 */
@Getter
@Setter
public class TaskStageEvent extends EventObject {

    public static final Logger log = LoggerFactory.getLogger(TaskStageEvent.class);

    private static final long serialVersionUID = 5839929868828876453L;

    private String mainTaskId;

    private String subTaskId;

    private String stage;

    private String name;
    private String date;

    private Long cost;

    private Integer subCount;


    public boolean isMainTaskEvent() {
        return subTaskId == null;
    }

    public boolean isSubTaskEvent() {
        return !isMainTaskEvent();
    }

    public TaskStageEvent(String id, String mainTaskId, String subTaskId, String stageCode, String stageName, Long cost, Integer subCount) {
        super(id);
        this.mainTaskId = mainTaskId;
        this.subTaskId = subTaskId;
        this.stage = stageCode;
        this.name = stageName;
        this.date = DateUtils.format(new Date(), DateUtils.NORM_DATETIME_FORMAT);
        this.cost = cost;
        this.subCount = subCount;
        if (subTaskId == null) {
            log.info("main:{}, stage:{}, cost:{}ms", mainTaskId, stageName, cost == null ? "-" : cost);
        } else {
            log.info("main:{}, sub:{}, stage:{}, cost:{}ms", mainTaskId, subTaskId, stageName, cost == null ? "-" : cost);
        }

    }


    public static TaskStageEvent mainTaskEvent(String mainTaskId, Stage stage) {
        TaskStageEvent event = new TaskStageEvent(mainTaskId, mainTaskId, null, stage.getCode(), stage.getName(), null, null);
        return event;
    }

    public static TaskStageEvent mainTaskEvent(String mainTaskId, Stage stage, Long cost) {
        TaskStageEvent event = new TaskStageEvent(mainTaskId, mainTaskId, null, stage.getCode(), stage.getName(), cost, null);
        return event;
    }

    public static TaskStageEvent mainTaskEvent(String mainTaskId, Stage stage, Long cost, Integer subCount) {
        TaskStageEvent event = new TaskStageEvent(mainTaskId, mainTaskId, null, stage.getCode(), stage.getName(), cost, subCount);
        return event;
    }

    public static TaskStageEvent subTaskEvent(String subTaskId, Stage stage) {
        String mainTaskId = TaskIdUtil.getMainTaskId(subTaskId);
        TaskStageEvent event = new TaskStageEvent(subTaskId, mainTaskId, subTaskId, stage.getCode(), stage.getName(), null, null);
        return event;
    }

    public static TaskStageEvent subTaskEvent(String subTaskId, Stage stage, Long cost) {
        String mainTaskId = TaskIdUtil.getMainTaskId(subTaskId);
        TaskStageEvent event = new TaskStageEvent(subTaskId, mainTaskId, subTaskId, stage.getCode(), stage.getName(), cost, null);
        return event;
    }
}
