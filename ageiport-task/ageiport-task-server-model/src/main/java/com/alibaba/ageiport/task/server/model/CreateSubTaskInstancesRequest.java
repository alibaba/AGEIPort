package com.alibaba.ageiport.task.server.model;

import com.alibaba.ageiport.sdk.core.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
@ToString
public class CreateSubTaskInstancesRequest extends Request<CreateSubTaskInstancesResponse> {

    private static final long serialVersionUID = -6065890169963711895L;
    /**
     * 主任务ID
     */
    private String mainTaskId;

    /**
     * 子任务信息
     */
    private List<SubTaskInstance> subTaskInstances;

    @Getter
    @Setter
    @ToString
    public static class SubTaskInstance {

        private Integer subTaskNo;

        private String bizQuery;

        private String runtimeParam;
    }


}
