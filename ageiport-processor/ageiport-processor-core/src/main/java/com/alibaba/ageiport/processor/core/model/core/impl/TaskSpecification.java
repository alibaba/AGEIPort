package com.alibaba.ageiport.processor.core.model.core.impl;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class TaskSpecification {

    /**
     * 任务编码
     */
    private String taskCode;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 执行类型
     */
    private String taskExecuteType;

    /**
     * 任务处理器
     */
    private String taskHandler;

    /**
     * feature
     */
    private String feature;
}
