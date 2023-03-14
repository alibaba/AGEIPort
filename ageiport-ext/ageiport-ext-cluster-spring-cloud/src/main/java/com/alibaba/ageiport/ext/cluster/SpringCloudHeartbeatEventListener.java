package com.alibaba.ageiport.ext.cluster;

import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationListener;

public class SpringCloudHeartbeatEventListener implements ApplicationListener<HeartbeatEvent> {

    private SpringCloudClusterManager clusterManager;

    public SpringCloudHeartbeatEventListener(SpringCloudClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public void onApplicationEvent(HeartbeatEvent event) {
        clusterManager.refreshNodes();
    }
}
