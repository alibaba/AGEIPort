package com.alibaba.ageiport.task.server.http;

import com.alibaba.ageiport.common.constants.RowStatus;
import com.alibaba.ageiport.common.constants.SubTaskStatus;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.TaskIdUtil;
import com.alibaba.ageiport.task.server.config.TaskServerConfig;
import com.alibaba.ageiport.task.server.entity.SubTaskInstanceEntity;
import com.alibaba.ageiport.task.server.oauth.Oauth;
import com.alibaba.ageiport.task.server.repository.MainTaskInstanceRepository;
import com.alibaba.ageiport.task.server.repository.SubTaskInstanceRepository;
import com.alibaba.ageiport.task.server.repository.query.TenantAppQuery;
import com.alibaba.ageiport.task.server.model.*;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lingyi
 */
@Slf4j
@ApplicationScoped
@Path("/v1")
@Produces("application/json")
@Consumes("application/json")
@Oauth
public class SubTaskInstanceApiV1 {

    private TaskServerConfig config;

    private SubTaskInstanceRepository repository;

    private MainTaskInstanceRepository mainTaskInstanceRepository;

    public SubTaskInstanceApiV1(TaskServerConfig config,
                                SubTaskInstanceRepository repository,
                                MainTaskInstanceRepository mainTaskInstanceRepository) {
        this.config = config;
        this.repository = repository;
        this.mainTaskInstanceRepository = mainTaskInstanceRepository;
    }

    @Path("/CreateSubTaskInstances")
    @POST
    public Uni<CreateSubTaskInstancesResponse> createMainTaskInstances(CreateSubTaskInstancesRequest request) {
        if (request == null) {
            throw new WebApplicationException("invalid param", 422);
        }
        TenantAppQuery query = new TenantAppQuery(request.getTenant(), request.getNamespace(), request.getApp(), config.getEnv());

        String mainTaskId = request.getMainTaskId();
        List<String> subTaskIds = new ArrayList<>();

        return Panache.withTransaction(() -> mainTaskInstanceRepository.findByMainTaskId(query, mainTaskId)
                        .onItem().ifNotNull().call(entity -> {
                            List<CreateSubTaskInstancesRequest.SubTaskInstance> subTaskInstances = request.getSubTaskInstances();
                            List<SubTaskInstanceEntity> entities = new ArrayList<>(subTaskInstances.size());
                            for (CreateSubTaskInstancesRequest.SubTaskInstance subTaskInstance : subTaskInstances) {
                                Integer subTaskNo = subTaskInstance.getSubTaskNo();

                                String subTaskId = TaskIdUtil.genSubTaskId(mainTaskId, subTaskNo);
                                subTaskIds.add(subTaskId);

                                SubTaskInstanceEntity subEntity = BeanUtils.cloneProp(entity, SubTaskInstanceEntity.class);
                                subEntity.setId(null);
                                subEntity.setMainTaskId(mainTaskId);
                                subEntity.setSubTaskId(subTaskId);
                                subEntity.setSubTaskNo(subTaskNo);
                                subEntity.setTenant(request.getTenant());
                                subEntity.setNamespace(request.getNamespace());
                                subEntity.setRowStatus(RowStatus.VALID);
                                subEntity.setRowVersion(1);
                                subEntity.setStatus(SubTaskStatus.NEW.getCode());
                                subEntity.setApp(request.getApp());
                                subEntity.setEnv(config.getEnv());
                                subEntity.setBizQuery(subTaskInstance.getBizQuery());
                                subEntity.setRuntimeParam(subTaskInstance.getRuntimeParam());

                                entities.add(subEntity);
                            }
                            return repository.persist(entities);
                        })
                )
                .onItem().ifNotNull().transform(pr -> {
                    CreateSubTaskInstancesResponse response = new CreateSubTaskInstancesResponse();
                    CreateSubTaskInstancesResponse.Data data = new CreateSubTaskInstancesResponse.Data();
                    data.setSubTaskIds(subTaskIds);
                    response.setSuccess(true);
                    response.setData(data);
                    return response;
                })
                .onItem().ifNull().continueWith(() -> {
                    CreateSubTaskInstancesResponse response = new CreateSubTaskInstancesResponse();
                    response.setSuccess(false);
                    response.setCode("NOT_FOUND");
                    return response;
                }).onFailure(Throwable.class).recoverWithItem(throwable -> {
                    log.error("SubTaskInstanceApiV1#createMainTaskInstances failed, request:{}", request, throwable);
                    CreateSubTaskInstancesResponse response = new CreateSubTaskInstancesResponse();
                    response.setSuccess(false);
                    response.setCode("SERVER_EXCEPTION");
                    response.setMessage("get main task instance failed, ");
                    return response;
                });
    }

