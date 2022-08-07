package com.alibaba.ageiport.processor.core.api.http;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.api.ApiServer;
import com.alibaba.ageiport.processor.core.spi.api.ApiServerFactory;
import com.alibaba.ageiport.processor.core.spi.api.ApiServerOptions;

/**
 * @author lingyi
 */
public class HttpApiServerFactory implements ApiServerFactory {
    @Override
    public ApiServer create(AgeiPort ageiPort, ApiServerOptions options) {
        return new HttpApiServer(ageiPort, (HttpApiServerOptions) options);
    }
}
