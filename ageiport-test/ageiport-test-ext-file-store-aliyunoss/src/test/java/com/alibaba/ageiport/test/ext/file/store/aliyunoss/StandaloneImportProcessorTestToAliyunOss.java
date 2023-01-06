package com.alibaba.ageiport.test.ext.file.store.aliyunoss;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.ext.file.store.aliyunoss.AliyunOssFileStoreOptions;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteResult;
import com.alibaba.ageiport.test.processor.core.TestHelper;
import com.alibaba.ageiport.test.processor.core.importer.StandaloneImportProcessor;
import com.alibaba.ageiport.test.processor.core.model.Query;
import com.alibaba.ageiport.test.processor.core.model.View;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
public class StandaloneImportProcessorTestToAliyunOss {

    //本例运行不会返回错误数据
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

        //2.读取文件，并上传到文件存储中
        String taskCode = StandaloneImportProcessor.class.getSimpleName();
        TestHelper testHelper = new TestHelper(ageiPort);
        String filePath = testHelper.file(taskCode + ".xlsx");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
        String fileKey = UUID.randomUUID().toString();
        ageiPort.getFileStore().save(fileKey, inputStream, new HashMap<>());

        //3.构造查询参数TaskExecuteParam
        TaskExecuteParam request = new TaskExecuteParam();
        Query query = new Query();
        query.setTotalCount(100);
        request.setTaskSpecificationCode(taskCode);
        request.setBizUserId("userId");
        request.setBizQuery(JsonUtil.toJsonString(query));
        request.setInputFileKey(fileKey);

        //4.调用本地方法executeTask，开始执行任务，并获取任务实例ID
        TaskExecuteResult response = ageiPort.getTaskService().executeTask(request);

        //5.使用内部封装的TaskHelp方法判断任务是否执行成功
        Assertions.assertTrue(response.getSuccess());
        testHelper.assertWithoutFile(response.getMainTaskId());
    }

    //本例运行会返回错误数据
    @SneakyThrows
    @Test
    public void testHasCheckError() {
        //1.初始化AgeiPort实例
        AgeiPortOptions options = AgeiPortOptions.debug();
        AgeiPort ageiPort = AgeiPort.ageiPort(options);

        //2.读取文件，并上传到文件存储中
        String taskCode = StandaloneImportProcessor.class.getSimpleName();
        TestHelper testHelper = new TestHelper(ageiPort);
        String filePath = testHelper.file(taskCode + ".xlsx");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
        String fileKey = UUID.randomUUID().toString();
        ageiPort.getFileStore().save(fileKey, inputStream, new HashMap<>());

        //3.构造查询参数TaskExecuteParam
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

        //4.调用本地方法executeTask，开始执行任务，并获取任务实例ID
        TaskExecuteResult response = ageiPort.getTaskService().executeTask(request);

        //5.使用内部封装的TaskHelp方法判断任务是否执行成功
        Assertions.assertTrue(response.getSuccess());
        testHelper.assertWithFile(response.getMainTaskId(), query.getErrorCount());
    }
}