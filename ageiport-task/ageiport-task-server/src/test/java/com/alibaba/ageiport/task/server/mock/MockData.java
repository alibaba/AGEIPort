package com.alibaba.ageiport.task.server.mock;

import com.alibaba.ageiport.task.server.model.CreateMainTaskInstanceRequest;
import com.alibaba.ageiport.task.server.model.CreateSubTaskInstancesRequest;
import com.alibaba.ageiport.task.server.model.CreateTaskSpecificationRequest;
import com.alibaba.fastjson.JSONObject;
import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockData {

    public static final String SUCCESS_RESPONSE = "\"success\":true";
    public static final String FAILED_RESPONSE = "\"success\":false";

    public static final String TENANT_CODE = "TenantTest";
    public static final String NAMESPACE = "com.alibaba";
    public static final String APP = "app";

    public static final String DESCRIPTION = "description";
    public static final String TASK_CODE = "TaskCode";
    public static final String TASK_NAME = "TaskName";
    public static final String TASK_TYPE = "EXPORT";

    public static final String EXECUTE_TYPE = "SHARDING";

    public static final String HOST = "127.0.0.1";

    public static final String HANDLER = "handler";

    public static CreateTaskSpecificationRequest createTaskSpecificationRequest() {
        MockConfig mockConfig = MockConfig.newInstance().excludes("serialVersionUID");

        CreateTaskSpecificationRequest request = JMockData.mock(CreateTaskSpecificationRequest.class, mockConfig);
        request.setTenant(TENANT_CODE);
        request.setNamespace(NAMESPACE);
        request.setApp(APP);
        request.setTaskCode(TASK_CODE);
        request.setTaskType(TASK_TYPE);
        request.setTaskExecuteType(EXECUTE_TYPE);
        request.setTaskHandler(HANDLER);
        return request;
    }

    public static CreateMainTaskInstanceRequest createMainTaskInstanceRequest() {
        MockConfig mockConfig = MockConfig.newInstance().excludes("serialVersionUID");
        CreateMainTaskInstanceRequest request = JMockData.mock(CreateMainTaskInstanceRequest.class, mockConfig);

        request.setTenant(TENANT_CODE);
        request.setNamespace(NAMESPACE);
        request.setApp(APP);
        request.setCode(TASK_CODE);
        request.setName(TASK_NAME);
        request.setType(TASK_TYPE);
        request.setExecuteType(EXECUTE_TYPE);
        request.setTraceId(UUID.randomUUID().toString());
        request.setHost(HOST);
        request.setRuntimeParam(new JSONObject().toJSONString());
        request.setBizQuery(new JSONObject().toJSONString());

        return request;
    }

    public static CreateSubTaskInstancesRequest createSubTaskInstanceRequest(String mainTaskId) {
        MockConfig mockConfig = MockConfig.newInstance().excludes("serialVersionUID");
        CreateSubTaskInstancesRequest request = JMockData.mock(CreateSubTaskInstancesRequest.class,mockConfig);

        request.setTenant(TENANT_CODE);
        request.setNamespace(NAMESPACE);
        request.setApp(APP);
        request.setMainTaskId(mainTaskId);

        List<CreateSubTaskInstancesRequest.SubTaskInstance> subTaskInstances = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CreateSubTaskInstancesRequest.SubTaskInstance subTaskInstance = new CreateSubTaskInstancesRequest.SubTaskInstance();
            subTaskInstance.setRuntimeParam(new JSONObject().toJSONString());
            subTaskInstance.setBizQuery(new JSONObject().toJSONString());
            subTaskInstances.add(subTaskInstance);
        }
        request.setSubTaskInstances(subTaskInstances);
        return request;
    }
}