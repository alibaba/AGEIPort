package com.alibaba.ageiport.processor.core.client.http;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClientFactory;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClientOptions;

/**
 * @author lingyi
 */
public class HttpTaskServerClientFactory implements TaskServerClientFactory {
    @Override
    public TaskServerClient taskServerClient(AgeiPort ageiPort, TaskServerClientOptions options) {
        HttpTaskServerClientOptions httpTaskServerClientOptions = (HttpTaskServerClientOptions) options;
        return new HttpTaskServerClient(ageiPort, httpTaskServerClientOptions);
    }
}
