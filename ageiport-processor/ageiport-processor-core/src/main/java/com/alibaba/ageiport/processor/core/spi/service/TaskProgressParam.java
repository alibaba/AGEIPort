package com.alibaba.ageiport.processor.core.spi.service;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class TaskProgressParam {
    private String mainTaskId;

    public TaskProgressParam() {
    }

    public TaskProgressParam(String mainTaskId) {
        this.mainTaskId = mainTaskId;
    }
}
