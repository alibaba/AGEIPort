package com.alibaba.ageiport.processor.core.dispatcher.local;

import com.alibaba.ageiport.common.utils.TaskIdUtil;
import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.executor.MainWorkerExecutor;
import com.alibaba.ageiport.processor.core.executor.SubWorkerExecutor;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.client.TaskServerClient;
import com.alibaba.ageiport.processor.core.spi.dispatcher.Dispatcher;
import com.alibaba.ageiport.processor.core.spi.dispatcher.RootDispatcherContext;
import com.alibaba.ageiport.processor.core.spi.dispatcher.SubDispatcherContext;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskWorker;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskWorkerFactory;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskWorker;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskWorkerFactory;
import com.alibaba.ageiport.processor.core.spi.task.selector.TaskSpiSelector;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationRegistry;

import java.util.List;

/**
 * @author lingyi
 */
public class LocalDispatcher implements Dispatcher {

    private AgeiPort ageiPort;

    private LocalDispatcherOptions options;

    public LocalDispatcher(AgeiPort ageiPort, LocalDispatcherOptions options) {
        this.ageiPort = ageiPort;
        this.options = options;
    }

    @Override
    public void dispatchMainTaskPrepare(RootDispatcherContext context) {
        String mainTaskId = context.getMainTaskId();
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(mainTaskId);
        TaskSpecificationRegistry taskSpecificationRegistry = ageiPort.getSpecificationRegistry();
        TaskSpec taskSpec = taskSpecificationRegistry.get(mainTask.getCode());

        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        MainTaskWorkerFactory workerFactory = spiSelector.selectExtension(taskSpec.getExecuteType(), taskSpec.getTaskType(), mainTask.getCode(), MainTaskWorkerFactory.class);

        MainTaskWorker worker = workerFactory.create(ageiPort, mainTask);
        worker.isReduce(false);

        MainWorkerExecutor workerExecutor = ageiPort.getMainWorkerExecutor();
        workerExecutor.submit(worker);
    }

    @Override
    public void dispatchSubTasks(SubDispatcherContext context) {
        String mainTaskId = context.getMainTaskId();
        MainTask mainTask = ageiPort.getTaskServerClient().getMainTask(mainTaskId);
        TaskSpecificationRegistry taskSpecificationRegistry = ageiPort.getSpecificationRegistry();
        TaskSpec taskSpec = taskSpecificationRegistry.get(mainTask.getCode());

        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        SubTaskWorkerFactory workerFactory = spiSelector.selectExtension(taskSpec.getExecuteType(), taskSpec.getTaskType(), mainTask.getCode(), SubTaskWorkerFactory.class);


        List<Integer> subTaskNos = context.getSubTaskNos();
        for (Integer subTaskNo : subTaskNos) {
            String subTaskId = TaskIdUtil.genSubTaskId(mainTaskId, subTaskNo);
            SubTask subTask = ageiPort.getTaskServerClient().getSubTask(subTaskId);
            SubTaskWorker worker = workerFactory.create(ageiPort, subTask);
            SubWorkerExecutor workerExecutor = ageiPort.getSubWorkerExecutor();
            workerExecutor.submit(worker);
        }
    }

    @Override
    public void dispatchMainTaskReduce(RootDispatcherContext context) {
        String mainTaskId = context.getMainTaskId();
        TaskServerClient taskServerClient = ageiPort.getTaskServerClient();
        MainTask mainTask = taskServerClient.getMainTask(mainTaskId);
        TaskSpecificationRegistry taskSpecificationRegistry = ageiPort.getSpecificationRegistry();
        TaskSpec taskSpec = taskSpecificationRegistry.get(mainTask.getCode());

        TaskSpiSelector spiSelector = ageiPort.getTaskSpiSelector();
        MainTaskWorkerFactory workerFactory = spiSelector.selectExtension(taskSpec.getExecuteType(), taskSpec.getTaskType(), mainTask.getCode(), MainTaskWorkerFactory.class);

        MainTaskWorker worker = workerFactory.create(ageiPort, mainTask);
        worker.isReduce(true);

        MainWorkerExecutor workerExecutor = ageiPort.getMainWorkerExecutor();
        workerExecutor.submit(worker);
    }
}
