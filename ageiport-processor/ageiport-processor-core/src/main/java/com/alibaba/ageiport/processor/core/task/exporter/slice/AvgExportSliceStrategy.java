package com.alibaba.ageiport.processor.core.task.exporter.slice;

import com.alibaba.ageiport.processor.core.task.exporter.context.ExportMainTaskContext;
import com.alibaba.ageiport.processor.core.task.exporter.model.ExportTaskRuntimeConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingyi
 */
public class AvgExportSliceStrategy<QUERY, DATA, VIEW> implements ExportSliceStrategy<QUERY, DATA, VIEW> {

    @Override
    public List<ExportSlice> slice(ExportMainTaskContext<QUERY, DATA, VIEW> context) {
        List<ExportSlice> result = new ArrayList<>();

        ExportTaskRuntimeConfig runtimeConfig = context.getExportTaskRuntimeConfig();
        Integer totalCount = runtimeConfig.getTotalCount();
        Integer pageSize = runtimeConfig.getPageSize();

        int sliceCount = totalCount / pageSize;
        if (sliceCount * pageSize < totalCount) {
            sliceCount += 1;
        }

        for (int i = 0; i < sliceCount; i++) {
            ExportSliceImpl slice = new ExportSliceImpl();
            slice.setNo(i + 1);
            slice.setOffset(i * pageSize);
            slice.setSize(pageSize);
            slice.setQueryJson(context.getMainTask().getBizQuery());
            result.add(slice);
        }
        return result;
    }
}
