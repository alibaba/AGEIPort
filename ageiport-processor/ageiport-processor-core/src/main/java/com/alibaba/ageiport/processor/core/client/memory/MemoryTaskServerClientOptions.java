package com.alibaba.ageiport.processor.core.client.memory;

import com.alibaba.ageiport.processor.core.spi.client.TaskServerClientOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lingyi
 */
@ToString
@Getter
@Setter
public class MemoryTaskServerClientOptions implements TaskServerClientOptions {

    @Override
    public String type() {
        return "MemoryTaskServerClient";
    }

}
