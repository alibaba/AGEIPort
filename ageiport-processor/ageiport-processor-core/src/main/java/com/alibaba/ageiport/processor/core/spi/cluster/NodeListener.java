package com.alibaba.ageiport.processor.core.spi.cluster;

/**
 * @author lingyi
 */
public interface NodeListener {

    void onNodeAdded(Node node);

    void onNodeLeft(Node node);
}
