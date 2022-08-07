package com.alibaba.ageiport.processor.core.spi.dispatcher;

import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;

/**
 * @author lingyi
 */
public class DispatcherManager {

    private AgeiPort ageiPort;

    private Dispatcher localDispatcher;

    private Dispatcher clusterDispatcher;

    public DispatcherManager(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        AgeiPortOptions options = ageiPort.getOptions();

        DispatcherOptions localDispatcherOptions = options.getLocal().getDispatcherOptions();
        DispatcherFactory localDispatcherFactory = ExtensionLoader.getExtensionLoader(DispatcherFactory.class).getExtension(localDispatcherOptions.type());
        this.localDispatcher = localDispatcherFactory.create(ageiPort, localDispatcherOptions);

        DispatcherOptions clusterDispatcherOptions = options.getCluster().getDispatcherOptions();
        DispatcherFactory clusterDispatcherFactory = ExtensionLoader.getExtensionLoader(DispatcherFactory.class).getExtension(clusterDispatcherOptions.type());
        this.clusterDispatcher = clusterDispatcherFactory.create(ageiPort, clusterDispatcherOptions);
    }

    public Dispatcher getDispatcher(String type) {
        if (ExecuteType.STANDALONE.equals(type)) {
            return localDispatcher;
        } else {
            return clusterDispatcher;
        }
    }
}
