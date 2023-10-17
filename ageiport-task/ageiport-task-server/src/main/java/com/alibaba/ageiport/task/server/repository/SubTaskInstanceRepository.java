package com.alibaba.ageiport.task.server.repository;

import com.alibaba.ageiport.task.server.entity.SubTaskInstanceEntity;
import com.alibaba.ageiport.task.server.repository.query.BaseQuery;
import com.alibaba.ageiport.task.server.repository.query.TenantAppQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * @author lingyi
 */
@ApplicationScoped
public class SubTaskInstanceRepository implements PanacheRepository<SubTaskInstanceEntity> {
    public Uni<List<SubTaskInstanceEntity>> listByMainTaskId(TenantAppQuery query, String mainTaskId) {
        BaseQuery findQuery = BaseQuery.merge(query, "mainTaskId", mainTaskId);
        return this.find(findQuery.query(), findQuery.parameters()).list();
    }

    public Uni<SubTaskInstanceEntity> findBySubTaskId(TenantAppQuery query, String subTaskId) {
        BaseQuery findQuery = BaseQuery.merge(query, "subTaskId", subTaskId);
        return this.find(findQuery.query(), findQuery.parameters()).firstResult();
    }
}
