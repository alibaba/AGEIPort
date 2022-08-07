package com.alibaba.ageiport.processor.core.spi.cluster;

import com.alibaba.ageiport.processor.core.AgeiPort;

import java.util.List;

/**
 * @author lingyi
 */
public interface ClusterManager {

    AgeiPort getAgei();

    List<Node> getNodes();

    Node getLocalNode();

    void join();

    void leave();

    List<NodeListener> getNodeListeners();
}
