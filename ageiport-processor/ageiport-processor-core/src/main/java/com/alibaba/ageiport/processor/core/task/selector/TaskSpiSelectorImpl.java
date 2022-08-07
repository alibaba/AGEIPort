package com.alibaba.ageiport.processor.core.task.selector;

import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author lingyi
 */
public class TaskSpiSelectorImpl implements TaskSpiSelector {

    private Map<String, String> mappings;

    public TaskSpiSelectorImpl(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    @Override
    public <T> String selectExtensionName(String taskExecuteType, String taskType, String taskCode, Class<T> spi) {
        ExtensionLoader<T> extensionLoader = ExtensionLoader.getExtensionLoader(spi);
        String extensionName = getKey(null, null, taskCode, spi);
        if (extensionLoader.hasExtension(extensionName)) {
            return extensionName;
        }
        extensionName = getKey(taskExecuteType, taskType, null, spi);
        if (extensionLoader.hasExtension(extensionName)) {
            return extensionName;
        }
        extensionName = getKey(taskExecuteType, null, null, spi);
        if (extensionLoader.hasExtension(extensionName)) {
            return extensionName;
        }

        extensionName = getKey(null, taskType, null, spi);
        if (extensionLoader.hasExtension(extensionName)) {
            return extensionName;
        }
        extensionName = getKey(null, null, null, spi);
        if (extensionLoader.hasExtension(extensionName)) {
            return extensionName;
        }
        String template = "no support extension for spi:{},taskExecuteType:{},taskType:{},taskCode:{}";
        String errorMsg = StringUtils.format(template, spi.getName(), taskExecuteType, taskType, taskCode);
        throw new IllegalArgumentException(errorMsg);
    }

    @NotNull
    private static <T> String getKey(String taskExecuteType, String taskType, String taskCode, Class<T> spi) {
        String prefix = "";
        if (StringUtils.isNotBlank(taskExecuteType)) {
            prefix += taskExecuteType.substring(0, 1).toUpperCase() + taskExecuteType.substring(1).toLowerCase();
        }
        if (StringUtils.isNotBlank(taskType)) {
            prefix += taskType.substring(0, 1).toUpperCase() + taskType.substring(1).toLowerCase();
        }
        if (StringUtils.isNotBlank(taskCode)) {
            prefix += taskCode;
        }
        if (prefix.length() == 0) {
            return spi.getSimpleName() + "Impl";
        }
        return prefix + spi.getSimpleName();
    }

    @Override
    public <T> T selectExtension(String taskExecuteType, String taskType, String taskCode, Class<T> spi) {
        String selectExtensionName = selectExtensionName(taskExecuteType, taskType, taskCode, spi);
        return ExtensionLoader.getExtensionLoader(spi).getExtension(selectExtensionName);
    }


    @Override
    public <T> void registryExtension(String taskExecuteType, String taskType, String taskCode, Class<T> spi, String extensionName) {
        String key = getKey(taskExecuteType, taskType, taskCode, spi);
        mappings.put(taskExecuteType, key);
    }


}
