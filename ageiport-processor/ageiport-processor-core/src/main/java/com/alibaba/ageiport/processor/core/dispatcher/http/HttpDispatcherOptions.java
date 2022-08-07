package com.alibaba.ageiport.processor.core.dispatcher.http;

import com.alibaba.ageiport.processor.core.spi.dispatcher.DispatcherOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lingyi
 */
@ToString
@Getter
@Setter
public class HttpDispatcherOptions implements DispatcherOptions {

    private Integer port = 9431;

    private Integer nodeFallbackMs = 20 * 60 * 1000;

    @Override
    public String type() {
        return "HttpDispatcherFactory";
    }
}
