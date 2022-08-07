package com.alibaba.ageiport.processor.core.test.processor.importer;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteResult;
import com.alibaba.ageiport.processor.core.test.model.Query;
import com.alibaba.ageiport.processor.core.test.model.View;
import com.alibaba.ageiport.processor.core.test.TestHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
public class StandaloneImportProcessorTest {

    @SneakyThrows
    @Test
    public void test() {
        AgeiPortOptions options = new AgeiPortOptions();
        AgeiPortOptions.Debug debug = new AgeiPortOptions.Debug();
        options.setDebug(debug);
        AgeiPort ageiPort = AgeiPort.ageiPort(options);

        String taskCode = StandaloneImportProcessor.class.getSimpleName();
        TestHelper testHelper = new TestHelper(ageiPort);
        String filePath = testHelper.file(taskCode + ".xlsx");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
        String fileKey = UUID.randomUUID().toString();
        ageiPort.getFileStore().save(fileKey, inputStream, new HashMap<>());

        TaskExecuteParam request = new TaskExecuteParam();
        Query query = new Query();
        query.setTotalCount(100);
        request.setTaskSpecificationCode(taskCode);
        request.setBizUserId("userId");
        request.setBizQuery(JsonUtil.toJsonString(query));
        request.setInputFileKey(fileKey);
        TaskExecuteResult response = ageiPort.getTaskService().executeTask(request);

        Assertions.assertTrue(response.getSuccess());
        testHelper.assertWithoutFile(response.getMainTaskId());
    }

    @SneakyThrows
    @Test
    public void testHasCheckError() {
        AgeiPortOptions options = new AgeiPortOptions();
        AgeiPortOptions.Debug debug = new AgeiPortOptions.Debug();
        options.setDebug(debug);
        AgeiPort ageiPort = AgeiPort.ageiPort(options);

        String taskCode = StandaloneImportProcessor.class.getSimpleName();
        TestHelper testHelper = new TestHelper(ageiPort);
        String filePath = testHelper.file(taskCode + ".xlsx");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
        String fileKey = UUID.randomUUID().toString();
        ageiPort.getFileStore().save(fileKey, inputStream, new HashMap<>());

        TaskExecuteParam request = new TaskExecuteParam();
        Query query = new Query();

        View view = new View();
        view.setId(1);
        view.setName("name1");

        query.setCheckErrorData(Lists.newArrayList(view));
        query.setTotalCount(100);
        request.setTaskSpecificationCode(taskCode);
        request.setBizUserId("userId");
        request.setBizQuery(JsonUtil.toJsonString(query));
        request.setInputFileKey(fileKey);
        TaskExecuteResult response = ageiPort.getTaskService().executeTask(request);

        Assertions.assertTrue(response.getSuccess());
        testHelper.assertWithFile(response.getMainTaskId(), query.getErrorCount());
    }
}
