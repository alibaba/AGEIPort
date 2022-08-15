package com.alibaba.ageiport.test.processor.core.exporter;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteResult;
import com.alibaba.ageiport.test.processor.core.TestHelper;
import com.alibaba.ageiport.test.processor.core.model.Query;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class ClusterExportProcessorTest {

    @SneakyThrows
    @Test
    public void test() {
        AgeiPortOptions options = new AgeiPortOptions();
        AgeiPortOptions.Debug debug = new AgeiPortOptions.Debug();
        options.setDebug(debug);
        AgeiPort ageiPort = AgeiPort.ageiPort(options);

        Query query = new Query();
        query.setTotalCount(100);

        TaskExecuteParam request = new TaskExecuteParam();
        String simpleName = ClusterExportProcessor.class.getSimpleName();
        request.setTaskSpecificationCode(simpleName);
        request.setBizUserId("userId");
        request.setBizQuery(JsonUtil.toJsonString(query));
        TaskExecuteResult response = ageiPort.getTaskService().executeTask(request);
        Assertions.assertTrue(response.getSuccess());

        TestHelper testHelper = new TestHelper(ageiPort);
        testHelper.assertWithFile(response.getMainTaskId(), query.getTotalCount());
    }


}
