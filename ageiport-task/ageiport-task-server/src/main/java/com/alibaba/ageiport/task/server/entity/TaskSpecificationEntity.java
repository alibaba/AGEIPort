package com.alibaba.ageiport.task.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author lingyi
 */
@Getter
@Setter
@Entity
@Table(name = "agei_task_specification",
        indexes = {
                @Index(name = "tenant_namespace_app_env_task_code", columnList = "tenant,namespace,app,env,taskCode")
        }
)
public class TaskSpecificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /**
     * 租户
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String tenant;

    /**
     * 命名空间
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String namespace;

    /**
     * 应用
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String app;

    /**
     * 环境
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String env;

    /**
     * 任务编码
     */
    @Column(length = 64, updatable = false, nullable = false)
    private String taskCode;

    /**
     * 任务名称
     */
    @Column(length = 64, nullable = false)
    private String taskName;

    /**
     * 任务描述
     */
    @Column(length = 128, nullable = false)
    private String taskDesc;

    /**
     * 任务类型
     */
    @Column(length = 64, nullable = false)
    private String taskType;

    /**
     * 执行类型
     */
    @Column(length = 64, nullable = false)
    private String taskExecuteType;

    /**
     * 任务处理器
     */
    @Column(length = 512, nullable = false)
    private String taskHandler;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @Column(nullable = false)
    private Date gmtModified;

    /**
     * 创建人ID
     */
    @Column(length = 64)
    private String creatorId;

    /**
     * 创建人姓名
     */
    @Column(length = 64)
    private String creatorName;

    /**
     * 修改人id
     */
    @Column(length = 64)
    private String modifierId;

    /**
     * 修改人姓名
     */
    @Column(length = 64)
    private String modifierName;

    /**
     * 拥有者id
     */
    @Column(length = 64)
    private String ownerId;

    /**
     * 拥有者姓名
     */
    @Column(length = 64)
    private String ownerName;

    /**
     * 状态
     */
    @Column(length = 64, nullable = false)
    private String status;

    /**
     * rowStatus
     */
    @Column(length = 64, nullable = false)
    private String rowStatus;

    /**
     * rowVersion
     */
    @Column(nullable = false)
    private Integer rowVersion;

    /**
     * feature
     */
    @Column(columnDefinition = "text default null")
    private String feature;
}