    @Path("/GetSubTaskInstance")
    @POST
    public Uni<GetSubTaskInstanceResponse> getSubTaskInstance(GetSubTaskInstanceRequest request) {
        if (request == null) {
            throw new WebApplicationException("invalid param", 422);
        }

        TenantAppQuery filter = new TenantAppQuery(request.getTenant(), request.getNamespace(), request.getApp(), config.getEnv());

        return repository.findBySubTaskId(filter, request.getSubTaskId())
                .onItem().ifNotNull().transform(entity -> {
                    GetSubTaskInstanceResponse response = new GetSubTaskInstanceResponse();
                    response.setSuccess(true);
                    GetSubTaskInstanceResponse.Data data = BeanUtils.cloneProp(entity, GetSubTaskInstanceResponse.Data.class);
                    response.setData(data);
                    return response;
                })
                .onItem().ifNull().continueWith(() -> {
                    GetSubTaskInstanceResponse response = new GetSubTaskInstanceResponse();
                    response.setSuccess(true);
                    return response;
                })
                .onFailure(Throwable.class).recoverWithItem(throwable -> {
                    log.error("MainTaskInstanceApiV1#getMainTaskInstance failed, request:{}", request, throwable);
                    GetSubTaskInstanceResponse response = new GetSubTaskInstanceResponse();
                    response.setSuccess(false);
                    response.setCode("SERVER_EXCEPTION");
                    response.setMessage("get sub task instance failed");
                    return response;
                });
    }


    @Path("/UpdateSubTaskInstance")
    @POST
    public Uni<UpdateSubTaskInstanceResponse> updateMainTaskInstance(UpdateSubTaskInstanceRequest request) {
        if (request == null) {
            throw new WebApplicationException("invalid param", 422);
        }
        TenantAppQuery query = new TenantAppQuery(request.getTenant(), request.getNamespace(), request.getApp(), config.getEnv());

        return Panache.withTransaction(() -> repository.findBySubTaskId(query, request.getSubTaskId())
                        .onItem().ifNotNull().invoke(entity -> modifyEntity(request, entity)))
                .onItem().ifNotNull().transform(entity -> {
                    UpdateSubTaskInstanceResponse response = new UpdateSubTaskInstanceResponse();
                    response.setSuccess(true);
                    return response;
                })
                .onItem().ifNull().continueWith(() -> {
                    UpdateSubTaskInstanceResponse response = new UpdateSubTaskInstanceResponse();
                    response.setSuccess(false);
                    response.setCode("NOT_FOUND");
                    return response;
                })
                .onFailure(Throwable.class).recoverWithItem(throwable -> {
                    log.error("SubTaskInstanceApiV1#updateMainTaskInstance failed, request:{}", request, throwable);
                    UpdateSubTaskInstanceResponse response = new UpdateSubTaskInstanceResponse();
                    response.setSuccess(false);
                    response.setCode("SERVER_EXCEPTION");
                    response.setMessage("get sub task instance failed");
                    return response;
                });

    }

    private static void modifyEntity(UpdateSubTaskInstanceRequest request, SubTaskInstanceEntity entity) {
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

        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getLog() != null) {
            entity.setLog(request.getLog());
        }
        if (request.getHost() != null) {
            entity.setHost(request.getHost());
        }
        if (request.getTraceId() != null) {
            entity.setTraceId(request.getTraceId());
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
