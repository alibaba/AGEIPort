package com.alibaba.ageiport.processor.core.annotation;

import com.alibaba.ageiport.processor.core.constants.ExecuteType;

import java.lang.annotation.*;

/**
 * @author lingyi
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExportSpecification {

    String code();

    String name();

    String desc() default "";

    String type() default "EXPORT";

    String executeType() default ExecuteType.STANDALONE;

    long timeoutMs() default 60 * 60 * 1000;

    int totalThreshold() default 100_0000;

    String fileType() default "xlsx";

    int pageSize() default 1000;

    String sliceStrategy() default "AvgExportSliceStrategy";
}
