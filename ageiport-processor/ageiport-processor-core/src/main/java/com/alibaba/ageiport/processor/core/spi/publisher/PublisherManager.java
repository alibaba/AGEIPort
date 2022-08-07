package com.alibaba.ageiport.processor.core.spi.publisher;

import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lingyi
 */
public class PublisherManager {

    private AgeiPort ageiPort;

    private Map<Class, ManageablePublisher> publishers;

    public PublisherManager(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        this.publishers = new HashMap<>();
        Set<ManageablePublisher> instances = ExtensionLoader.getExtensionLoader(ManageablePublisher.class).getSupportedExtensionInstances();
        for (ManageablePublisher instance : instances) {
            instance.startPublish(ageiPort);
            publishers.put(instance.publishEventType(), instance);
        }
    }

    public <T extends EventObject> ManageablePublisher<T> getPublisher(Class<T> type) {
        return publishers.get(type);
    }
}
