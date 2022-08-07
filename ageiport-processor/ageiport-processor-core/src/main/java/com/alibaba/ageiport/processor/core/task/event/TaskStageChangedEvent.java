package com.alibaba.ageiport.processor.core.task.event;

import lombok.Getter;
import lombok.Setter;

import java.util.EventObject;

/**
 * @author lingyi
 */
@Getter
@Setter
public class TaskStageChangedEvent extends EventObject {

    private static final long serialVersionUID = -8594993039670144208L;

    private String mainTaskId;

    public TaskStageChangedEvent(String mainTaskId) {
        super(mainTaskId);
        this.mainTaskId = mainTaskId;
    }
}
