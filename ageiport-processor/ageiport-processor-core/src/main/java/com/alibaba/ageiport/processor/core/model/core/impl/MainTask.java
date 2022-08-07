package com.alibaba.ageiport.processor.core.model.core.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author lingyi
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
public class MainTask {

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

    /**
     * 任务组ID
     */
    private String flowTaskId;

    /**
     * 流程顺序
     */
    private Integer flowOrder;

    /**
     * 任务ID
     */
    private String mainTaskId;

    /**
     * 任务编码
     */
    private String code;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务类型
     */
    private String type;

    /**
     * 任务执行类型
     */
    private String executeType;

    /**
     * 子任务处理总量
     */
    private Integer subTotalCount;

    /**
     * 子任务已处理量
     */
    private Integer subFinishedCount;

    /**
     * 子任务处理成功量
     */
    private Integer subSuccessCount;

    /**
     * 子任务处理失败量
     */
    private Integer subFailedCount;

    /**
     * 数据处理总量
     */
    private Integer dataTotalCount;

    /**
     * 数据已处理量
     */
    private Integer dataProcessedCount;

    /**
     * 数据处理成功量
     */
    private Integer dataSuccessCount;

    /**
     * 数据处理失败量
     */
    private Integer dataFailedCount;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 任务执行节点
     */
    private String host;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 任务开始时间
     */
    private Date gmtStart;

    /**
     * 任务开始分发时间
     */
    private Date gmtDispatch;

    /**
     * 任务开始执行时间
     */
    private Date gmtExecute;

    /**
     * 任务结束时间
     */
    private Date gmtFinished;

    /**
     * 任务过期时间
     */
    private Date gmtExpired;

    /**
     * Trace
     */
    private String traceId;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 错误Code
     */
    private String resultCode;

    /**
     * 错误信息
     */
    private String resultMessage;

    /**
     * rowStatus
     */
    private String rowStatus;

    /**
     * rowVersion
     */
    private Integer rowVersion;

    /**
     * 任务日志
     */
    private String log;

    /**
     * 扩展字段，JSON格式
     */
    private String feature;

    /**
     * 任务运行时参数
     */
    private String runtimeParam;
}
