package com.alibaba.ageiport.processor.core.spi.task.stage;

import java.util.List;

/**
 * @author lingyi
 */
public interface StageProvider {
    List<Stage> getStages();

    Stage getStage(String code);


    Stage getStage(Integer order);
}
