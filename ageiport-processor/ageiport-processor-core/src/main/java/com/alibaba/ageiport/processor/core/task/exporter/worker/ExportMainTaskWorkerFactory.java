package com.alibaba.ageiport.processor.core.task.exporter.worker;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.model.core.impl.MainTask;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskWorker;
import com.alibaba.ageiport.processor.core.spi.task.factory.MainTaskWorkerFactory;

/**
 * @author lingyi
 */
public class ExportMainTaskWorkerFactory implements MainTaskWorkerFactory {

    @Override
    public MainTaskWorker create(AgeiPort ageiPort, MainTask mainTask) {
        ExportMainTaskWorker worker = new ExportMainTaskWorker(ageiPort, mainTask);
        return worker;
    }
}
