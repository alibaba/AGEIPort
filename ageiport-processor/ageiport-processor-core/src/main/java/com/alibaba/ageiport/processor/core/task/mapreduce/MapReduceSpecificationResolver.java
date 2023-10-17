package com.alibaba.ageiport.processor.core.task.mapreduce;

import com.alibaba.ageiport.common.feature.FeatureUtils;
import com.alibaba.ageiport.common.utils.TypeUtils;
import com.alibaba.ageiport.processor.core.Processor;
import com.alibaba.ageiport.processor.core.TaskSpec;
import com.alibaba.ageiport.processor.core.annotation.ExportSpecification;
import com.alibaba.ageiport.processor.core.constants.TaskSpecificationFeatureKeys;
import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecification;
import com.alibaba.ageiport.processor.core.spi.task.specification.TaskSpecificationResolver;
import com.alibaba.ageiport.processor.core.task.exporter.ExportProcessor;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskSpecHolder;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author lingyi
 */
public class MapReduceSpecificationResolver<QUERY, DATA, VIEW> implements TaskSpecificationResolver {

    @Override
    public TaskSpec resolve(Processor processor) {
        if (!(processor instanceof ExportProcessor)) {
            return null;
        }

        Class<? extends Processor> processorClass = processor.getClass();
        String handler = processorClass.getName();

        ExportSpecification specAnnotation = processorClass.getAnnotation(ExportSpecification.class);
        String code = specAnnotation.code();
        String name = specAnnotation.name();
        String desc = specAnnotation.desc();
        String type = specAnnotation.type();
        String executeType = specAnnotation.executeType();
        long timeoutMs = specAnnotation.timeoutMs();
        int threshold = specAnnotation.totalThreshold();
        String fileType = specAnnotation.fileType();
        int pageSize = specAnnotation.pageSize();
        String sliceStrategy = specAnnotation.sliceStrategy();

        TaskSpecification taskSpecification = new TaskSpecification();
        taskSpecification.setTaskCode(code);
        taskSpecification.setTaskName(name);
        taskSpecification.setTaskDesc(desc);
        taskSpecification.setTaskType(type);
        taskSpecification.setTaskExecuteType(executeType);
        taskSpecification.setTaskHandler(handler);

        String feature = FeatureUtils.putFeature(null, TaskSpecificationFeatureKeys.TIMEOUT_MS, timeoutMs);
        feature = FeatureUtils.putFeature(feature, TaskSpecificationFeatureKeys.THRESHOLD, threshold);
        feature = FeatureUtils.putFeature(feature, TaskSpecificationFeatureKeys.FILE_TYPE, fileType);
        feature = FeatureUtils.putFeature(feature, TaskSpecificationFeatureKeys.PAGE_SIZE, pageSize);
        feature = FeatureUtils.putFeature(feature, TaskSpecificationFeatureKeys.TASK_SLICE_STRATEGY, sliceStrategy);
        taskSpecification.setFeature(feature);


        List<Type> genericParamType = TypeUtils.getGenericParamType(processorClass, ExportProcessor.class);
        Class<QUERY> queryClass = (Class<QUERY>) genericParamType.get(0);
        Class<DATA> dataClass = genericParamType.size() > 1 ? (Class<DATA>) genericParamType.get(1) : null;
        Class<VIEW> viewClass = genericParamType.size() > 2 ? (Class<VIEW>) genericParamType.get(2) : null;

        ExportTaskSpecHolder<QUERY, DATA, VIEW> holder = new ExportTaskSpecHolder<>();
        holder.setProcessor(processor);
        holder.setTaskSpecification(taskSpecification);
        holder.setQueryClass(queryClass);
        holder.setDataClass(dataClass);
        holder.setViewClass(viewClass);
        holder.setPageSize(pageSize);

        return holder;
    }

}
