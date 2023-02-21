package com.alibaba.ageiport.processor.core.spi.eventbus;

import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;

/**
 * @author lingyi
 */
public class EventBusManager {

    private AgeiPort ageiPort;

    private EventBus localEventBus;

    private EventBus clusterEventBus;

    public EventBusManager(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        AgeiPortOptions options = ageiPort.getOptions();

        EventBusOptions localEventBusOptions = options.getLocal().getEventBusOptions();
        EventBusFactory localEventBusFactory = ExtensionLoader.getExtensionLoader(EventBusFactory.class).getExtension(localEventBusOptions.type());
        this.localEventBus = localEventBusFactory.create(ageiPort, localEventBusOptions);

        EventBusOptions clusterEventBusOptions = options.getCluster().getEventBusOptions();
        EventBusFactory clusterDispatcherFactory = ExtensionLoader.getExtensionLoader(EventBusFactory.class).getExtension(clusterEventBusOptions.type());
        this.clusterEventBus = clusterDispatcherFactory.create(ageiPort, clusterEventBusOptions);
    }

    public EventBus getEventBus(String type) {
        if (ExecuteType.CLUSTER.equals(type)) {
            return clusterEventBus;
        } else {
            return localEventBus;
        }
    }

}
