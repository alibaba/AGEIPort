package com.alibaba.ageiport.task.server.http;

import com.alibaba.ageiport.common.constants.RowStatus;
import com.alibaba.ageiport.common.constants.TaskStatus;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.common.utils.TaskIdUtil;
import com.alibaba.ageiport.task.server.config.TaskServerConfig;
import com.alibaba.ageiport.task.server.entity.MainTaskInstanceEntity;
import com.alibaba.ageiport.task.server.model.*;
import com.alibaba.ageiport.task.server.oauth.Oauth;
import com.alibaba.ageiport.task.server.repository.MainTaskInstanceRepository;
import com.alibaba.ageiport.task.server.repository.query.TenantAppQuery;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import lombok.extern.slf4j.Slf4j;

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
public class MainTaskInstanceApiV1 {

    private TaskServerConfig config;

    private MainTaskInstanceRepository repository;

    public MainTaskInstanceApiV1(TaskServerConfig config, MainTaskInstanceRepository repository) {
        this.config = config;
        this.repository = repository;
    }

    @Path("/CreateMainTaskInstance")
    @POST
    public Uni<CreateMainTaskInstanceResponse> createMainTaskInstance(CreateMainTaskInstanceRequest request) {
        if (request == null) {
            throw new WebApplicationException("invalid param", 422);
        }

        return Panache.withTransaction(() -> {
            String mainTaskId = TaskIdUtil.genMainTaskId();

            MainTaskInstanceEntity entity = BeanUtils.cloneProp(request, MainTaskInstanceEntity.class);
            entity.setGmtCreate(new Date());
            entity.setGmtModified(new Date());
            entity.setMainTaskId(mainTaskId);
            entity.setRowStatus(RowStatus.VALID);
            entity.setRowVersion(1);
            entity.setRetryTimes(0);
            entity.setStatus(TaskStatus.NEW.getCode());
            entity.setEnv(config.getEnv());
            return repository.persist(entity);
        }).onItem().ifNotNull().transform(unused -> {
            CreateMainTaskInstanceResponse response = new CreateMainTaskInstanceResponse();
            response.setSuccess(true);
            CreateMainTaskInstanceResponse.Data data = new CreateMainTaskInstanceResponse.Data();
            data.setMainTaskId(unused.getMainTaskId());
            response.setData(data);
            return response;
        }).onFailure(Throwable.class).recoverWithItem(throwable -> {
            log.error("MainTaskInstanceApiV1#create failed, request:{}", request, throwable);
            CreateMainTaskInstanceResponse response = new CreateMainTaskInstanceResponse();
            response.setSuccess(false);
            response.setCode("SERVER_EXCEPTION");
            response.setMessage("create task failed");
            return response;
        });

    }

    @Path("/GetMainTaskInstance")
    @POST
    public Uni<GetMainTaskInstanceResponse> getMainTaskInstance(GetMainTaskInstanceRequest request) {
        if (request == null) {
            throw new WebApplicationException("invalid param", 422);
        }

        TenantAppQuery filter = new TenantAppQuery(request.getTenant(), request.getNamespace(), request.getApp(), config.getEnv());

        return repository.findByMainTaskId(filter, request.getMainTaskId())
                         .onItem().ifNotNull().transform(entity -> {
                    GetMainTaskInstanceResponse response = new GetMainTaskInstanceResponse();
                    response.setSuccess(true);
                    GetMainTaskInstanceResponse.Data data = BeanUtils.cloneProp(entity, GetMainTaskInstanceResponse.Data.class);
                    response.setData(data);
                    return response;
                })
                         .onItem().ifNull().continueWith(() -> {
                    GetMainTaskInstanceResponse response = new GetMainTaskInstanceResponse();
                    response.setSuccess(true);
                    return response;
                })
                         .onFailure(Throwable.class).recoverWithItem(throwable -> {
                    log.error("MainTaskInstanceApiV1#getMainTaskInstance failed, request:{}", request, throwable);
                    GetMainTaskInstanceResponse response = new GetMainTaskInstanceResponse();
                    response.setSuccess(false);
                    response.setCode("SERVER_EXCEPTION");
                    response.setMessage("get main task instance failed");
                    return response;
                });
    }


