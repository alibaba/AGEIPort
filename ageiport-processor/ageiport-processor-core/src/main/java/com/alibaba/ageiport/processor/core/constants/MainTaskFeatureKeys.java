package com.alibaba.ageiport.processor.core.constants;

import com.alibaba.ageiport.common.feature.FeatureKey;

/**
 * @author lingyi
 */
public interface MainTaskFeatureKeys {

    FeatureKey<String> VERSION = FeatureKey.create("version", String.class);

    FeatureKey<String> LABELS = FeatureKey.create("labels", String.class);

    FeatureKey<String> OUTPUT_FILE_KEY = FeatureKey.create("outputFileKey", String.class);

    FeatureKey<String> INPUT_FILE_KEY = FeatureKey.create("inputFileKey", String.class);

    FeatureKey<String> RT_FILE_TYPE_KEY = FeatureKey.create("fileType", String.class);

    FeatureKey<String> RT_COLUMN_HEADERS_KEY = FeatureKey.create("columnHeaders", String.class);

}
