package com.alibaba.ageiport.processor.core.spi.service;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class GetTaskProgressParam {
    private String mainTaskId;

    public GetTaskProgressParam() {
    }

    public GetTaskProgressParam(String mainTaskId) {
        this.mainTaskId = mainTaskId;
    }
}
