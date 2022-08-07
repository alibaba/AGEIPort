package com.alibaba.ageiport.processor.core.spi.dispatcher;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author lingyi
 */
@Getter
@Setter
public class RootDispatcherContext {

    private String mainTaskId;

    private Map<String, String> labels;
}
