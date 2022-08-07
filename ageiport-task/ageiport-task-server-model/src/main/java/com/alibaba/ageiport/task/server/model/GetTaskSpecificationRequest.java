package com.alibaba.ageiport.task.server.model;

import com.alibaba.ageiport.sdk.core.Request;
import lombok.*;

/**
 * @author lingyi
 */
@Getter
@Setter
@ToString
public class GetTaskSpecificationRequest extends Request<GetTaskSpecificationResponse> {

    private static final long serialVersionUID = 3528014524872921518L;

    /**
     * 任务编码
     */
    private String taskCode;
}
