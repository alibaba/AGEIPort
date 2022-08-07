package com.alibaba.ageiport.processor.core.task.monitor;


import com.alibaba.ageiport.common.function.VoidCallback;
import com.alibaba.ageiport.common.lang.DelayedElement;
import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.DelayQueue;

/**
 * 清理任务
 *
 * @author xuechao.sxc
 */
public class ClearTask {
    private final Logger LOGGER = LoggerFactory.getLogger(ClearTask.class);

    /**
     * 清理队列
     */
    private final DelayQueue<DelayedElement<String>> DELAYED_CLEAN_QUEUE = new DelayQueue<>();

    private final Map<String, VoidCallback> CALLBACK_MAP = new HashMap<>(1 << 5);

    public ClearTask(String name) {
        Thread cleanThread = new Thread(() -> {
            while (true) {
                DelayedElement<String> clearTask = null;
                try {
                    clearTask = DELAYED_CLEAN_QUEUE.take();
                    clear(clearTask.getData());
                } catch (Throwable e) {
                    LOGGER.error("ClearTask#failed, task:{}", clearTask, e);
                }
            }
        });
        cleanThread.setName(name);
        cleanThread.setDaemon(true);
        cleanThread.start();
    }

    /**
     * 添加清理任务
     *
     * @param key
     * @param delay
     * @param callback
     */
    public synchronized void addClearTask(String key, long delay, VoidCallback callback) {
        boolean exist = CALLBACK_MAP.containsKey(key);
        String clearTaskKey = exist ? key + System.nanoTime() : key;

        DelayedElement<String> clearTask = new DelayedElement<>(delay, clearTaskKey);
        DELAYED_CLEAN_QUEUE.add(clearTask);
        CALLBACK_MAP.put(clearTaskKey, callback);
    }

    private void clear(String taskId) {
        VoidCallback callback = CALLBACK_MAP.remove(taskId);
        Optional.ofNullable(callback).ifPresent(VoidCallback::call);
    }

}
