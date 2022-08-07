package com.alibaba.ageiport.processor.core.task;

import com.alibaba.ageiport.common.collections.Lists;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.Processor;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationProvider;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationResolver;

import java.util.List;
import java.util.Set;

/**
 * @author lingyi
 */
public class DefaultTaskSpecificationProvider implements TaskSpecificationProvider {
    @Override
    public List<TaskSpec> provide(AgeiPort ageiPort) {
        Set<Processor> instances = ExtensionLoader.getExtensionLoader(Processor.class).getSupportedExtensionInstances();

        List<TaskSpec> taskSpecifications = Lists.newArrayListWithCapacity(instances.size());
        for (Processor instance : instances) {
            String resolver = instance.resolver();
            ExtensionLoader<TaskSpecificationResolver> resolverExtensionLoader = ExtensionLoader.getExtensionLoader(TaskSpecificationResolver.class);
            TaskSpecificationResolver specificationResolver = resolverExtensionLoader.getExtension(resolver);
            TaskSpec taskSpec = specificationResolver.resolve(instance);
            if (taskSpec == null) {
                continue;
            }
            taskSpecifications.add(taskSpec);
        }
        return taskSpecifications;
    }
}
