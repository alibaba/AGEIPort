package com.alibaba.ageiport.processor.core.spi.service;

import com.alibaba.ageiport.common.Version;
import com.alibaba.ageiport.common.feature.FeatureUtils;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.constants.MainTaskFeatureKeys;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.client.CreateMainTaskRequest;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.task.factory.TaskContext;
import com.alibaba.ageiport.processor.core.spi.task.monitor.MainTaskProgress;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskProgressService;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;

/**
 * @author lingyi
 */
public class TaskServiceImpl implements TaskService {

    Logger log = LoggerFactory.getLogger(TaskContext.class);

    private final AgeiPort ageiPort;

    public TaskServiceImpl(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
    }


    public TaskExecuteResult executeTask(TaskExecuteParam param) {
        try {
            String code = param.getTaskSpecificationCode();
            TaskSpecificationRegistry registry = ageiPort.getSpecificationRegistry();
            TaskSpec taskSpec = registry.get(code);
            String executeType = taskSpec.getExecuteType();
            String taskType = taskSpec.getTaskType();

            CreateMainTaskRequest createMainTaskRequest = BeanUtils.cloneProp(param, CreateMainTaskRequest.class);
            createMainTaskRequest.setCode(param.getTaskSpecificationCode());
            createMainTaskRequest.setExecuteType(executeType);
            createMainTaskRequest.setType(taskType);
            createMainTaskRequest.setName(taskSpec.getTaskName());
            createMainTaskRequest.setHost(ageiPort.getClusterManager().getLocalNode().getIp());
            String feature = createMainTaskRequest.getFeature();
            feature = FeatureUtils.putFeature(feature, MainTaskFeatureKeys.VERSION, Version.getVersion());
            feature = FeatureUtils.putFeature(feature, MainTaskFeatureKeys.LABELS, JsonUtil.toJsonString(param.getLabels()));
            feature = FeatureUtils.putFeature(feature, MainTaskFeatureKeys.INPUT_FILE_KEY, param.getInputFileKey());
            createMainTaskRequest.setFeature(feature);

            TaskServerClient taskServerClient = ageiPort.getTaskServerClient();
            String mainTaskId = taskServerClient.createMainTask(createMainTaskRequest);
            MainTask mainTask = taskServerClient.getMainTask(mainTaskId);

            ageiPort.getMainTaskCallback().afterCreated(mainTask);
            ageiPort.getTaskAcceptor().accept(mainTask);

            TaskExecuteResult response = new TaskExecuteResult();
            response.setSuccess(true);
            response.setMainTaskId(mainTaskId);
            return response;
        } catch (Throwable e) {
            log.error("TaskService#executeTask failed, request:{}", param, e);
            TaskExecuteResult response = new TaskExecuteResult();
            response.setSuccess(true);
            response.setErrorMessage(e.getMessage());
            return response;
        }

    }

    public TaskProgressResult getTaskProgress(GetTaskProgressParam param) {
        String mainTaskId = param.getMainTaskId();
        TaskProgressService taskProgressService = ageiPort.getTaskProgressService();
        MainTaskProgress taskProgress = taskProgressService.getTaskProgress(mainTaskId);
        TaskProgressResult response = BeanUtils.cloneProp(taskProgress, TaskProgressResult.class);
        return response;
    }

}
