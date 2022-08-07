package com.alibaba.ageiport.processor.core.test.model;

import com.alibaba.ageiport.common.utils.CollectionUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Query {

    private Integer totalCount = 10000;

    private List<View> checkErrorData;

    private List<View> writeErrorData;


    public Integer getErrorCount() {
        int sum = 0;
        if (CollectionUtils.isNotEmpty(checkErrorData)) {
            sum += checkErrorData.size();
        }
        if (CollectionUtils.isNotEmpty(writeErrorData)) {
            sum += writeErrorData.size();
        }
        return sum;
    }
}
