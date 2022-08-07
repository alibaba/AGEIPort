package com.alibaba.ageiport.processor.core.cluster;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterManager;
import com.alibaba.ageiport.processor.core.spi.cluster.Node;
import com.alibaba.ageiport.processor.core.spi.cluster.NodeListener;

import java.util.List;

/**
 * @author lingyi
 */
public class DefaultClusterManager implements ClusterManager {

    private AgeiPort ageiPort;

    private DefaultClusterOptions options;

    private Node localNode;

    private List<Node> nodes;

    private List<NodeListener> nodeListeners;

    public DefaultClusterManager(AgeiPort ageiPort, DefaultClusterOptions options) {
        this.ageiPort = ageiPort;
        this.options = options;
        this.localNode = options.getLocalNode();
        this.nodes = options.getNodes();
        this.nodes.add(this.localNode);
        this.nodeListeners = options.getNodeListeners();

    }

    @Override
    public AgeiPort getAgei() {
        return ageiPort;
    }

    @Override
    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public Node getLocalNode() {
        return localNode;
    }

    @Override
    public void join() {
        //DoNothing
    }

    @Override
    public void leave() {
        //DoNothing
    }

    @Override
    public List<NodeListener> getNodeListeners() {
        return nodeListeners;
    }
}
