package com.alibaba.ageiport.processor.core.task.registry;

import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;
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

    private Map<String, TaskSpec> taskSpecMap = new ConcurrentHashMap<>();


    public SpecificationRegistryImpl(AgeiPort ageiPort) {
        ExtensionLoader<TaskSpecificationProvider> extensionLoader = ExtensionLoader.getExtensionLoader(TaskSpecificationProvider.class);
        Set<TaskSpecificationProvider> extensionInstances = extensionLoader.getSupportedExtensionInstances();

        for (TaskSpecificationProvider provider : extensionInstances) {
            List<TaskSpec> taskSpecs = provider.provide(ageiPort);
            for (TaskSpec taskSpec : taskSpecs) {
                taskSpecMap.put(taskSpec.getTaskCode(), taskSpec);
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
