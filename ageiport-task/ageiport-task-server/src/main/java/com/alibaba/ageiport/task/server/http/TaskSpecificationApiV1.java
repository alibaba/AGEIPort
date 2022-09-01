package com.alibaba.ageiport.task.server.http;

import com.alibaba.ageiport.common.constants.RowStatus;
import com.alibaba.ageiport.common.constants.TaskSpecificationStatus;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.task.server.config.TaskServerConfig;
import com.alibaba.ageiport.task.server.entity.TaskSpecificationEntity;
import com.alibaba.ageiport.task.server.model.CreateTaskSpecificationRequest;
import com.alibaba.ageiport.task.server.model.CreateTaskSpecificationResponse;
import com.alibaba.ageiport.task.server.model.GetTaskSpecificationRequest;
import com.alibaba.ageiport.task.server.model.GetTaskSpecificationResponse;
import com.alibaba.ageiport.task.server.oauth.Oauth;
import com.alibaba.ageiport.task.server.repository.TaskSpecificationRepository;
import com.alibaba.ageiport.task.server.repository.query.TenantAppQuery;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import java.util.Date;

/**
 * @author lingyi
 */
@Slf4j
@ApplicationScoped
@Path("/v1")
@Produces("application/json")
@Consumes("application/json")
@Oauth
public class TaskSpecificationApiV1 {

    private TaskServerConfig config;

    private TaskSpecificationRepository repository;

    public TaskSpecificationApiV1(TaskServerConfig config, TaskSpecificationRepository repository) {
        this.config = config;
        this.repository = repository;
    }

    @Path("/CreateTaskSpecification")
    @POST
    public Uni<CreateTaskSpecificationResponse> createTaskSpecification(CreateTaskSpecificationRequest request) {
        if (request == null) {
            throw new WebApplicationException("invalid param", 422);
        }
        return Panache.withTransaction(() -> {
            TaskSpecificationEntity entity = BeanUtils.cloneProp(request, TaskSpecificationEntity.class);
            entity.setGmtCreate(new Date());
            entity.setGmtModified(new Date());
            entity.setRowStatus(RowStatus.VALID);
            entity.setRowVersion(1);
            entity.setStatus(TaskSpecificationStatus.ENABLE.getCode());
            entity.setEnv(config.getEnv());
            return repository.persist(entity);
        }).onItem().ifNotNull().transform(unused -> {
            CreateTaskSpecificationResponse.Data data = new CreateTaskSpecificationResponse.Data();
            data.setId(unused.getId());
            CreateTaskSpecificationResponse response = new CreateTaskSpecificationResponse();
            response.setData(data);
            response.setSuccess(true);
            return response;
        }).onFailure(Throwable.class).recoverWithItem(throwable -> {
            log.error("TaskSpecificationApiV1#create failed, request:{}", request, throwable);
            CreateTaskSpecificationResponse response = new CreateTaskSpecificationResponse();
            response.setSuccess(false);
            response.setCode("SERVER_EXCEPTION");
            response.setMessage("create task specification failed");
            return response;
        });
    }

    @Path("/GetTaskSpecification")
    @POST
    public Uni<GetTaskSpecificationResponse> getTaskSpecification(GetTaskSpecificationRequest request) {
        if (request == null) {
            throw new WebApplicationException("invalid param", 422);
        }

        TenantAppQuery filter = new TenantAppQuery(request.getTenant(), request.getNamespace(), request.getApp(), config.getEnv());

        return repository.findByTaskCode(filter, request.getTaskCode())
                .onItem().ifNotNull().transform(entity -> {
                    GetTaskSpecificationResponse response = new GetTaskSpecificationResponse();
                    response.setSuccess(true);
                    GetTaskSpecificationResponse.Data data = BeanUtils.cloneProp(entity, GetTaskSpecificationResponse.Data.class);
                    response.setData(data);
                    return response;
                })
                .onItem().ifNull().continueWith(() -> {
                    GetTaskSpecificationResponse response = new GetTaskSpecificationResponse();
                    response.setSuccess(true);
                    return response;
                })
                .onFailure(Throwable.class).recoverWithItem(throwable -> {
                    log.error("TaskSpecificationApiV1#getTaskSpecification failed, request:{}", request, throwable);
                    GetTaskSpecificationResponse response = new GetTaskSpecificationResponse();
                    response.setSuccess(false);
                    response.setCode("SERVER_EXCEPTION");
                    response.setMessage("get task specification failed");
                    return response;
                });
    }


}
