package com.alibaba.ageiport.processor.core.spi.task.stage;

import java.util.Objects;

/**
 * @author lingyi
 */
public class CommonStage {

    public static Stage START = new StageImpl("GROUP", Integer.MIN_VALUE, "START", "执行开始", 0.00, 0.00, "", false, false, false);

    public static Stage ERROR = new StageImpl("GROUP", Integer.MAX_VALUE, "ERROR", "执行失败", 1.00, 1.00, "", true, false, true);

    public static Stage FINISHED = new StageImpl("GROUP", Integer.MAX_VALUE, "FINISHED", "执行完成", 1.00, 1.00, "", true, false, true);

    public static Stage of(String code){
        if (code == null) {
            return null;
        }
        if (Objects.equals(code, START.getCode())) {
            return START;
        }
        if (Objects.equals(code, ERROR.getCode())) {
            return ERROR;
        }
        if (Objects.equals(code, FINISHED.getCode())) {
            return FINISHED;
        }

        return null;

    }
}
