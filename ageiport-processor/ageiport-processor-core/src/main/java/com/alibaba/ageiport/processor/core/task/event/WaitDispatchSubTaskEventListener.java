package com.alibaba.ageiport.processor.core.task.event;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.constants.ExecuteType;
import com.alibaba.ageiport.processor.core.eventbus.local.async.Subscribe;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.dispatcher.Dispatcher;
import com.alibaba.ageiport.processor.core.spi.dispatcher.SubDispatcherContext;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.listener.ManageableListener;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingyi
 */
public class WaitDispatchSubTaskEventListener implements ManageableListener<WaitDispatchSubTaskEvent> {

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
    public void handle(WaitDispatchSubTaskEvent event) {
        String mainTaskId = event.getMainTaskId();
        TaskServerClient client = ageiPort.getTaskServerClient();
        MainTask mainTask = client.getMainTask(mainTaskId);

        String executeType = mainTask.getExecuteType();
        String taskType = mainTask.getType();
        String taskCode = mainTask.getCode();

        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();

        MainTaskStageProvider stageProvider = spiSelector.selectExtension(executeType, taskType, taskCode, MainTaskStageProvider.class);

        EventBus bus = ageiPort.getEventBusManager().getEventBus(executeType);
        bus.post(TaskStageEvent.mainTaskEvent(mainTaskId, stageProvider.subTaskDispatchStart()));
        long startTime = System.currentTimeMillis();

        Dispatcher dispatcher = ageiPort.getDispatcherManager().getDispatcher(executeType);
        List<Integer> nos = new ArrayList<>();
        for (int i = 1; i <= mainTask.getSubTotalCount(); i++) {
            nos.add(i);
        }
        SubDispatcherContext dispatcherContext = new SubDispatcherContext();
        dispatcherContext.setMainTaskId(mainTaskId);
        dispatcherContext.setSubTaskNos(nos);

        dispatcher.dispatchSubTasks(dispatcherContext);

        long costTime = System.currentTimeMillis() - startTime;
        bus.post(TaskStageEvent.mainTaskEvent(mainTaskId, stageProvider.subTaskDispatchEnd(), costTime));
        bus.post(TaskStageEvent.mainTaskEvent(mainTaskId, stageProvider.subTaskExecuteStart(), costTime));
    }
}
