package com.alibaba.ageiport.test.processor.core.strategy.slice;

import com.alibaba.ageiport.common.utils.JsonUtil;
import com.alibaba.ageiport.processor.core.task.exporter.context.ExportMainTaskContext;
import com.alibaba.ageiport.processor.core.task.exporter.slice.ExportSlice;
import com.alibaba.ageiport.processor.core.task.exporter.slice.ExportSliceImpl;
import com.alibaba.ageiport.processor.core.task.exporter.slice.ExportSliceStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingyi
 */
public class UdfExportSliceStrategy<QUERY, DATA, VIEW> implements ExportSliceStrategy<QUERY, DATA, VIEW> {

    @Override
    public List<ExportSlice> slice(ExportMainTaskContext<QUERY, DATA, VIEW> context) {
        List<ExportSlice> result = new ArrayList<>();

        //可能从某些配置或数据源查询出的分片
        Map<String, Integer> genderCountMap = new HashMap<>();
        genderCountMap.put("男", 3000);
        genderCountMap.put("女", 2000);
        genderCountMap.put("其他", 1000);
        int pageSize = 1000;
        int no = 1;
        for (Map.Entry<String, Integer> entry : genderCountMap.entrySet()) {
            Integer count = entry.getValue();
            String gender = entry.getKey();
            int sliceCount = count / 1000;
            if (sliceCount * pageSize < count) {
                sliceCount += 1;
            }

            int offset = 0;
            for (int i = 0; i < sliceCount; i++) {
                ExportSliceImpl slice = new ExportSliceImpl();
                slice.setNo(no++);
                slice.setOffset(offset * pageSize);
                offset++;
                slice.setSize(pageSize);
                String bizQuery = context.getMainTask().getBizQuery();
                Map map = JsonUtil.toMap(bizQuery);
                map.put("sliceKey", gender);
                map.put("sliceOffset", i * pageSize);
                slice.setQueryJson(JsonUtil.toJsonString(map));
                result.add(slice);
            }
        }

        return result;
    }
}