package com.alibaba.ageiport.task.server.http;

import com.alibaba.ageiport.task.server.mock.MockData;
import com.alibaba.ageiport.task.server.model.*;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static io.restassured.RestAssured.given;

@Slf4j
@QuarkusTest
public class MainTaskInstanceApiV1Test {

    @Test
    public void testCreateMainTaskInstance() {
        CreateMainTaskInstanceRequest request = MockData.createMainTaskInstanceRequest();
        final CreateMainTaskInstanceResponse response = given()
                .when()
                .body(request)
                .contentType("application/json")
                .post(request.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateMainTaskInstanceResponse.class);
        Assertions.assertThat(response.getSuccess()).isEqualTo(true);
        Assertions.assertThat(response.getData()).isNotNull();
        Assertions.assertThat(response.getData().getMainTaskId()).isNotNull();

        CreateMainTaskInstanceRequest request2 = MockData.createMainTaskInstanceRequest();
        request2.setApp(UUID.randomUUID() + UUID.randomUUID().toString());
        CreateMainTaskInstanceResponse response2 = given()
                .when()
                .body(request2)
                .contentType("application/json")
                .post(request2.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateMainTaskInstanceResponse.class);
        Assertions.assertThat(response2.getSuccess()).isEqualTo(false);
    }

    @Test
    public void testGetMainTaskInstance() {
        CreateMainTaskInstanceRequest createMainTaskInstanceRequest = MockData.createMainTaskInstanceRequest();
        CreateMainTaskInstanceResponse createMainTaskInstanceResponse = given()
                .when()
                .body(createMainTaskInstanceRequest)
                .contentType("application/json")
                .post(createMainTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(createMainTaskInstanceRequest.getResponseClass());
        Assertions.assertThat(createMainTaskInstanceResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(createMainTaskInstanceResponse.getData()).isNotNull();
        String mainTaskId = createMainTaskInstanceResponse.getData().getMainTaskId();
        Assertions.assertThat(mainTaskId).isNotNull();

        GetMainTaskInstanceRequest getMainTaskInstanceRequest = new GetMainTaskInstanceRequest();
        getMainTaskInstanceRequest.setTenant(createMainTaskInstanceRequest.getTenant());
        getMainTaskInstanceRequest.setNamespace(createMainTaskInstanceRequest.getNamespace());
        getMainTaskInstanceRequest.setApp(createMainTaskInstanceRequest.getApp());
        getMainTaskInstanceRequest.setMainTaskId(mainTaskId);


        GetMainTaskInstanceResponse getMainTaskInstanceResponse = given()
                .when()
                .body(getMainTaskInstanceRequest)
                .contentType("application/json")
                .post(getMainTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body().as(GetMainTaskInstanceResponse.class);
        Assertions.assertThat(getMainTaskInstanceResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(getMainTaskInstanceResponse.getData()).isNotNull();
        Assertions.assertThat(getMainTaskInstanceResponse.getData().getMainTaskId()).isNotNull();
        Assertions.assertThat(getMainTaskInstanceResponse.getData().getMainTaskId()).isEqualTo(mainTaskId);

        GetMainTaskInstanceRequest getMainTaskInstanceRequest2 = new GetMainTaskInstanceRequest();
        getMainTaskInstanceRequest2.setTenant(createMainTaskInstanceRequest.getTenant());
        getMainTaskInstanceRequest2.setNamespace(createMainTaskInstanceRequest.getNamespace());
        getMainTaskInstanceRequest2.setApp(createMainTaskInstanceRequest.getApp());
        getMainTaskInstanceRequest2.setMainTaskId(mainTaskId + "2");
        GetMainTaskInstanceResponse getMainTaskInstanceResponse2 = given()
                .when()
                .body(getMainTaskInstanceRequest2)
                .contentType("application/json")
                .post(getMainTaskInstanceRequest2.getUrl())
                .then()
                .extract()
                .body().as(GetMainTaskInstanceResponse.class);
        Assertions.assertThat(getMainTaskInstanceResponse2.getSuccess()).isEqualTo(true);
        Assertions.assertThat(getMainTaskInstanceResponse2.getData()).isNull();
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
        String mainTaskId = createMainTaskInstanceResponse.getData().getMainTaskId();
        Assertions.assertThat(mainTaskId).isNotNull();

        UpdateMainTaskInstanceRequest updateMainTaskInstanceRequest = new UpdateMainTaskInstanceRequest();
        updateMainTaskInstanceRequest.setTenant(createMainTaskInstanceRequest.getTenant());
        updateMainTaskInstanceRequest.setNamespace(createMainTaskInstanceRequest.getNamespace());
        updateMainTaskInstanceRequest.setApp(createMainTaskInstanceRequest.getApp());
        updateMainTaskInstanceRequest.setMainTaskId(mainTaskId);
        String bizKey = "bizKey";
        updateMainTaskInstanceRequest.setBizKey(bizKey);
        Date gmtStart = new Date();
        updateMainTaskInstanceRequest.setGmtStart(gmtStart);

        UpdateMainTaskInstanceResponse updateMainTaskInstanceResponse = given()
                .when()
                .body(updateMainTaskInstanceRequest)
                .contentType("application/json")
                .post(updateMainTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body().as(UpdateMainTaskInstanceResponse.class);
        Assertions.assertThat(updateMainTaskInstanceResponse.getSuccess()).isEqualTo(true);

        GetMainTaskInstanceRequest getMainTaskInstanceRequest = new GetMainTaskInstanceRequest();
        getMainTaskInstanceRequest.setTenant(createMainTaskInstanceRequest.getTenant());
        getMainTaskInstanceRequest.setNamespace(createMainTaskInstanceRequest.getNamespace());
        getMainTaskInstanceRequest.setApp(createMainTaskInstanceRequest.getApp());
        getMainTaskInstanceRequest.setMainTaskId(mainTaskId);
        GetMainTaskInstanceResponse getMainTaskInstanceResponse = given()
                .when()
                .body(getMainTaskInstanceRequest)
                .contentType("application/json")
                .post(getMainTaskInstanceRequest.getUrl())
                .then()
                .extract()
                .body().as(GetMainTaskInstanceResponse.class);
        Assertions.assertThat(getMainTaskInstanceResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(getMainTaskInstanceResponse.getData()).isNotNull();
        Assertions.assertThat(getMainTaskInstanceResponse.getData().getMainTaskId()).isNotNull();
        Assertions.assertThat(getMainTaskInstanceResponse.getData().getMainTaskId()).isEqualTo(mainTaskId);
        Assertions.assertThat(getMainTaskInstanceResponse.getData().getBizKey()).isEqualTo(bizKey);
        Assertions.assertThat(getMainTaskInstanceResponse.getData().getGmtStart()).isEqualTo(gmtStart);
    }
}
