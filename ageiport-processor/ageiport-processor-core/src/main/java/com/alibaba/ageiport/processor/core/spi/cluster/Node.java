package com.alibaba.ageiport.processor.core.spi.cluster;

import java.util.Map;

/**
 * @author lingyi
 */
public interface Node {

    String getApp();

    String getId();

    String getHost();

    String getGroup();

    Map<String, String> getLabels();

    Map<String, String> getAttributes();
}
