package com.alibaba.ageiport.processor.core.model.api;

/**
 * @author lingyi
 */
public interface BizUser {

    /**
     * 业务用户ID
     */
     String getBizUserId();

    /**
     * 业务用户姓名
     */
     String getBizUserName();

    /**
     * 业务用户所属租户
     */

    /**
     * 业务用户所属组织
     */
     String getBizUserOrg();

    /**
     * 业务用户key
     */
     String getBizUserKey();

    /**
     * 业务用户自定义扩展属性
     */
     String getBizUserFeature();
}
