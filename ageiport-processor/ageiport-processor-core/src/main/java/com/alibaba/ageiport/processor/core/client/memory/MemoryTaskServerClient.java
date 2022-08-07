package com.alibaba.ageiport.processor.core.client.memory;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.BeanUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.TaskIdUtil;
import com.alibaba.ageiport.processor.core.constants.TaskStatus;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecification;
import com.alibaba.ageiport.processor.core.spi.client.CreateMainTaskRequest;
import com.alibaba.ageiport.processor.core.spi.client.CreateSpecificationRequest;
import com.alibaba.ageiport.processor.core.spi.client.CreateSubTasksRequest;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public class MemoryTaskServerClient implements TaskServerClient {

    public static Logger LOGGER = LoggerFactory.getLogger(MemoryTaskServerClient.class);


    private Map<String, MainTask> mainTaskMap = new HashMap<>();
    private Map<String, SubTask> subTaskMap = new HashMap<>();
    private Map<String, TaskSpecification> taskSpecificationMap = new HashMap<>();

    public MemoryTaskServerClient() {

    }

    @Override
    public String createMainTask(CreateMainTaskRequest createMainTaskRequest) {
        MainTask mainTask = BeanUtils.cloneProp(createMainTaskRequest, MainTask.class);
        mainTask.setMainTaskId(TaskIdUtil.genMainTaskId());
        mainTask.setStatus(TaskStatus.NEW);
        mainTaskMap.put(mainTask.getMainTaskId(), mainTask);
        return mainTask.getMainTaskId();
    }

    @Override
    public void updateMainTask(MainTask mainTask) {
        String mainTaskId = mainTask.getMainTaskId();
        MainTask exist = mainTaskMap.get(mainTaskId);
        String mergeFeature = JsonUtil.merge(exist.getFeature(), mainTask.getFeature());
        String mergeRuntime = JsonUtil.merge(exist.getRuntimeParam(), mainTask.getRuntimeParam());
        String merge = JsonUtil.merge(JsonUtil.toJsonString(exist), JsonUtil.toJsonString(mainTask));
        MainTask newTask = JsonUtil.toObject(merge, MainTask.class);
        newTask.setFeature(mergeFeature);
        newTask.setRuntimeParam(mergeRuntime);
        mainTaskMap.put(mainTaskId, newTask);
    }


    @Override
    public MainTask getMainTask(String mainTaskId) {
        return mainTaskMap.get(mainTaskId);
    }

    @Override
    public List<String> createSubTask(CreateSubTasksRequest createSubTasksRequest) {
        List<String> subTaskIds = new ArrayList<>();
        String mainTaskId = createSubTasksRequest.getMainTaskId();
        List<CreateSubTasksRequest.SubTaskInstance> subTaskInstances = createSubTasksRequest.getSubTaskInstances();
        for (CreateSubTasksRequest.SubTaskInstance subTaskInstance : subTaskInstances) {
            MainTask mainTask = mainTaskMap.get(mainTaskId);
            SubTask subTask = BeanUtils.cloneProp(mainTask, SubTask.class);
            subTask.setSubTaskId(TaskIdUtil.genSubTaskId(mainTaskId, subTaskInstance.getSubTaskNo()));
            subTask.setMainTaskId(mainTaskId);
            this.subTaskMap.put(subTask.getSubTaskId(), subTask);
            subTaskIds.add(subTask.getSubTaskId());
        }
        return subTaskIds;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        String subTaskId = subTask.getSubTaskId();
        SubTask exist = subTaskMap.get(subTaskId);
        String mergeFeature = JsonUtil.merge(exist.getFeature(), subTask.getFeature());
        String mergeRuntime = JsonUtil.merge(exist.getRuntimeParam(), subTask.getRuntimeParam());
        String merge = JsonUtil.merge(JsonUtil.toJsonString(exist), JsonUtil.toJsonString(subTask));
        SubTask newTask = JsonUtil.toObject(merge, SubTask.class);
        newTask.setFeature(mergeFeature);
        newTask.setRuntimeParam(mergeRuntime);
        subTaskMap.put(subTaskId, newTask);
    }


    @Override
    public SubTask getSubTask(String subTaskId) {
        return this.subTaskMap.get(subTaskId);
    }

    @Override
    public TaskSpecification getTaskSpecification(String taskCode) {
        return this.taskSpecificationMap.get(taskCode);
    }

    @Override
    public String createTaskSpecification(CreateSpecificationRequest createSpecificationRequest) {
        TaskSpecification taskSpecification = BeanUtils.cloneProp(createSpecificationRequest, TaskSpecification.class);
        this.taskSpecificationMap.put(taskSpecification.getTaskCode(), taskSpecification);
        return createSpecificationRequest.getTaskCode();
    }
}
