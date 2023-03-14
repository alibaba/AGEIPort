package com.alibaba.ageiport.ext.cluster;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.CollectionUtils;
import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.spi.cluster.Node;
import com.alibaba.ageiport.processor.core.spi.cluster.NodeListener;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class SpringCloudClusterManager implements com.alibaba.ageiport.processor.core.spi.cluster.ClusterManager {

    public static Logger log = LoggerFactory.getLogger(SpringCloudClusterManager.class);

    private AgeiPort ageiPort;

    private List<Node> nodes;

    private Node localNode;

    private List<NodeListener> nodeListeners;

    private DiscoveryClient discoveryClient;

    private boolean refreshed = false;

    @Override
    public void join() {

    }

    @Override
    public void leave() {

    }

    @Override
    public List<NodeListener> getNodeListeners() {
        return nodeListeners;
    }

    public void refreshNodes() {
        AgeiPortOptions ageiPortOptions = ageiPort.getOptions();
        String app = ageiPortOptions.getApp();

        List<ServiceInstance> instances = discoveryClient.getInstances(app);
        if (CollectionUtils.isEmpty(instances)) {
            throw new IllegalArgumentException("Cannot found any instance from discovery server, serviceId:" + app);
        }

        List<Node> nodes = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            SpringCloudNode cloudNode = new SpringCloudNode();
            cloudNode.setApp(app);
            cloudNode.setId(instance.getHost() + ":" + instance.getPort());
            cloudNode.setHost(instance.getHost());
            cloudNode.setGroup(app);
            cloudNode.setAttributes(instance.getMetadata());
            cloudNode.setLabels(instance.getMetadata());
            nodes.add(cloudNode);

            if (cloudNode.getHost().equals(localNode.getHost())) {
                this.setLocalNode(cloudNode);
            }
        }
        this.setNodes(nodes);
        log.info("refresh cluster node, clusterSize:{}, local:{}", nodes.size(), JsonUtil.toJsonString(localNode));
        this.setRefreshed(true);
    }
}
