package com.alibaba.ageiport.task.server.model;

import com.alibaba.ageiport.sdk.core.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author lingyi
 */
@Getter
@Setter
@ToString
public class UpdateMainTaskInstanceRequest extends Request<UpdateMainTaskInstanceResponse> {

    private static final long serialVersionUID = 3308675521012081998L;

    /**
     * 主任务ID
     */
    private String mainTaskId;

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

    private String log;

    private String status;

    private String runtimeParam;

    private String feature;

}
