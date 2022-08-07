package com.alibaba.ageiport.task.server.http;

import com.alibaba.ageiport.task.server.mock.MockData;
import com.alibaba.ageiport.task.server.model.CreateTaskSpecificationRequest;
import com.alibaba.ageiport.task.server.model.CreateTaskSpecificationResponse;
import com.alibaba.ageiport.task.server.model.GetTaskSpecificationRequest;
import com.alibaba.ageiport.task.server.model.GetTaskSpecificationResponse;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@Slf4j
@QuarkusTest
public class TaskSpecificationApiV1Test {

    @Test
    public void testCreateMainTaskInstance() {
        CreateTaskSpecificationRequest createTaskSpecificationRequest = MockData.createTaskSpecificationRequest();
        CreateTaskSpecificationResponse createTaskSpecificationResponse = given()
                .when()
                .body(createTaskSpecificationRequest)
                .contentType("application/json")
                .post(createTaskSpecificationRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateTaskSpecificationResponse.class);
        Assertions.assertThat(createTaskSpecificationResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(createTaskSpecificationResponse.getData()).isNotNull();
        Assertions.assertThat(createTaskSpecificationResponse.getData().getId()).isNotNull();

        CreateTaskSpecificationRequest createTaskSpecificationRequest2 = MockData.createTaskSpecificationRequest();
        createTaskSpecificationRequest2.setTaskCode(UUID.randomUUID() + UUID.randomUUID().toString());
        CreateTaskSpecificationResponse createTaskSpecificationResponse2 = given()
                .when()
                .body(createTaskSpecificationRequest2)
                .contentType("application/json")
                .post(createTaskSpecificationRequest2.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateTaskSpecificationResponse.class);
        Assertions.assertThat(createTaskSpecificationResponse2.getSuccess()).isEqualTo(false);
    }

    @Test
    public void testGetSubTaskInstance() {
        CreateTaskSpecificationRequest createTaskSpecificationRequest = MockData.createTaskSpecificationRequest();
        CreateTaskSpecificationResponse createTaskSpecificationResponse = given()
                .when()
                .body(createTaskSpecificationRequest)
                .contentType("application/json")
                .post(createTaskSpecificationRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(CreateTaskSpecificationResponse.class);
        Assertions.assertThat(createTaskSpecificationResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(createTaskSpecificationResponse.getData()).isNotNull();
        Assertions.assertThat(createTaskSpecificationResponse.getData().getId()).isNotNull();

        GetTaskSpecificationRequest getTaskSpecificationRequest = new GetTaskSpecificationRequest();
        getTaskSpecificationRequest.setTenant(createTaskSpecificationRequest.getTenant());
        getTaskSpecificationRequest.setNamespace(createTaskSpecificationRequest.getNamespace());
        getTaskSpecificationRequest.setApp(createTaskSpecificationRequest.getApp());
        getTaskSpecificationRequest.setTaskCode(createTaskSpecificationRequest.getTaskCode());
        GetTaskSpecificationResponse getTaskSpecificationResponse = given()
                .when()
                .body(createTaskSpecificationRequest)
                .contentType("application/json")
                .post(getTaskSpecificationRequest.getUrl())
                .then()
                .extract()
                .body()
                .as(GetTaskSpecificationResponse.class);
        Assertions.assertThat(getTaskSpecificationResponse.getSuccess()).isEqualTo(true);
        Assertions.assertThat(getTaskSpecificationResponse.getData()).isNotNull();
        Assertions.assertThat(getTaskSpecificationResponse.getData().getTaskHandler()).isNotNull();
    }

}
