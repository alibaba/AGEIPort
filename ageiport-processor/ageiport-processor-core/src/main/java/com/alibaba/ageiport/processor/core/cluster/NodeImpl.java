package com.alibaba.ageiport.processor.core.cluster;

import com.alibaba.ageiport.processor.core.spi.cluster.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author lingyi
 */
@Getter
@Setter
public class NodeImpl implements Node {

    private String id;

    private String ip;

    private String group;

    private Map<String, String> labels;

    private Map<String, Object> attributes;


    public NodeImpl() {

    }

    public NodeImpl(String id, String ip, String group, Map<String, String> labels, Map<String, Object> attributes) {
        this.id = id;
        this.ip = ip;
        this.group = group;
        this.labels = labels;
        this.attributes = attributes;
    }
}
