package com.alibaba.ageiport.processor.core.task.mapreduce;

import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.processor.core.Processor;
import com.alibaba.ageiport.processor.core.exception.BizException;
import com.alibaba.ageiport.processor.core.model.api.BizUser;
import com.alibaba.ageiport.processor.core.spi.Adapter;
import com.alibaba.ageiport.processor.core.task.exporter.api.BizExportTaskRuntimeConfig;
import com.alibaba.ageiport.processor.core.task.mapreduce.adpter.StandardMapReduceProcessorAdapter;

import java.util.List;

public interface MapReduceProcessor<QUERY, DATA, RESULT> extends Processor {
    default String resolver() {
        return "MapReduceSpecificationResolver";
    }

    default Adapter getConcreteAdapter() {
        String name = StandardMapReduceProcessorAdapter.class.getSimpleName();
        return ExtensionLoader.getExtensionLoader(Adapter.class).getExtension(name);
    }

    default BizExportTaskRuntimeConfig taskRuntimeConfig(BizUser user, QUERY query) throws BizException {
        return null;
    }

    QUERY resetQuery(BizUser bizUser, QUERY query);

    List<QUERY> map(BizUser bizUser, QUERY query);

    List<DATA> execute(BizUser bizUser, QUERY query);

    List<RESULT> reduce(BizUser bizUser, QUERY query, List<DATA> executeResults);
}
