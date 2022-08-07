package com.alibaba.ageiport.processor.core.client.http;

import com.alibaba.ageiport.processor.core.spi.client.TaskServerClientOptions;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class HttpTaskServerClientOptions implements TaskServerClientOptions {

    @Override
    public String type() {
        return "HttpTaskServerClient";
    }

    private String accessKeyId;

    private String accessKeySecret;

    private String schema = "http";

    private String endpoint = "agei-server-task";

    private Integer port = 8710;

    private Integer timeoutMs = 3000;
}
