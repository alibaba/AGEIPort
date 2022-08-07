package com.alibaba.ageiport.processor.core.spi.cache;

import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.AgeiPortOptions;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;

/**
 * @author lingyi
 */
public class BigDataCacheManager {

    private AgeiPort ageiPort;

    private BigDataCache localBigDataCache;

    private BigDataCache clusterBigDataCache;

    public BigDataCacheManager(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        AgeiPortOptions options = ageiPort.getOptions();
        this.localBigDataCache = ExtensionLoader.getExtensionLoader(BigDataCache.class).getExtension(options.getLocalBigDataCache());
        this.localBigDataCache.init(ageiPort);
        this.clusterBigDataCache = ExtensionLoader.getExtensionLoader(BigDataCache.class).getExtension(options.getClusterBigDataCache());
        this.clusterBigDataCache.init(ageiPort);
    }

    public BigDataCache getBigDataCacheCache(String type) {
        if (ExecuteType.STANDALONE.equals(type)) {
            return localBigDataCache;
        } else {
            return clusterBigDataCache;
        }
    }
}
