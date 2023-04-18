package com.alibaba.ageiport.task.server.http;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
