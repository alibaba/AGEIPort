package com.alibaba.ageiport.processor.core.spi.cluster;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.AgeiPort;

/**
 * @author lingyi
 */
@SPI
public interface ClusterManagerFactory {

    ClusterManager create(AgeiPort ageiPort, ClusterOptions options);

}
