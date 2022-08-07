package com.alibaba.ageiport.processor.core.spi.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author lingyi
 */
@Getter
@Setter
@ToString
public class TaskExecuteParam {

    /**
     * 任务编码
     */
    private String taskSpecificationCode;

    /**
     * 业务任务名称
     */
    private String bizTaskName;

    /**
     * 业务Key
     */
    private String bizKey;

    /**
     * 业务用户查询参数
     */
    private String bizQuery;

    /**
     * 业务用户ID
     */
    private String bizUserId;

    /**
     * 业务用户姓名
     */
    private String bizUserName;

    /**
     * 业务用户所属租户
     */
    private String bizUserTenant;

    /**
     * 业务用户所属组织
     */
    private String bizUserOrg;

    /**
     * 业务用户key
     */
    private String bizUserKey;

    /**
     * 业务用户自定义扩展属性
     */
    private String bizUserFeature;

    private Map<String, String> labels;

    private String inputFileKey;
}
