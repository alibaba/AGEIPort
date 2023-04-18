package com.alibaba.ageiport.processor.core.task.registry;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecification;
import com.alibaba.ageiport.processor.core.spi.client.CreateSpecificationRequest;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationProvider;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingyi
 */
public class SpecificationRegistryImpl implements TaskSpecificationRegistry {

    public static final Logger logger = LoggerFactory.getLogger(SpecificationRegistryImpl.class);

    private Map<String, TaskSpec> taskSpecMap = new ConcurrentHashMap<>();


    public SpecificationRegistryImpl(AgeiPort ageiPort) {
        ExtensionLoader<TaskSpecificationProvider> extensionLoader = ExtensionLoader.getExtensionLoader(TaskSpecificationProvider.class);
        Set<TaskSpecificationProvider> extensionInstances = extensionLoader.getSupportedExtensionInstances();

        TaskServerClient taskServerClient = ageiPort.getTaskServerClient();

        for (TaskSpecificationProvider provider : extensionInstances) {
            List<TaskSpec> taskSpecs = provider.provide(ageiPort);
            for (TaskSpec taskSpec : taskSpecs) {
                taskSpecMap.put(taskSpec.getTaskCode(), taskSpec);
                TaskSpecification specification = taskServerClient.getTaskSpecification(taskSpec.getTaskCode());
                if (specification != null) {
                    logger.info("TaskSpecification already exist, app:{},code:{}", ageiPort.getOptions().getApp(), specification.getTaskCode());
                } else {
                    CreateSpecificationRequest request = new CreateSpecificationRequest();
                    request.setTaskCode(taskSpec.getTaskCode());
                    request.setTaskName(taskSpec.getTaskName());
                    request.setTaskDesc(taskSpec.getTaskDesc());
                    request.setTaskType(taskSpec.getTaskType());
                    request.setTaskExecuteType(taskSpec.getExecuteType());
                    request.setTaskHandler(taskSpec.getProcessor().getClass().getName());
                    try {
                        String result = taskServerClient.createTaskSpecification(request);
                        logger.info("TaskSpecification already registry, app:{},code:{}, id:{}", ageiPort.getOptions().getApp(), taskSpec.getTaskCode(), result);
                    } catch (Throwable e) {
                        logger.error("createTaskSpecification failed, request:{}", request, e);
                        throw new IllegalStateException("createTaskSpecification failed, code:" + taskSpec.getTaskCode());
                    }
                }
            }
        }
    }

    @Override
    public void add(TaskSpec taskSpec) {
        taskSpecMap.put(taskSpec.getTaskCode(), taskSpec);
    }


    @Override
    public TaskSpec get(String taskCode) {
        if (taskCode == null) {
            throw new IllegalArgumentException("taskCode is null");
        }
        return taskSpecMap.get(taskCode);
    }
}
