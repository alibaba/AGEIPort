package com.alibaba.ageiport.processor.core.task.event;

import lombok.Getter;
import lombok.Setter;

import java.util.EventObject;

/**
 * @author lingyi
 */
@Getter
@Setter
public class WaitDispatchMainTaskReduceEvent extends EventObject {

    private static final long serialVersionUID = -5290410256804020892L;

    private String mainTaskId;

    public WaitDispatchMainTaskReduceEvent(String mainTaskId) {
        super(mainTaskId);
        this.mainTaskId = mainTaskId;
    }
}
