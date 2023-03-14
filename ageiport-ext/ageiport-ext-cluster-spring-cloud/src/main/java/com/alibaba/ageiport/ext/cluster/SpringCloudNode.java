package com.alibaba.ageiport.ext.cluster;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author lingyi
 */
@Getter
@Setter
public class SpringCloudNode implements com.alibaba.ageiport.processor.core.spi.cluster.Node {

    private String app;

    private String id;

    private String host;

    private String group;

    private Map<String, String> labels;

    private Map<String, String> attributes;


    public SpringCloudNode() {

    }




}
