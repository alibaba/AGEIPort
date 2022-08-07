package com.alibaba.ageiport.processor.core.client.memory;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClientFactory;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClientOptions;

/**
 * @author lingyi
 */
public class MemoryTaskServerClientFactory implements TaskServerClientFactory {
    @Override
    public TaskServerClient taskServerClient(AgeiPort ageiPort, TaskServerClientOptions options) {
        return new MemoryTaskServerClient();
    }
}
