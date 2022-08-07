package com.alibaba.ageiport.processor.core.cache;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.cache.BigDataCache;
import com.alibaba.ageiport.processor.core.task.monitor.ClearTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingyi
 */
public class LocalMemoryBigDataCache implements BigDataCache {

    private AgeiPort ageiPort;

    private Integer timeoutMs;

    private ClearTask clearTask;

    private Map<String, Object> cache;

    @Override

    public void init(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        this.cache = new ConcurrentHashMap<>();
        this.timeoutMs = ageiPort.getOptions().getBigDataCacheExpireMs();
        this.clearTask = new ClearTask("LocalBigDataCache Cache Task");
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return (T) cache.get(key);
    }

    @Override
    public <T> T remove(String key, Class<T> clazz) {
        return (T) cache.remove(key);
    }

    @Override
    public boolean exist(String key) {
        return cache.containsKey(key);
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
        clearTask.addClearTask(key, timeoutMs, () -> cache.remove(key));
    }
}
