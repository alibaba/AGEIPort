package com.alibaba.ageiport.task.server.model;

import com.alibaba.ageiport.sdk.core.Request;
import lombok.*;


/**
 * @author lingyi
 */
@Getter
@Setter
@ToString
public class CreateTaskSpecificationRequest extends Request<CreateTaskSpecificationResponse> {

    private static final long serialVersionUID = -2768778380963180317L;

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
