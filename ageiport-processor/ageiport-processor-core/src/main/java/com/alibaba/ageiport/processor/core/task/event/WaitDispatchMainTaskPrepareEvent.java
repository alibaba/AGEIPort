package com.alibaba.ageiport.processor.core.task.event;

import lombok.Getter;
import lombok.Setter;

import java.util.EventObject;
import java.util.Map;

/**
 * @author lingyi
 */

@Getter
@Setter
public class WaitDispatchMainTaskPrepareEvent extends EventObject {

    private static final long serialVersionUID = -4079144433908101774L;

    private String mainTaskId;

    public WaitDispatchMainTaskPrepareEvent(String mainTaskId) {
        super(mainTaskId);
        this.mainTaskId = mainTaskId;
    }

}
