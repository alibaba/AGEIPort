package com.alibaba.ageiport.task.server.http;

import com.alibaba.ageiport.task.server.mock.MockData;
import com.alibaba.ageiport.task.server.model.*;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;

@Slf4j
@QuarkusTest
public class SubTaskInstanceApiV1Test {
    @Test
    public void testCreateMainTaskInstance() {
        CreateMainTaskInstanceRequest createMainTaskInstanceRequest = MockData.createMainTaskInstanceRequest();
        CreateMainTaskInstanceResponse createMainTaskInstanceResponse = given()
                .when()
                .body(createMainTaskInstanceRequest)
                .contentType("application/json")
                .post(createMainTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateMainTaskInstanceResponse.class);
        Assertions.assertThat(createMainTaskInstanceResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(createMainTaskInstanceResponse.getData()).isNotNull();
        Assertions.assertThat(createMainTaskInstanceResponse.getData().getMainTaskId()).isNotNull();

        String mainTaskId = createMainTaskInstanceResponse.getData().getMainTaskId();
        CreateSubTaskInstancesRequest createSubTaskInstancesRequest = MockData.createSubTaskInstanceRequest(mainTaskId);
        CreateSubTaskInstancesResponse createSubTaskInstancesResponse = given()
                .when()
                .body(createSubTaskInstancesRequest)
                .contentType("application/json")
                .post(createSubTaskInstancesRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateSubTaskInstancesResponse.class);
        Assertions.assertThat(createSubTaskInstancesResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(createSubTaskInstancesResponse.getData()).isNotNull();
        Assertions.assertThat(createSubTaskInstancesResponse.getData().getSubTaskIds()).isNotEmpty();


        CreateSubTaskInstancesRequest createSubTaskInstancesRequest2 = MockData.createSubTaskInstanceRequest(mainTaskId);
        createSubTaskInstancesRequest2.setMainTaskId("uuid");
        CreateSubTaskInstancesResponse createSubTaskInstancesResponse2 = given()
                .when()
                .body(createSubTaskInstancesRequest2)
                .contentType("application/json")
                .post(createSubTaskInstancesRequest2.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateSubTaskInstancesResponse.class);
        Assertions.assertThat(createSubTaskInstancesResponse2.getSuccess()).isEqualTo(false);
    }

    @Test
    public void testGetSubTaskInstance() {
        CreateMainTaskInstanceRequest createMainTaskInstanceRequest = MockData.createMainTaskInstanceRequest();
        CreateMainTaskInstanceResponse createMainTaskInstanceResponse = given()
                .when()
                .body(createMainTaskInstanceRequest)
                .contentType("application/json")
                .post(createMainTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateMainTaskInstanceResponse.class);
        Assertions.assertThat(createMainTaskInstanceResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(createMainTaskInstanceResponse.getData()).isNotNull();
        Assertions.assertThat(createMainTaskInstanceResponse.getData().getMainTaskId()).isNotNull();

        String mainTaskId = createMainTaskInstanceResponse.getData().getMainTaskId();
        CreateSubTaskInstancesRequest createSubTaskInstancesRequest = MockData.createSubTaskInstanceRequest(mainTaskId);
        CreateSubTaskInstancesResponse createSubTaskInstancesResponse = given()
                .when()
                .body(createSubTaskInstancesRequest)
                .contentType("application/json")
                .post(createSubTaskInstancesRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateSubTaskInstancesResponse.class);
        Assertions.assertThat(createSubTaskInstancesResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(createSubTaskInstancesResponse.getData()).isNotNull();
        Assertions.assertThat(createSubTaskInstancesResponse.getData().getSubTaskIds()).isNotEmpty();

        List<String> subTaskIds = createSubTaskInstancesResponse.getData().getSubTaskIds();
        String subTaskId = subTaskIds.get(0);

        GetSubTaskInstanceRequest getSubTaskInstanceRequest = new GetSubTaskInstanceRequest();
        getSubTaskInstanceRequest.setTenant(createMainTaskInstanceRequest.getTenant());
        getSubTaskInstanceRequest.setNamespace(createMainTaskInstanceRequest.getNamespace());
        getSubTaskInstanceRequest.setApp(createMainTaskInstanceRequest.getApp());
        getSubTaskInstanceRequest.setSubTaskId(subTaskId);
        GetSubTaskInstanceResponse getSubTaskInstanceResponse = given()
                .when()
                .body(getSubTaskInstanceRequest)
                .contentType("application/json")
                .post(getSubTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(GetSubTaskInstanceResponse.class);
        Assertions.assertThat(getSubTaskInstanceResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(getSubTaskInstanceResponse.getData()).isNotNull();
        Assertions.assertThat(getSubTaskInstanceResponse.getData().getMainTaskId()).isEqualTo(mainTaskId);
        Assertions.assertThat(getSubTaskInstanceResponse.getData().getSubTaskId()).isEqualTo(subTaskId);
    }

    @Test
    public void testUpdateMainTaskInstance() {
        CreateMainTaskInstanceRequest createMainTaskInstanceRequest = MockData.createMainTaskInstanceRequest();
        CreateMainTaskInstanceResponse createMainTaskInstanceResponse = given()
                .when()
                .body(createMainTaskInstanceRequest)
                .contentType("application/json")
                .post(createMainTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateMainTaskInstanceResponse.class);
        Assertions.assertThat(createMainTaskInstanceResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(createMainTaskInstanceResponse.getData()).isNotNull();
        Assertions.assertThat(createMainTaskInstanceResponse.getData().getMainTaskId()).isNotNull();

        String mainTaskId = createMainTaskInstanceResponse.getData().getMainTaskId();
        CreateSubTaskInstancesRequest createSubTaskInstancesRequest = MockData.createSubTaskInstanceRequest(mainTaskId);
        CreateSubTaskInstancesResponse createSubTaskInstancesResponse = given()
                .when()
                .body(createSubTaskInstancesRequest)
                .contentType("application/json")
                .post(createSubTaskInstancesRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateSubTaskInstancesResponse.class);
        Assertions.assertThat(createSubTaskInstancesResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(createSubTaskInstancesResponse.getData()).isNotNull();
        Assertions.assertThat(createSubTaskInstancesResponse.getData().getSubTaskIds()).isNotEmpty();

        List<String> subTaskIds = createSubTaskInstancesResponse.getData().getSubTaskIds();
        String subTaskId = subTaskIds.get(0);


        UpdateSubTaskInstanceRequest updateSubTaskInstanceRequest = new UpdateSubTaskInstanceRequest();
        updateSubTaskInstanceRequest.setTenant(createMainTaskInstanceRequest.getTenant());
        updateSubTaskInstanceRequest.setNamespace(createMainTaskInstanceRequest.getNamespace());
        updateSubTaskInstanceRequest.setApp(createMainTaskInstanceRequest.getApp());
        updateSubTaskInstanceRequest.setSubTaskId(subTaskId);
        int dataFailedCount = 3;
        updateSubTaskInstanceRequest.setDataFailedCount(dataFailedCount);
        Date gmtStart = new Date();
        updateSubTaskInstanceRequest.setGmtStart(gmtStart);
        String log = "log";
        updateSubTaskInstanceRequest.setLog(log);
        String traceId = "traceId";
        updateSubTaskInstanceRequest.setTraceId(traceId);
        UpdateSubTaskInstanceResponse updateSubTaskInstanceResponse = given()
                .when()
                .body(updateSubTaskInstanceRequest)
                .contentType("application/json")
                .post(updateSubTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(UpdateSubTaskInstanceResponse.class);
        Assertions.assertThat(updateSubTaskInstanceResponse.getSuccess()).isEqualTo(true);


        GetSubTaskInstanceRequest getSubTaskInstanceRequest = new GetSubTaskInstanceRequest();
        getSubTaskInstanceRequest.setTenant(createMainTaskInstanceRequest.getTenant());
        getSubTaskInstanceRequest.setNamespace(createMainTaskInstanceRequest.getNamespace());
        getSubTaskInstanceRequest.setApp(createMainTaskInstanceRequest.getApp());
        getSubTaskInstanceRequest.setSubTaskId(subTaskId);
        GetSubTaskInstanceResponse getSubTaskInstanceResponse = given()
                .when()
                .body(getSubTaskInstanceRequest)
                .contentType("application/json")
                .post(getSubTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(GetSubTaskInstanceResponse.class);
        Assertions.assertThat(getSubTaskInstanceResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(getSubTaskInstanceResponse.getData()).isNotNull();
        Assertions.assertThat(getSubTaskInstanceResponse.getData().getMainTaskId()).isEqualTo(mainTaskId);
        Assertions.assertThat(getSubTaskInstanceResponse.getData().getSubTaskId()).isEqualTo(subTaskId);
        Assertions.assertThat(getSubTaskInstanceResponse.getData().getDataFailedCount()).isEqualTo(dataFailedCount);
        Assertions.assertThat(getSubTaskInstanceResponse.getData().getGmtStart()).isEqualTo(gmtStart);
        Assertions.assertThat(getSubTaskInstanceResponse.getData().getLog()).isEqualTo(log);
        Assertions.assertThat(getSubTaskInstanceResponse.getData().getTraceId()).isEqualTo(traceId);
    }
}
