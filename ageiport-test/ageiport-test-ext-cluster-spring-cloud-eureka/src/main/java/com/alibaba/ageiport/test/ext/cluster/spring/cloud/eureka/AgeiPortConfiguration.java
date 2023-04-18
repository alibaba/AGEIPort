package com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka;

import com.alibaba.ageiport.common.utils.NetUtils;
import com.alibaba.ageiport.ext.cluster.SpringCloudClusterOptions;
import com.alibaba.ageiport.ext.cluster.SpringCloudNode;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.client.http.HttpTaskServerClientOptions;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Configuration
public class AgeiPortConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${ageiport.taskServerClientOptions.port}")
    private Integer port;
    @Value("${ageiport.taskServerClientOptions.endpoint}")
    private String endpoint;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private EurekaClient eurekaClient;

    @Bean
    public AgeiPort getAgeiPort() {
        log.info("AgeiPort init start.");

        //1.初始化AgeiPort实例
        AgeiPortOptions options = AgeiPortOptions.debug();

        //以下信息最好统一从Eureka 或 其他地方获取
        SpringCloudNode localNode = new SpringCloudNode();
        localNode.setGroup(applicationName);
        localNode.setHost(NetUtils.getInstanceIp());
        localNode.setId(UUID.randomUUID().toString());
        localNode.setApp(applicationName);
        localNode.setLabels(new HashMap<>());
        localNode.setLabels(new HashMap<>());

        SpringCloudClusterOptions clusterOptions = new SpringCloudClusterOptions();
        clusterOptions.setDiscoveryClient(discoveryClient);
        clusterOptions.setApplicationContext(applicationContext);
        clusterOptions.setLocalNode(localNode);

        options.setClusterOptions(clusterOptions);
        options.setApp(applicationName);

        HttpTaskServerClientOptions taskServerClientOptions = new HttpTaskServerClientOptions();
        taskServerClientOptions.setPort(port);
        taskServerClientOptions.setEndpoint(endpoint);
        options.setTaskServerClientOptions(taskServerClientOptions);

        AgeiPort ageiPort = AgeiPort.ageiPort(options);

        log.info("AgeiPort init finished.");
        return ageiPort;
    }
}
