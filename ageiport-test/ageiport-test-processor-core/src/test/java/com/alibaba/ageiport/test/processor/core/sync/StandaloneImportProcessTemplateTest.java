package com.alibaba.ageiport.test.processor.core.sync;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.spi.service.SyncExtensionApiParam;
import com.alibaba.ageiport.processor.core.spi.service.SyncExtensionApiResult;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.test.processor.core.TestHelper;
import com.alibaba.ageiport.test.processor.core.importer.ClusterImportProcessor;
import com.alibaba.ageiport.test.processor.core.model.Query;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
public class StandaloneImportProcessTemplateTest {

    @SneakyThrows
    @Test
    public void test() {
        AgeiPortOptions options = AgeiPortOptions.debug();
        AgeiPort ageiPort = AgeiPort.ageiPort(options);

        String taskCode = ClusterImportProcessor.class.getSimpleName();
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

        SyncExtensionApiParam param = new SyncExtensionApiParam();
        param.setSyncExtensionApiCode("ImportTemplateSyncExtension");
        param.setSyncExtensionApiParam(JsonUtil.toJsonString(request));
        SyncExtensionApiResult result = ageiPort.getTaskService().executeSyncExtension(param);

        log.info("result:{}", JsonUtil.toJsonString(result));

        Assertions.assertTrue(result.getSuccess());
        Assertions.assertNotNull(result.getSyncExtensionApiResult());
    }
}
