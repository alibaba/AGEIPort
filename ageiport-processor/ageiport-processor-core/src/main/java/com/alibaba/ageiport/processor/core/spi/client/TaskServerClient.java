package com.alibaba.ageiport.processor.core.spi.client;

import com.alibaba.ageiport.ext.arch.SPI;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecification;

import java.util.List;

/**
 * @author lingyi
 */
@SPI
public interface TaskServerClient {

    String createMainTask(CreateMainTaskRequest request);

    void updateMainTask(MainTask mainTask);


    MainTask getMainTask(String mainTaskId);

    List<String> createSubTask(CreateSubTasksRequest request);

    void updateSubTask(SubTask subTaskId);


    SubTask getSubTask(String subTaskId);

    TaskSpecification getTaskSpecification(String taskCode);

    String createTaskSpecification(CreateSpecificationRequest request);
}
