package com.alibaba.ageiport.processor.core.spi.task.monitor;

import com.alibaba.ageiport.processor.core.spi.task.stage.Stage;

/**
 * @author lingyi
 */
public interface TaskProgressMonitor {
    void onMainTaskChanged(MainTaskProgress mainTaskProgress, Stage oldStage, Stage newStage);

    void onSubTaskChanged(MainTaskProgress mainTaskProgress, SubTaskProgress subTaskProgress, Stage oldStage, Stage newStage, Stage mainTaskStage);

}
