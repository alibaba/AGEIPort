package com.alibaba.ageiport.task.server.model;


import com.alibaba.ageiport.sdk.core.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lingyi
 */
@Getter
@Setter
@ToString
public class GetMainTaskInstanceRequest extends Request<GetMainTaskInstanceResponse> {

    private static final long serialVersionUID = 4023398034333722309L;

    /**
     * 任务ID
     */
    private String mainTaskId;
}