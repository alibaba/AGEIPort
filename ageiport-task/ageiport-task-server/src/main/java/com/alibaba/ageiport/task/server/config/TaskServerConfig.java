package com.alibaba.ageiport.task.server.config;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Data;
import org.eclipse.microprofile.config.inject.ConfigProperty;



/**
 * @author lingyi
 */
@ApplicationScoped
@Data
public class TaskServerConfig {
    @ConfigProperty(name = "ageiport.env", defaultValue = "PRODUCTION")
    protected String env;
    @ConfigProperty(name = "ageiport.task.server.oauth2.enable", defaultValue = "false")
    protected boolean enableOauth2;
}
