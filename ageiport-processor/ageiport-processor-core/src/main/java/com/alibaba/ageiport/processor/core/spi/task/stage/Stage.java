package com.alibaba.ageiport.processor.core.spi.task.stage;

import java.util.EventObject;

/**
 * @author lingyi
 */
public interface Stage extends Comparable<Stage> {

    String getGroup();

    Integer getOrder();

    String getCode();

    String getName();

    String getDescription();

    Double getMinPercent();

    Double getMaxPercent();

    Class<? extends EventObject> getTriggerEvent();

    StageProvider getStageProvider();

    Stage next();

    Stage pre();

    boolean isAfterThan(String code);

    boolean isBeforeThan(String code);

    boolean isFinal();

    boolean isFinished();

    boolean isError();

}
