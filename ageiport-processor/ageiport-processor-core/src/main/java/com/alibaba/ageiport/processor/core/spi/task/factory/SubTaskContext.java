package com.alibaba.ageiport.processor.core.spi.task.factory;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.task.stage.StageProvider;
import com.alibaba.ageiport.processor.core.spi.task.stage.SubTaskStageProvider;

/**
 * @author lingyi
 */
public interface SubTaskContext extends TaskContext {

    SubTaskStageProvider getSubTaskStageProvider();

    default StageProvider getStageProvider() {
        return getSubTaskStageProvider();
    }

    default void save() {
        SubTask subTask = getSubTask();
        AgeiPort ageiPort = getAgeiPort();
        TaskServerClient client = ageiPort.getTaskServerClient();
        client.updateSubTask(subTask);
    }

}
