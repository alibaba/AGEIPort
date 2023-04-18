package com.alibaba.ageiport.processor.core.task.acceptor;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.eventbus.EventBus;
import com.alibaba.ageiport.processor.core.spi.task.acceptor.TaskAcceptor;
import com.alibaba.ageiport.processor.core.spi.task.monitor.TaskStageEvent;
import com.alibaba.ageiport.processor.core.spi.task.stage.MainTaskStageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;

/**
 * @author lingyi
 */
public class DefaultTaskAcceptor implements TaskAcceptor {

    private AgeiPort ageiPort;

    public DefaultTaskAcceptor(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
    }

    @Override
    public void accept(MainTask mainTask) {
        String code = mainTask.getCode();
        TaskSpecificationRegistry registry = ageiPort.getSpecificationRegistry();
        TaskSpec taskSpec = registry.get(code);
        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        MainTaskStageProvider stageProvider = spiSelector.selectExtension(taskSpec.getExecuteType(), taskSpec.getTaskType(), code, MainTaskStageProvider.class);

        Stage mainTaskCreated = stageProvider.mainTaskCreated();
        TaskStageEvent event = TaskStageEvent.mainTaskEvent(mainTask.getMainTaskId(), mainTaskCreated);
        EventBus localEventBus = ageiPort.getLocalEventBus();
        localEventBus.post(event);
    }
}
