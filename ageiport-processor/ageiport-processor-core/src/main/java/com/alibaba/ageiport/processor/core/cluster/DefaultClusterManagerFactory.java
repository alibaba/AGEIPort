package com.alibaba.ageiport.processor.core.cluster;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterManager;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterManagerFactory;
import com.alibaba.ageiport.processor.core.spi.cluster.ClusterOptions;

/**
 * @author lingyi
 */
public class DefaultClusterManagerFactory implements ClusterManagerFactory {
    @Override
    public ClusterManager create(AgeiPort ageiPort, ClusterOptions options) {
        return new DefaultClusterManager(ageiPort, (DefaultClusterOptions) options);
    }
}