    @Path("/UpdateMainTaskInstance")
    @POST
    public Uni<UpdateMainTaskInstanceResponse> updateMainTaskInstance(UpdateMainTaskInstanceRequest request) {
        if (request == null) {
            throw new WebApplicationException("invalid param", 422);
        }
        TenantAppQuery filter = new TenantAppQuery(request.getTenant(), request.getNamespace(), request.getApp(), config.getEnv());

        return Panache.withTransaction(() -> repository.findByMainTaskId(filter, request.getMainTaskId())
                                                       .onItem().ifNotNull().invoke(entity -> modifyEntity(request, entity)))
                      .onItem().ifNotNull().transform(entity -> {
                    UpdateMainTaskInstanceResponse response = new UpdateMainTaskInstanceResponse();
                    response.setSuccess(true);
                    return response;
                })
                      .onItem().ifNull().continueWith(() -> {
                    UpdateMainTaskInstanceResponse response = new UpdateMainTaskInstanceResponse();
                    response.setSuccess(false);
                    response.setCode("NOT_FOUND");
                    return response;
                })
                      .onFailure(Throwable.class).recoverWithItem(throwable -> {
                    log.error("MainTaskInstanceApiV1#updateMainTaskInstance failed, request:{}", request, throwable);
                    UpdateMainTaskInstanceResponse response = new UpdateMainTaskInstanceResponse();
                    response.setSuccess(false);
                    response.setCode("SERVER_EXCEPTION");
                    response.setMessage("get main task instance failed, ");
                    return response;
                });

    }

    private static void modifyEntity(UpdateMainTaskInstanceRequest request, MainTaskInstanceEntity entity) {
        if (StringUtils.isNotBlank(request.getBizTaskName())) {
            entity.setBizTaskName(request.getBizTaskName());
        }
        if (StringUtils.isNotBlank(request.getBizKey())) {
            entity.setBizKey(request.getBizKey());
        }
        if (StringUtils.isNotBlank(request.getBizQuery())) {
            entity.setBizQuery(request.getBizQuery());
        }
        if (request.getSubTotalCount() != null) {
            entity.setSubTotalCount(request.getSubTotalCount());
        }
        if (request.getSubFinishedCount() != null) {
            entity.setSubFinishedCount(request.getSubFinishedCount());
        }
        if (request.getSubSuccessCount() != null) {
            entity.setSubSuccessCount(request.getSubSuccessCount());
        }
        if (request.getSubFailedCount() != null) {
            entity.setSubFailedCount(request.getSubFailedCount());
        }
        if (request.getDataTotalCount() != null) {
            entity.setDataTotalCount(request.getDataTotalCount());
        }
        if (request.getDataProcessedCount() != null) {
            entity.setDataProcessedCount(request.getDataProcessedCount());
        }
        if (request.getDataSuccessCount() != null) {
            entity.setDataSuccessCount(request.getDataSuccessCount());
        }
        if (request.getDataFailedCount() != null) {
            entity.setDataFailedCount(request.getDataFailedCount());
        }
        if (request.getGmtStart() != null) {
            entity.setGmtStart(request.getGmtStart());
        }
        if (request.getGmtDispatch() != null) {
            entity.setGmtDispatch(request.getGmtDispatch());
        }
        if (request.getGmtExecute() != null) {
            entity.setGmtExecute(request.getGmtExecute());
        }
        if (request.getGmtFinished() != null) {
            entity.setGmtFinished(request.getGmtFinished());
        }
        if (request.getGmtExpired() != null) {
            entity.setGmtExpired(request.getGmtExpired());
        }
        if (request.getRetryTimes() != null) {
            entity.setRetryTimes(request.getRetryTimes());
        }
        if (request.getResultCode() != null) {
            entity.setResultCode(request.getResultCode());
        }
        if (request.getResultMessage() != null) {
            entity.setResultMessage(request.getResultMessage());
        }
        if (request.getLog() != null) {
            entity.setResultMessage(request.getResultMessage());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        if (JsonUtil.isJson(request.getRuntimeParam())) {
            String runtimeParam = JsonUtil.merge(entity.getRuntimeParam(), request.getRuntimeParam());
            entity.setRuntimeParam(runtimeParam);
        }

        if (JsonUtil.isJson(request.getFeature())) {
            String feature = JsonUtil.merge(entity.getFeature(), request.getFeature());
            entity.setFeature(feature);
        }
        entity.setRowVersion(entity.getRowVersion() + 1);
    }
}
