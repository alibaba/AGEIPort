package com.alibaba.ageiport.processor.core.task.exporter.worker;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.SubTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskWorker;
import com.alibaba.ageiport.processor.core.spi.task.factory.SubTaskWorkerFactory;

/**
 * @author lingyi
 */
public class ExportSubTaskWorkerFactory implements SubTaskWorkerFactory {
    @Override
    public SubTaskWorker create(AgeiPort ageiPort, SubTask subTask) {
        ExportSubTaskWorker worker = new ExportSubTaskWorker();
        worker.setAgeiPort(ageiPort);
        worker.setSubTask(subTask);
        return worker;
    }
}
