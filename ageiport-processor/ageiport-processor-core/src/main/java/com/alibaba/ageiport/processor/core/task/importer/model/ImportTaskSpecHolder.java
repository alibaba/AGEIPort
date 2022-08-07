package com.alibaba.ageiport.processor.core.task.importer.model;

import com.alibaba.ageiport.common.feature.FeatureUtils;
import com.alibaba.ageiport.processor.core.constants.TaskSpecificationFeatureKeys;
import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecHolder;
import com.alibaba.ageiport.processor.core.model.core.impl.TaskSpecification;
import com.alibaba.ageiport.processor.core.task.importer.ImportProcessor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingyi
 */
@Setter
@Getter
public class ImportTaskSpecHolder<QUERY, DATA, VIEW> extends TaskSpecHolder implements ImportTaskSpecification<QUERY, DATA, VIEW> {

    private Class<QUERY> queryClass;

    private Class<DATA> dataClass;

    private Class<VIEW> viewClass;

    private Integer pageSize;

    private String fileType;

    public ImportProcessor<QUERY, DATA, VIEW> getImportProcessor() {
        return (ImportProcessor<QUERY, DATA, VIEW>) getProcessor();
    }

    @Override
    public String getFileType() {
        TaskSpecification taskSpecification = getTaskSpecification();
        String feature = taskSpecification.getFeature();
        return FeatureUtils.getFeature(feature, TaskSpecificationFeatureKeys.FILE_TYPE);
    }

}
