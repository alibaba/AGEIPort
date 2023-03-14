package com.alibaba.ageiport.ext.cluster;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.common.utils.NetUtils;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterManager;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterOptions;
import com.alibaba.ageiport.processor.core.spi.cluster.Node;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpringCloudClusterManagerFactory implements com.alibaba.ageiport.processor.core.spi.cluster.ClusterManagerFactory {

    public static final Logger log = LoggerFactory.getLogger(SpringCloudClusterManagerFactory.class);

    @Override
    public ClusterManager create(AgeiPort ageiPort, ClusterOptions options) {
        AgeiPortOptions ageiPortOptions = ageiPort.getOptions();
        String app = ageiPortOptions.getApp();

        SpringCloudClusterOptions springCloudClusterOptions = (SpringCloudClusterOptions) options;

        ConfigurableApplicationContext applicationContext = springCloudClusterOptions.getApplicationContext();
        if (applicationContext == null) {
            throw new IllegalArgumentException("SpringCloudClusterOptions applicationContext is null");
        }
        DiscoveryClient discoveryClient = springCloudClusterOptions.getDiscoveryClient();
        if (discoveryClient == null) {
            throw new IllegalArgumentException("SpringCloudClusterOptions discoveryClient is null");
        }

        SpringCloudNode localNode = springCloudClusterOptions.getLocalNode();

        if (localNode == null) {
            localNode = new SpringCloudNode();
            localNode.setApp(app);
            localNode.setId(UUID.randomUUID().toString());
            localNode.setHost(NetUtils.getInstanceIp());
            localNode.setGroup("defaultGroup");
        }

        boolean containsLocalNode = false;

        List<Node> nodes = new ArrayList<>();
        if (springCloudClusterOptions.getNodes() != null) {
            nodes.addAll(springCloudClusterOptions.getNodes());
            for (Node node : nodes) {
                if (node.getHost().equals(localNode.getHost())) {
                    containsLocalNode = true;
                }
            }
        }
        if (!containsLocalNode) {
            nodes.add(localNode);
        }


        SpringCloudClusterManager clusterManager = new SpringCloudClusterManager();
        clusterManager.setAgeiPort(ageiPort);
        clusterManager.setDiscoveryClient(discoveryClient);
        clusterManager.setNodes(nodes);
        clusterManager.setLocalNode(localNode);

        log.info("SpringCloudCluster init, local:{}, nodes:{}", JsonUtil.toJsonString(localNode), JsonUtil.toJsonString(nodes));

        SpringCloudHeartbeatEventListener listener = new SpringCloudHeartbeatEventListener(clusterManager);
        applicationContext.addApplicationListener(listener);
        return clusterManager;
    }

}
