package com.alibaba.ageiport.ext.cluster;

import com.alibaba.ageiport.processor.core.spi.cluster.Node;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * @author lingyi
 */
@Getter
@Setter
public class SpringCloudClusterOptions implements com.alibaba.ageiport.processor.core.spi.cluster.ClusterOptions {

    private SpringCloudNode localNode;

    private List<SpringCloudNode> nodes;

    private DiscoveryClient discoveryClient;

    private ConfigurableApplicationContext applicationContext;

    private Integer retrySleepTimeMs = 5000;

    @Override
    public String type() {
        return "SpringCloudClusterManagerFactory";
    }

}
