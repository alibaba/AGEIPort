package com.alibaba.ageiport.processor.core.task.event;

import lombok.Getter;
import lombok.Setter;

import java.util.EventObject;

/**
 * @author lingyi
 */

@Getter
@Setter
public class WaitDispatchSubTaskEvent extends EventObject {

    private static final long serialVersionUID = -4079144433908101774L;

    private String mainTaskId;

    public WaitDispatchSubTaskEvent(String mainTaskId) {
        super(mainTaskId);
        this.mainTaskId = mainTaskId;
    }

}
