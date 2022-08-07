package com.alibaba.ageiport.processor.core.dispatcher.http;


import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.StringUtils;
import com.alibaba.ageiport.processor.core.spi.dispatcher.SubDispatcherContext;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Queue for dispatch slice task
 *
 * @author lingyi
 **/
public class DispatchQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatchQueue.class);

    private static final int THRESHOLD = 1024 * 8;

    private ArrayBlockingQueue<SubDispatcherContext> queue = new ArrayBlockingQueue<>(THRESHOLD);

    public SubDispatcherContext get() {
        try {
            return queue.take();
        } catch (Throwable e) {
            LOGGER.error("get failed, ", e);
            throw new RuntimeException(e);
        }
    }

    public void add(SubDispatcherContext dispatchSlice) {
        try {
            queue.add(dispatchSlice);
        } catch (Throwable e) {
            LOGGER.error("add failed, ", e);
            throw new RuntimeException(e);
        }
    }

    public void add(Collection<SubDispatcherContext> subDispatcherContexts) {
        int curQueueSize = queue.size();
        if (curQueueSize + subDispatcherContexts.size() > THRESHOLD) {
            String format = StringUtils.format("addAll failed, max:{}, current:{}, addAll:{}", THRESHOLD, curQueueSize, subDispatcherContexts.size());
            LOGGER.error(format);
            throw new RuntimeException(format);
        }
        for (SubDispatcherContext subDispatchPayload : subDispatcherContexts) {
            this.add(subDispatchPayload);
        }
    }

}