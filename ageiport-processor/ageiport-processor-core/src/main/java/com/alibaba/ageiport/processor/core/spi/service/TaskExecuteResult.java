package com.alibaba.ageiport.processor.core.spi.service;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class TaskExecuteResult {

    private Boolean success;

    private String mainTaskId;

    private String errorMessage;
}
