package com.alibaba.ageiport.processor.core.spi.client;

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
public class CreateSubTasksRequest {

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
