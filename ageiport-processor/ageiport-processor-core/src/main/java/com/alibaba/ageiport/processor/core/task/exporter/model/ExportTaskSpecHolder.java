package com.alibaba.ageiport.processor.core.task.exporter.model;

import com.alibaba.ageiport.common.feature.FeatureUtils;
import com.alibaba.ageiport.processor.core.constants.TaskSpecificationFeatureKeys;
import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecHolder;
import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecification;
import com.alibaba.ageiport.processor.core.task.exporter.ExportProcessor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Setter
@Getter
public class ExportTaskSpecHolder<QUERY, DATA, VIEW> extends TaskSpecHolder implements ExportTaskSpecification<QUERY, DATA, VIEW> {

    private Class<QUERY> queryClass;

    private Class<DATA> dataClass;

    private Class<VIEW> viewClass;

    private Integer pageSize;

    private String fileType;

    public ExportProcessor<QUERY, DATA, VIEW> getProcessor() {
        return (ExportProcessor<QUERY, DATA, VIEW>) getProcessor();
    }

    @Override
    public String getFileType() {
        TaskSpecification taskSpecification = getTaskSpecification();
        String feature = taskSpecification.getFeature();
        return FeatureUtils.getFeature(feature, TaskSpecificationFeatureKeys.FILE_TYPE);
    }

}
