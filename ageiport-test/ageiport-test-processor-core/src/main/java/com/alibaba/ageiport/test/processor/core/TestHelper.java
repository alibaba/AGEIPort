package com.alibaba.ageiport.test.processor.core;

import com.alibaba.ageiport.common.feature.FeatureUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.MainTaskFeatureKeys;
import com.alibaba.ageiport.processor.core.model.core.ColumnHeader;
import com.alibaba.ageiport.processor.core.model.core.impl.ColumnHeaderImpl;
import com.alibaba.ageiport.processor.core.model.core.impl.ColumnHeadersImpl;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.spi.file.FileReader;
import com.alibaba.ageiport.processor.core.spi.file.FileReaderFactory;
import com.alibaba.ageiport.processor.core.spi.service.TaskProgressParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskProgressResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
public class TestHelper {

    private AgeiPort ageiPort;

    public TestHelper(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
    }

    public String file(String fileName) {
        return "." + File.separator + "import-xlsx" + File.separator + fileName;
    }

    public void assertWithoutFile(String mainTaskId) throws InterruptedException {
        //1.创建进度查询请求参数GetTaskProgressParam
        TaskProgressParam progressRequest = new TaskProgressParam();
        progressRequest.setMainTaskId(mainTaskId);
        //2.调用本地方法，查询任务进度。
        TaskProgressResult taskProgress = ageiPort.getTaskService().getTaskProgress(progressRequest);
        int sleepTime = 0;
        log.info("getTaskProgress, taskProgress:{}", taskProgress);
        //3.轮询任务进度，直至任务完成或出错
        while (taskProgress == null || !taskProgress.getIsFinished() && !taskProgress.getIsError()) {
            Thread.sleep(1000);
            if (sleepTime++ > 100) {
                Assertions.assertTrue(taskProgress.getIsFinished() || taskProgress.getIsError());
            }
            taskProgress = ageiPort.getTaskService().getTaskProgress(progressRequest);
            if (taskProgress != null) {
                log.info("getTaskProgress, percent:{}, stageName:{}", taskProgress.getPercent(), taskProgress.getStageName());
            } else {
                log.info("no progress...");
            }
        }
        Assertions.assertTrue(taskProgress.getIsFinished());
        Assertions.assertEquals(1, taskProgress.getPercent());
    }

    public void assertWithFile(String mainTaskId, Integer outputCount) throws InterruptedException {
        //1.创建进度查询请求参数GetTaskProgressParam
        TaskProgressParam progressRequest = new TaskProgressParam();
        progressRequest.setMainTaskId(mainTaskId);
        //2.调用本地方法，查询任务进度。
        TaskProgressResult taskProgress = ageiPort.getTaskService().getTaskProgress(progressRequest);
        int sleepTime = 0;
        log.info("getTaskProgress, taskProgress:{}", taskProgress);
        //3.轮询任务进度，直至任务完成或出错
        while (taskProgress == null || !taskProgress.getIsFinished() && !taskProgress.getIsError()) {
            Thread.sleep(1000);
            if (sleepTime++ > 100) {
                Assertions.assertTrue(taskProgress.getIsFinished() || taskProgress.getIsError());
            }
            taskProgress = ageiPort.getTaskService().getTaskProgress(progressRequest);
            if (taskProgress != null) {
                log.info("getTaskProgress, percent:{}, stageName:{}", taskProgress.getPercent(), taskProgress.getStageName());
            } else {
                log.info("no progress...");
            }
        }
        Assertions.assertTrue(taskProgress.getIsFinished());
        Assertions.assertEquals(1, taskProgress.getPercent());

        //4.任务完成后，查询任务实例信息，任务输出的文件信息
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(taskProgress.getMainTaskId());
        String fileKey = FeatureUtils.getFeature(mainTask.getFeature(), MainTaskFeatureKeys.OUTPUT_FILE_KEY);
        Assertions.assertNotNull(fileKey);
        boolean exists = ageiPort.getFileStore().exists(fileKey, new HashMap<>());
        Assertions.assertTrue(exists);

        String runtimeParam = mainTask.getRuntimeParam();
        String fileType = FeatureUtils.getFeature(runtimeParam, MainTaskFeatureKeys.RT_FILE_TYPE_KEY);
        String headersString = FeatureUtils.getFeature(runtimeParam, MainTaskFeatureKeys.RT_COLUMN_HEADERS_KEY);
        List<ColumnHeaderImpl> columnHeaderList = JsonUtil.toArrayObject(headersString, ColumnHeaderImpl.class);
        List<ColumnHeader> columnHeaderList1 = new ArrayList<>(columnHeaderList);
        ColumnHeadersImpl headers = new ColumnHeadersImpl(columnHeaderList1);

        //5.下载文件到本地
        InputStream inputStream = ageiPort.getFileStore().get(fileKey, new HashMap<>());
        String outputFileReaderFactory = ageiPort.getOptions().getFileTypeReaderSpiMappings().get(fileType);
        final FileReaderFactory factory = ExtensionLoader.getExtensionLoader(FileReaderFactory.class).getExtension(outputFileReaderFactory);

        //6.断言判断产生的文件是否符合期望
        FileReader fileReader = factory.create(ageiPort, headers);
        fileReader.read(inputStream);
        DataGroup dataGroup = fileReader.finish();
        int count = 0;
        List<DataGroup.Data> data = dataGroup.getData();
        for (DataGroup.Data datum : data) {
            if (datum.getItems() != null) {
                count += datum.getItems().size();
            }
        }
    }
}
