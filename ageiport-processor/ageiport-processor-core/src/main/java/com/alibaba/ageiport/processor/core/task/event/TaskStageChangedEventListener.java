package com.alibaba.ageiport.processor.core.task.event;

import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.eventbus.local.async.Subscribe;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.listener.ManageableListener;

/**
 * @author lingyi
 */
public class TaskStageChangedEventListener implements ManageableListener<TaskStageChangedEvent> {

    public static Logger log = LoggerFactory.getLogger(TaskStageChangedEventListener.class);

    private EventBus eventBus;

    @Override
    public void startListen(AgeiPort ageiPort) {
        this.eventBus = ageiPort.getEventBusManager().getEventBus(ExecuteType.STANDALONE);
        eventBus.register(this);
    }


    @Subscribe
    @Override
    public void handle(TaskStageChangedEvent event) {

    }
}
