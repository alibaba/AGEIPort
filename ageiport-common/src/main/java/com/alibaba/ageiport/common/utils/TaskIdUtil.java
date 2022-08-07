package com.alibaba.ageiport.common.utils;

import java.util.Date;
import java.util.UUID;

/**
 * @author lingyi
 */
public class TaskIdUtil {
    public static String genMainTaskId() {
        String date = DateUtils.format(new Date(), DateUtils.PURE_DATETIME_FORMAT);
        String uuid = UUID.randomUUID().toString();
        return date + "-" + uuid.split("-")[0];
    }

    public static String genSubTaskId(String mainTaskId, Integer subTaskNo) {
        return mainTaskId + "_" + subTaskNo;
    }

    public static String getMainTaskId(String subTaskId) {
        int endIndex = subTaskId.lastIndexOf("_");
        return subTaskId.substring(0, endIndex);
    }


}
