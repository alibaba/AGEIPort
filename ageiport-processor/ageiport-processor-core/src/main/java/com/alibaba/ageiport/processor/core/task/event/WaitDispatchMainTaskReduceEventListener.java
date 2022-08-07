package com.alibaba.ageiport.processor.core.task.event;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.eventbus.local.async.Subscribe;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.dispatcher.Dispatcher;
import com.alibaba.ageiport.processor.core.spi.dispatcher.RootDispatcherContext;
import com.alibaba.ageiport.processor.core.spi.listener.ManageableListener;

import java.util.HashMap;

/**
 * @author lingyi
 */
public class WaitDispatchMainTaskReduceEventListener implements ManageableListener<WaitDispatchMainTaskReduceEvent> {

    private AgeiPort ageiPort;

    @Override
    public void startListen(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        this.ageiPort.getLocalEventBus().register(this);
    }

    @Subscribe
    @Override
    public void handle(WaitDispatchMainTaskReduceEvent event) {
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(event.getMainTaskId());
        Dispatcher dispatcher = ageiPort.getDispatcherManager().getDispatcher(mainTask.getExecuteType());
        RootDispatcherContext context = new RootDispatcherContext();
        context.setMainTaskId(mainTask.getMainTaskId());
        context.setLabels(new HashMap<>());
        dispatcher.dispatchMainTaskReduce(context);
    }
}
