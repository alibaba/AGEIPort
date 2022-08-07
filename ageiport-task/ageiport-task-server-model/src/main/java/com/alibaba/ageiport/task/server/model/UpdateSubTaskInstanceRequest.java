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
public class UpdateSubTaskInstanceRequest extends Request<UpdateSubTaskInstanceResponse> {

    private static final long serialVersionUID = 8614644357204437511L;
    /**
     * 子任务ID
     */
    private String subTaskId;

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


    private String host;

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
