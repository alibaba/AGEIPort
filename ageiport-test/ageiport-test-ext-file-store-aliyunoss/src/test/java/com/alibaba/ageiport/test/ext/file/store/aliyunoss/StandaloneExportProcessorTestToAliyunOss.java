package com.alibaba.ageiport.test.ext.file.store.aliyunoss;


import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.ext.file.store.aliyunoss.AliyunOssFileStoreOptions;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteResult;
import com.alibaba.ageiport.test.processor.core.TestHelper;
import com.alibaba.ageiport.test.processor.core.exporter.StandaloneExportProcessor;
import com.alibaba.ageiport.test.processor.core.model.Query;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class StandaloneExportProcessorTestToAliyunOss {

    @SneakyThrows
    @Test
    public void test() {
        String endpoint = System.getenv("ageiport.ext.file.store.aliyunoss.endpoint");
        String bucketName = System.getenv("ageiport.ext.file.store.aliyunoss.bucketName");
        String accessKeyId = System.getenv("ageiport.ext.file.store.aliyunoss.accessKeyId");
        String accessKeySecret = System.getenv("ageiport.ext.file.store.aliyunoss.accessKeySecret");

        //1.初始化AgeiPort实例
        AgeiPortOptions options = AgeiPortOptions.debug();
        AliyunOssFileStoreOptions aliyunOssFileStoreOptions = new AliyunOssFileStoreOptions();
        aliyunOssFileStoreOptions.setBucketName(bucketName);
        aliyunOssFileStoreOptions.setEndpoint(endpoint);
        aliyunOssFileStoreOptions.setConfig(new ClientConfiguration());
        DefaultCredentials credentials = new DefaultCredentials(accessKeyId, accessKeySecret);
        DefaultCredentialProvider credentialProvider = new DefaultCredentialProvider(credentials);
        aliyunOssFileStoreOptions.setCredentialsProvider(credentialProvider);
        options.setFileStoreOptions(aliyunOssFileStoreOptions);

        AgeiPort ageiPort = AgeiPort.ageiPort(options);

        //2.构造查询参数TaskExecuteParam
        Query query = new Query();
        query.setTotalCount(2000);

        //3.调用本地方法executeTask，开始执行任务，并获取任务实例ID。
        TaskExecuteParam request = new TaskExecuteParam();
        request.setTaskSpecificationCode(StandaloneExportProcessor.class.getSimpleName());
        request.setBizUserId("userId");
        request.setBizQuery(JsonUtil.toJsonString(query));
        TaskExecuteResult response = ageiPort.getTaskService().executeTask(request);
        Assertions.assertTrue(response.getSuccess());

        //4.使用内部封装的TaskHelp方法判断任务是否执行成功
        TestHelper testHelper = new TestHelper(ageiPort);
        testHelper.assertWithFile(response.getMainTaskId(), query.getTotalCount());
    }

}