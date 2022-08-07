package com.alibaba.ageiport.common.logger;

import java.io.PrintStream;
import java.util.Calendar;

/**
 * Log's logger
 *
 * @author lingyi
 */
public class LogLog {

    private static final String CLASS_INFO = LogLog.class.getClassLoader().toString();

    protected static boolean debugEnabled = false;
    protected static boolean infoEnabled = true;

    private static boolean quietMode = false;

    private static final String DEBUG_PREFIX = "JM.Log:DEBUG ";
    private static final String INFO_PREFIX = "JM.Log:INFO ";

    private static final String WARN_PREFIX = "JM.Log:WARN ";
    private static final String ERR_PREFIX = "JM.Log:ERROR ";

    public static void setQuietMode(boolean quietMode) {
        LogLog.quietMode = quietMode;
    }

    static public void setInternalDebugging(boolean enabled) {
        debugEnabled = enabled;
    }

    static public void setInternalInfoing(boolean enabled) {
        infoEnabled = enabled;
    }

    public static void debug(String msg) {
        if (debugEnabled && !quietMode) {
            println(System.out, DEBUG_PREFIX + msg);
        }
    }

    public static void debug(String msg, Throwable t) {
        if (debugEnabled && !quietMode) {
            println(System.out, DEBUG_PREFIX + msg);
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }
    }

    public static void info(String msg) {
        if (infoEnabled && !quietMode) {
            println(System.out, INFO_PREFIX + msg);
        }
    }

    public static void info(String msg, Throwable t) {
        if (infoEnabled && !quietMode) {
            println(System.out, INFO_PREFIX + msg);
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }
    }

    public static void error(String msg) {
        if (quietMode) {
            return;
        }

        println(System.err, ERR_PREFIX + msg);
    }

    public static void error(String msg, Throwable t) {
        if (quietMode) {
            return;
        }

        println(System.err, ERR_PREFIX + msg);
        if (t != null) {
            t.printStackTrace();
        }
    }

    public static void warn(String msg) {
        if (quietMode) {
            return;
        }

        println(System.err, WARN_PREFIX + msg);
    }

    public static void warn(String msg, Throwable t) {
        if (quietMode) {
            return;
        }

        println(System.err, WARN_PREFIX + msg);
        if (t != null) {
            t.printStackTrace();
        }
    }

    private static void println(PrintStream out, String msg) {
        out.println(Calendar.getInstance().getTime().toString() + " " + CLASS_INFO + " " + msg);
    }

    private static void outPrintln(PrintStream out, String msg) {
        out.println(Calendar.getInstance().getTime().toString() + " " + CLASS_INFO + " " + msg);
    }
}
