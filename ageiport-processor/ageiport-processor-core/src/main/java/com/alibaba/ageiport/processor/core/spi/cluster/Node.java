package com.alibaba.ageiport.processor.core.spi.cluster;

import java.util.Map;

/**
 * @author lingyi
 */
public interface Node {

    String getId();

    String getIp();

    String getGroup();

    Map<String, String> getLabels();

    Map<String, Object> getAttributes();
}
