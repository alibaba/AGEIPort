package com.alibaba.ageiport.processor.core.spi.task.monitor;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.eventbus.local.async.Subscribe;
import com.alibaba.ageiport.processor.core.spi.listener.ManageableListener;

/**
 * @author lingyi
 */
public class TaskStageEventListener implements ManageableListener<TaskStageEvent> {



    private AgeiPort ageiPort;

    @Subscribe
    @Override
    public void handle(TaskStageEvent event) {
        ageiPort.getTaskProgressService().updateTaskProgress(event);
    }

    @Override
    public void startListen(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        ageiPort.getEventBusManager().getEventBus(ExecuteType.STANDALONE).register(this);
        ageiPort.getEventBusManager().getEventBus(ExecuteType.CLUSTER).register(this);
    }
}
