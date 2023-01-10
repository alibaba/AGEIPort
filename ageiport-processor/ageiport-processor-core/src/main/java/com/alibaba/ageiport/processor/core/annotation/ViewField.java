package com.alibaba.ageiport.processor.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lingyi
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewField {

    String headerName();

    String type() default "AUTO";

    int index() default 1;

    boolean isDynamicColumn() default false;

    int groupIndex() default -1;

    String groupName() default "";

    boolean isErrorHeader() default false;

    boolean isRequired() default true;

    int columnWidth() default 20;

    String[] values() default {};
}
