package com.alibaba.ageiport.processor.core.task.event;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.eventbus.local.async.Subscribe;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.dispatcher.Dispatcher;
import com.alibaba.ageiport.processor.core.spi.dispatcher.RootDispatcherContext;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.listener.ManageableListener;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;

/**
 * @author lingyi
 */
public class WaitDispatchMainTaskPrepareEventListener implements ManageableListener<WaitDispatchMainTaskPrepareEvent> {

    private AgeiPort ageiPort;

    private EventBus eventBus;

    @Override
    public void startListen(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
        this.eventBus = ageiPort.getEventBusManager().getEventBus(ExecuteType.STANDALONE);
        eventBus.register(this);
    }


    @Subscribe
    @Override
    public void handle(WaitDispatchMainTaskPrepareEvent event) {
        String mainTaskId = event.getMainTaskId();
        TaskServerClient client = ageiPort.getTaskServerClient();
        MainTask mainTask = client.getMainTask(mainTaskId);

        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        MainTaskStageProvider stageProvider = spiSelector.selectExtension(mainTask.getExecuteType(), mainTask.getType(), mainTask.getCode(), MainTaskStageProvider.class);

        EventBus bus = ageiPort.getEventBusManager().getEventBus(mainTask.getExecuteType());
        bus.post(TaskStageEvent.mainTaskEvent(mainTaskId, stageProvider.mainTaskDispatchStart()));
        long startTime = System.currentTimeMillis();

        Dispatcher dispatcher = ageiPort.getDispatcherManager().getDispatcher(mainTask.getExecuteType());

        RootDispatcherContext dispatcherContext = new RootDispatcherContext();
        dispatcherContext.setMainTaskId(mainTaskId);
        dispatcher.dispatchMainTaskPrepare(dispatcherContext);

        long costTime = System.currentTimeMillis() - startTime;
        bus.post(TaskStageEvent.mainTaskEvent(mainTaskId, stageProvider.mainTaskDispatchEnd(), costTime));
    }


}
