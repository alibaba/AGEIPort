package com.alibaba.ageiport.task.server.model;


import com.alibaba.ageiport.sdk.core.Request;
import lombok.*;

/**
 * @author lingyi
 */
@Getter
@Setter
@ToString
public class GetSubTaskInstanceRequest extends Request<GetSubTaskInstanceResponse> {

    private static final long serialVersionUID = 2770527285818474078L;

    /**
     * 任务ID
     */
    private String subTaskId;
}