package com.alibaba.ageiport.task.server.model;

import com.alibaba.ageiport.sdk.core.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lingyi
 */
@ToString
@Setter
@Getter
public class GetTaskSpecificationResponse extends Response {

    private static final long serialVersionUID = -8335676793116010064L;

    private Data data;

    @ToString
    @Setter
    @Getter
    public static class Data {
        /**
         * 租户
         */
        private String tenant;

        /**
         * 命名空间
         */
        private String namespace;

        /**
         * 应用
         */
        private String app;

        /**
         * 环境
         */
        private String env;

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

}
