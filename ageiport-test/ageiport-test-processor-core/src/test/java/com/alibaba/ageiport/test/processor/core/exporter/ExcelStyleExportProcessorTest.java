package com.alibaba.ageiport.test.processor.core.exporter;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.file.excel.ExcelWriteHandlerProviderSpiConfig;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteResult;
import com.alibaba.ageiport.test.processor.core.TestHelper;
import com.alibaba.ageiport.test.processor.core.excel.UDFExcelWriteHandlerProvider;
import com.alibaba.ageiport.test.processor.core.model.Query;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ExcelStyleExportProcessorTest {
    @SneakyThrows
    @Test
    public void test() {
        //1.初始化AgeiPort实例
        AgeiPortOptions options = AgeiPortOptions.debug();
        //添加ExcelWriteHandlerProvider配置
        Map<String, Map<String, String>> spiConfigs = options.getSpiConfigs();
        Map<String, String> handlerProvider = spiConfigs.get("ExcelWriteHandlerProvider");
        ExcelWriteHandlerProviderSpiConfig config = JsonUtil.toObject(handlerProvider, ExcelWriteHandlerProviderSpiConfig.class);
        config.setExtensionName(UDFExcelWriteHandlerProvider.class.getSimpleName());
        spiConfigs.put("ExcelWriteHandlerProvider", JsonUtil.toMap(config));

        AgeiPort ageiPort = AgeiPort.ageiPort(options);

        //2.构造查询参数TaskExecuteParam
        Query query = new Query();
        query.setTotalCount(2000);
        query.setDynamicHeaderCount(3);

        //3.调用本地方法executeTask，开始执行任务，并获取任务实例ID。
        TaskExecuteParam request = new TaskExecuteParam();
        request.setTaskSpecificationCode(ExcelStyleExportProcessor.class.getSimpleName());
        request.setBizUserId("userId");
        request.setBizQuery(JsonUtil.toJsonString(query));
        TaskExecuteResult response = ageiPort.getTaskService().executeTask(request);
        Assertions.assertTrue(response.getSuccess());

        //4.使用内部封装的TaskHelp方法判断任务是否执行成功
        TestHelper testHelper = new TestHelper(ageiPort);
        testHelper.assertWithFile(response.getMainTaskId(), query.getTotalCount());
    }
}
