package com.alibaba.ageiport.processor.core.spi.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author lingyi
 */
@Getter
@Setter
@ToString
public class CreateSpecificationRequest {

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

}
