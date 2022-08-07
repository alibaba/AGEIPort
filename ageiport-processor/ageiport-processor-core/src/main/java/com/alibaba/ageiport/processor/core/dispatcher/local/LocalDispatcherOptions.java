package com.alibaba.ageiport.processor.core.dispatcher.local;

import com.alibaba.ageiport.processor.core.spi.dispatcher.DispatcherOptions;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Getter
@Setter
public class LocalDispatcherOptions implements DispatcherOptions {
    @Override
    public String type() {
        return "LocalDispatcherFactory";
    }
}
