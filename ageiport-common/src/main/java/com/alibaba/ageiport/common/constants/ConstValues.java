package com.alibaba.ageiport.common.constants;

import java.util.regex.Pattern;

/**
 * Const values
 *
 * @author lingyi
 */
public interface ConstValues {

    Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    String PROJECT_NAME = "AgeiPort";

    String PROJECT_LOWER_KEY = "ageiport";

    String DEFAULT_KEY = "default";

    String REMOVE_PREFIX = "-";

    String DOT = ".";

    String AT = "@";

    String SUB = "-";

    int MAX_TIMEOUT_TIME_MS = 3 * 60 * 60 * 1000;

}
