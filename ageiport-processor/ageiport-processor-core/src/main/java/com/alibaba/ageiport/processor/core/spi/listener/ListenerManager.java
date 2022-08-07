package com.alibaba.ageiport.processor.core.spi.listener;

import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;

import java.util.Set;

/**
 * @author lingyi
 */
public class ListenerManager {

    private AgeiPort ageiPort;

    public ListenerManager(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        Set<ManageableListener> instances = ExtensionLoader.getExtensionLoader(ManageableListener.class).getSupportedExtensionInstances();
        for (ManageableListener instance : instances) {
            instance.startListen(ageiPort);
        }
    }
}
