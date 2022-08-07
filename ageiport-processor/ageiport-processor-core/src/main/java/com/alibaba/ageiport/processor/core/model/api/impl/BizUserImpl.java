package com.alibaba.ageiport.processor.core.model.api.impl;

import com.alibaba.ageiport.processor.core.model.api.BizUser;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class BizUserImpl implements BizUser {

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
}
