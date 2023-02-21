package com.alibaba.ageiport.processor.core.api.http;

import com.alibaba.ageiport.processor.core.spi.api.ApiServerOptions;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */

@Getter
@Setter
public class HttpApiServerOptions implements ApiServerOptions {
    @Override
    public String type() {
        return HttpApiServerFactory.class.getSimpleName();
    }

    private Integer port = 9741;
}
