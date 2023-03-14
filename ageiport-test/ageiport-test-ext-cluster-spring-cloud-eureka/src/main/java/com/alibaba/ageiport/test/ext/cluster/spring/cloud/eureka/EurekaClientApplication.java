package com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class EurekaClientApplication {


    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

}
