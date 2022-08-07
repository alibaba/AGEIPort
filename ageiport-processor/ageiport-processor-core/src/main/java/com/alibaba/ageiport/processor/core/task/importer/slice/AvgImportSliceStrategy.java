package com.alibaba.ageiport.processor.core.task.importer.slice;

import com.alibaba.ageiport.processor.core.spi.file.DataGroup;
import com.alibaba.ageiport.processor.core.task.importer.context.ImportMainTaskContext;
import com.alibaba.ageiport.processor.core.task.importer.model.ImportTaskRuntimeConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingyi
 */
public class AvgImportSliceStrategy<QUERY, DATA, VIEW> implements ImportSliceStrategy<QUERY, DATA, VIEW> {

    @Override
    public List<ImportSlice> slice(ImportMainTaskContext<QUERY, DATA, VIEW> context) {
        List<ImportSlice> result = new ArrayList<>();

        ImportTaskRuntimeConfig runtimeConfig = context.getImportTaskRuntimeConfig();
        Integer pageSize = runtimeConfig.getPageSize();

        DataGroup dataGroup = context.getDataGroup();
        List<DataGroup.Data> data = dataGroup.getData();

        int no = 1;
        for (DataGroup.Data datum : data) {
            List<DataGroup.Item> items = datum.getItems();
            int sliceCount = (items.size() - 1) / pageSize + 1;
            for (int i = 0; i < sliceCount; i++) {
                int fromIndex = i * pageSize;
                int toIndex = Math.min((i + 1) * pageSize, items.size());
                List<DataGroup.Item> subList = items.subList(fromIndex, toIndex);
                List<DataGroup.Item> sliceItemList = new ArrayList<>(subList);

                DataGroup.Data sliceData = new DataGroup.Data();
                sliceData.setName(datum.getName());
                sliceData.setItems(sliceItemList);

                List<DataGroup.Data> sliceDataList = new ArrayList<>();
                sliceDataList.add(sliceData);

                DataGroup sliceDataGroup = new DataGroup();
                sliceDataGroup.setData(sliceDataList);

                ImportSliceImpl importSlice = new ImportSliceImpl();
                importSlice.setNo(no++);
                importSlice.setQueryJson(context.getMainTask().getBizQuery());
                importSlice.setCount(sliceDataList.size());
                importSlice.setPageSize(pageSize);
                importSlice.setDataGroup(sliceDataGroup);
                result.add(importSlice);
            }
        }
        return result;
    }
}
