package com.alibaba.ageiport.task.server.http;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;


/**
 * @author lingyi
 */
@Slf4j
@Path("/ping")
public class PingApi {

    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public String ping() {
        return System.currentTimeMillis() + "";
    }
}
