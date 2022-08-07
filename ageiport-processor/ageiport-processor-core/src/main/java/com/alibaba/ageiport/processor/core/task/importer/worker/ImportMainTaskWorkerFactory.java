package com.alibaba.ageiport.processor.core.task.importer.worker;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskWorker;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskWorkerFactory;

/**
 * @author lingyi
 */
public class ImportMainTaskWorkerFactory implements MainTaskWorkerFactory {

    @Override
    public MainTaskWorker create(AgeiPort ageiPort, MainTask mainTask) {
        ImportMainTaskWorker worker = new ImportMainTaskWorker(ageiPort, mainTask);
        return worker;
    }
}
