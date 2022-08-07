package com.alibaba.ageiport.task.server.config;

import lombok.Data;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;


/**
 * @author lingyi
 */
@ApplicationScoped
@Data
public class TaskServerConfig {
    @ConfigProperty(name = "ageiport.env", defaultValue = "PRODUCTION")
    protected String env;
}
