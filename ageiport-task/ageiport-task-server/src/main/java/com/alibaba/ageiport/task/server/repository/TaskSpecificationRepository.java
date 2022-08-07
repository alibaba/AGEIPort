package com.alibaba.ageiport.task.server.repository;

import com.alibaba.ageiport.task.server.entity.TaskSpecificationEntity;
import com.alibaba.ageiport.task.server.repository.query.BaseQuery;
import com.alibaba.ageiport.task.server.repository.query.TenantAppQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author lingyi
 */
@ApplicationScoped
public class TaskSpecificationRepository implements PanacheRepository<TaskSpecificationEntity> {

    public Uni<TaskSpecificationEntity> findByTaskCode(TenantAppQuery query, String taskCode) {
        BaseQuery findQuery = BaseQuery.merge(query, "taskCode", taskCode);
        return this.find(findQuery.query(), findQuery.parameters()).firstResult();
    }
}
