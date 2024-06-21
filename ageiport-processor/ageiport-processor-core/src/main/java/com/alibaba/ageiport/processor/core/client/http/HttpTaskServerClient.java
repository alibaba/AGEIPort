package com.alibaba.ageiport.processor.core.client.http;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecification;
import com.alibaba.ageiport.processor.core.spi.client.CreateMainTaskRequest;
import com.alibaba.ageiport.processor.core.spi.client.CreateSpecificationRequest;
import com.alibaba.ageiport.processor.core.spi.client.CreateSubTasksRequest;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.task.server.model.*;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lingyi
 */
public class HttpTaskServerClient implements TaskServerClient {

    public static Logger LOGGER = LoggerFactory.getLogger(HttpTaskServerClient.class);

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private AgeiPort ageiPort;
    private String domain;

    private Integer timeoutMs;

    private OkHttpClient client;

    HttpTaskServerClientOptions httpTaskServerClientOptions;

    public HttpTaskServerClient(AgeiPort ageiPort, HttpTaskServerClientOptions options) {
        this.ageiPort = ageiPort;
        this.domain = options.getSchema() + "://" + options.getEndpoint() + ":" + options.getPort();
        this.timeoutMs = options.getTimeoutMs();
        this.client = new OkHttpClient().newBuilder().callTimeout(timeoutMs, TimeUnit.MILLISECONDS).connectTimeout(timeoutMs, TimeUnit.MILLISECONDS).readTimeout(timeoutMs, TimeUnit.MILLISECONDS).writeTimeout(timeoutMs, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true).build();
        this.httpTaskServerClientOptions = options;
    }

    @Override
    public String createMainTask(CreateMainTaskRequest createMainTaskRequest) {
        CreateMainTaskInstanceRequest request = BeanUtils.cloneProp(createMainTaskRequest, CreateMainTaskInstanceRequest.class);
        request.setDomain(domain);
        CreateMainTaskInstanceResponse response = getResponse(request);
        if (response.getSuccess() && response.getData() != null) {
            return response.getData().getMainTaskId();
        }
        throw new IllegalArgumentException("CreateMainTaskInstanceRequest failed:" + response);
    }

    @Override
    public void updateMainTask(MainTask mainTask) {
        UpdateMainTaskInstanceRequest request = BeanUtils.cloneProp(mainTask, UpdateMainTaskInstanceRequest.class);
        request.setDomain(domain);
        UpdateMainTaskInstanceResponse response = getResponse(request);
        if (!response.getSuccess()) {
            throw new IllegalArgumentException("UpdateMainTaskInstanceRequest failed:" + response);
        }
    }

    @Override
    public MainTask getMainTask(String mainTaskId) {
        GetMainTaskInstanceRequest request = new GetMainTaskInstanceRequest();
        request.setMainTaskId(mainTaskId);
        GetMainTaskInstanceResponse response = getResponse(request);
        if (!response.getSuccess()) {
            throw new IllegalArgumentException("GetMainTaskInstanceRequest failed:" + response);
        }
        GetMainTaskInstanceResponse.Data responseData = response.getData();
        MainTask mainTask = BeanUtils.cloneProp(responseData, MainTask.class);
        return mainTask;
    }

    @Override
    public List<String> createSubTask(CreateSubTasksRequest createSubTasksRequest) {
        CreateSubTaskInstancesRequest request = BeanUtils.cloneProp(createSubTasksRequest, CreateSubTaskInstancesRequest.class);
        CreateSubTaskInstancesResponse response = getResponse(request);
        if (!response.getSuccess()) {
            throw new IllegalArgumentException("CreateSubTaskInstancesRequest failed:" + response);
        }
        return response.getData().getSubTaskIds();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        UpdateSubTaskInstanceRequest request = BeanUtils.cloneProp(subTask, UpdateSubTaskInstanceRequest.class);
        UpdateSubTaskInstanceResponse response = getResponse(request);
        if (!response.getSuccess()) {
            throw new IllegalArgumentException("UpdateSubTaskInstanceRequest failed:" + response);
        }
    }

