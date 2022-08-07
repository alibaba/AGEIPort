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
public class CreateMainTaskInstanceRequest extends Request<CreateMainTaskInstanceResponse> {

    private static final long serialVersionUID = -4847388998783534763L;

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
     * traceId
     */
    private String traceId;

    /**
     * host
     */
    private String host;

    /**
     * 运行时参数
     */
    private String runtimeParam;
}
