package com.alibaba.ageiport.processor.core.spi.task.stage;

/**
 * @author lingyi
 */
public class CommonStage {

    public static Stage START = new StageImpl("GROUP", Integer.MIN_VALUE, "START", "执行失败", 0.00, 0.00, "", false, false, false);

    public static Stage ERROR = new StageImpl("GROUP", Integer.MAX_VALUE, "ERROR", "执行失败", 1.00, 1.00, "", true, false, true);

    public static Stage FINISHED = new StageImpl("GROUP", Integer.MAX_VALUE, "FINISHED", "执行完成", 1.00, 1.00, "", true, false, true);
}
