package com.alibaba.ageiport.processor.core.spi.dispatcher;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
@Getter
@Setter
public class SubDispatcherContext {

    private String mainTaskId;

    private List<Integer> subTaskNos;

    private Map<String, String> labels;
}
