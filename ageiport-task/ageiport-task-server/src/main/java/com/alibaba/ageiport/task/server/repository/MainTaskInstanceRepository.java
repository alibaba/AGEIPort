package com.alibaba.ageiport.task.server.repository;

import com.alibaba.ageiport.task.server.entity.MainTaskInstanceEntity;
import com.alibaba.ageiport.task.server.repository.query.BaseQuery;
import com.alibaba.ageiport.task.server.repository.query.TenantAppQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author lingyi
 */
@ApplicationScoped
public class MainTaskInstanceRepository implements PanacheRepository<MainTaskInstanceEntity> {

    public Uni<MainTaskInstanceEntity> findByMainTaskId(TenantAppQuery query, String mainTaskId) {
        BaseQuery findQuery = BaseQuery.merge(query, "mainTaskId", mainTaskId);
        return this.find(findQuery.query(), findQuery.parameters()).firstResult();
    }
}