    @Override
    public SubTask getSubTask(String subTaskId) {
        GetSubTaskInstanceRequest request = new GetSubTaskInstanceRequest();
        request.setSubTaskId(subTaskId);
        GetSubTaskInstanceResponse response = getResponse(request);
        if (!response.getSuccess()) {
            throw new IllegalArgumentException("GetSubTaskInstanceRequest failed:" + response);
        }
        GetSubTaskInstanceResponse.Data responseData = response.getData();
        SubTask subTask = BeanUtils.cloneProp(responseData, SubTask.class);
        return subTask;
    }

    @Override
    public TaskSpecification getTaskSpecification(String taskCode) {
        GetTaskSpecificationRequest request = new GetTaskSpecificationRequest();
        request.setTaskCode(taskCode);
        GetTaskSpecificationResponse response = getResponse(request);
        if (!response.getSuccess()) {
            throw new IllegalArgumentException("GetSubTaskInstanceRequest failed:" + response);
        }
        GetTaskSpecificationResponse.Data responseData = response.getData();
        TaskSpecification taskSpecification = BeanUtils.cloneProp(responseData, TaskSpecification.class);
        return taskSpecification;
    }

    @Override
    public String createTaskSpecification(CreateSpecificationRequest createSpecificationRequest) {
        CreateTaskSpecificationRequest request = BeanUtils.cloneProp(createSpecificationRequest, CreateTaskSpecificationRequest.class);
        CreateTaskSpecificationResponse response = getResponse(request);
        if (!response.getSuccess()) {
            throw new IllegalArgumentException("CreateTaskSpecificationRequest failed:" + response);
        }
        return response.getData().getId().toString();
    }

    public <T extends com.alibaba.ageiport.sdk.core.Response> T getResponse(com.alibaba.ageiport.sdk.core.Request<T> request) {
        AgeiPortOptions options = ageiPort.getOptions();

        request.setAccessKeyId(options.getAccessKeyId());
        request.setApp(options.getApp());
        request.setNamespace(options.getNamespace());
        request.setDomain(domain);

        String json = JsonUtil.toJsonString(request);
        RequestBody body = RequestBody.create(json, JSON);

        String requestDomain = request.getDomain();
        String requestVersion = request.getVersion();
        String requestAction = request.getAction();

        String url = requestDomain + "/" + requestVersion + "/" + requestAction;
        Request httpRequest = new Request.Builder().url(url).post(body).build();
        try (Response httpResponse = client.newCall(httpRequest).execute()) {
            ResponseBody responseBody = httpResponse.body();
            String bodyAsString = responseBody.string();
            return JsonUtil.toObject(bodyAsString, request.getResponseClass());
        } catch (Throwable e) {
            LOGGER.error("http request failed, but will retry:{} times", httpTaskServerClientOptions.getRetryTimes(), e);

            int retryTimes = 1;
            while (retryTimes <= httpTaskServerClientOptions.getRetryTimes()) {
                try {
                    String retryResult = retryRequest(httpRequest);
                    LOGGER.info("http request failed, but retry success, already retry {} times", retryTimes);
                    return JsonUtil.toObject(retryResult, request.getResponseClass());
                } catch (Throwable retryException) {
                    LOGGER.error("http request retry failed, already retry {} times", retryTimes, retryException);
                }
                retryTimes++;
            }
            throw new IllegalStateException(e);
        }
    }

    public String retryRequest(Request httpRequest) throws IOException {
        try (Response httpResponse = client.newCall(httpRequest).execute()) {
            ResponseBody responseBody = httpResponse.body();
            String bodyAsString = responseBody.string();
            return bodyAsString;
        } catch (Throwable e) {
            LOGGER.error("http request failed, ", e);
            throw new IllegalStateException(e);
        }
    }

}
