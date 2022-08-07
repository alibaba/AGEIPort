package com.alibaba.ageiport.processor.core.constants;

import com.alibaba.ageiport.common.feature.FeatureKey;

/**
 * @author lingyi
 */
public interface TaskSpecificationFeatureKeys {

    FeatureKey<Long> TIMEOUT_MS = FeatureKey.create("timeoutMs", Long.class);
    FeatureKey<Integer> THRESHOLD = FeatureKey.create("threshold", Integer.class);
    FeatureKey<String> FILE_TYPE = FeatureKey.create("fileType", String.class);

    FeatureKey<Integer> PAGE_SIZE = FeatureKey.create("pageSize", Integer.class);

    FeatureKey<String> TASK_SLICE_STRATEGY = FeatureKey.create("taskSliceStrategy", String.class);

}
