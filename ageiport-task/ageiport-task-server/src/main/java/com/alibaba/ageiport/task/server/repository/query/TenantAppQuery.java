package com.alibaba.ageiport.task.server.repository.query;

/**
 * TenantAppFilter
 *
 * @author lingyi
 */
public class TenantAppQuery extends BaseQuery {

    public TenantAppQuery(String tenant, String namespace, String app, String env) {
        this.addIgnoreNull("tenant", tenant);
        this.addIgnoreNull("namespace", namespace);
        this.addIgnoreNull("app", app);
        this.addIgnoreNull("env", env);
    }
}
