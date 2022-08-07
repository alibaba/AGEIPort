package com.alibaba.ageiport.processor.core.spi.task.selector;

/**
 * @author lingyi
 */
public interface TaskSpiSelector {
    <T> T selectExtension(String taskExecuteType, String taskType, String taskCode, Class<T> clazz);

    <X> String selectExtensionName(String taskExecuteType, String taskType, String taskCode, Class<X> clazz);

    <T> void registryExtension(String taskExecuteType, String taskType, String taskCode, Class<T> spi, String extensionName);
}
