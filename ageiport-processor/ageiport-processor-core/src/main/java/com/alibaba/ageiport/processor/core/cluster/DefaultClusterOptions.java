package com.alibaba.ageiport.processor.core.cluster;

import com.alibaba.ageiport.common.utils.NetUtils;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterOptions;
import com.alibaba.ageiport.processor.core.spi.cluster.Node;
import com.alibaba.ageiport.processor.core.spi.cluster.NodeListener;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author lingyi
 */
@Getter
@Setter
public class DefaultClusterOptions implements ClusterOptions {
    @Override
    public String type() {
        return "DefaultClusterManagerFactory";
    }

    private Node localNode = new NodeImpl(UUID.randomUUID().toString(), NetUtils.getInstanceIp(), "defaultGroup", new HashMap<>(), new HashMap<>());

    private List<NodeListener> nodeListeners = new ArrayList<>();

    private List<Node> nodes = new ArrayList<>();


}
