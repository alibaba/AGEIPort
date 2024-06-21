package com.alibaba.ageiport.processor.core.task.mapreduce;

import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.Processor;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.spi.Adapter;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.mapreduce.adpter.StandardMapReduceProcessorAdapter;

import java.util.List;

public interface MapReduceProcessor<INPUT, SUB_INPUT, SUB_OUTPUT, OUTPUT> extends Processor {
    default String resolver() {
        return "MapReduceSpecificationResolver";
    }

    default Adapter getConcreteAdapter() {
        String name = StandardMapReduceProcessorAdapter.class.getSimpleName();
        return ExtensionLoader.getExtensionLoader(Adapter.class).getExtension(name);
    }

    default BizExportTaskRuntimeConfig taskRuntimeConfig(BizUser user, INPUT INPUT) throws BizException {
        return null;
    }

    INPUT resetInput(BizUser bizUser, INPUT INPUT);

    List<SUB_INPUT> map(BizUser bizUser, INPUT INPUT);

    List<SUB_OUTPUT> execute(BizUser bizUser, INPUT SUB_INPUT);

    List<OUTPUT> reduce(BizUser bizUser, INPUT INPUT, List<SUB_OUTPUT> executeResults);
}
