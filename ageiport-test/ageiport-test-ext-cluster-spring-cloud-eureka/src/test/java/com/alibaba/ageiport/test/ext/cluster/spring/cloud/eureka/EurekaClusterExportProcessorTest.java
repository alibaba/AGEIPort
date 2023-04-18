package com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.ext.cluster.SpringCloudClusterManager;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteResult;
import com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka.model.Query;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
public class EurekaClusterExportProcessorTest {

    public static Logger log = LoggerFactory.getLogger(EurekaClusterExportProcessorTest.class);


    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private AgeiPort ageiPort;

    @SneakyThrows
    @Test
    public void test() {
        SpringCloudClusterManager clusterManager = (SpringCloudClusterManager) ageiPort.getClusterManager();
        for (int i = 0; i < 10; i++) {
            if (clusterManager.isRefreshed()) {
                break;
            } else {
                log.info("wait refresh...");
                Thread.sleep(5000);
            }
        }
        Assertions.assertTrue(clusterManager.isRefreshed());


        //2.构造查询参数TaskExecuteParam
        Query query = new Query();
        query.setTotalCount(2000);
        query.setDynamicHeaderCount(3);

        //3.调用本地方法executeTask，开始执行任务，并获取任务实例ID。
        TaskExecuteParam request = new TaskExecuteParam();
        request.setTaskSpecificationCode(EurekaClusterExportProcessor.class.getSimpleName());
        request.setBizUserId("userId");
        request.setBizQuery(JsonUtil.toJsonString(query));



        TaskExecuteResult response = ageiPort.getTaskService().executeTask(request);
        Assertions.assertTrue(response.getSuccess());

        //4.使用内部封装的TaskHelp方法判断任务是否执行成功
        TestHelper testHelper = new TestHelper(ageiPort);
        testHelper.assertWithFile(response.getMainTaskId(), query.getTotalCount());
    }

}