package com.alibaba.ageiport.common.utils;


import com.alibaba.ageiport.common.exception.UtilException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * 数字工具类<br>
 *
 * @author xuechao.sxc
 */
public class NumberUtils {
    private NumberUtils() {
    }

    public static final Integer INTEGER_ZERO = 0;
    public static final Integer INTEGER_ONE = 1;
    public static final Integer INTEGER_TWO = 2;

    /**
     * 是否为数字，支持包括：
     *
     * <pre>
     * 1、10进制
     * 2、16进制数字（0x开头）
     * 3、科学计数法形式（1234E3）
     * 4、类型标识形式（123D）
     * 5、正负数标识形式（+123、-234）
     * </pre>
     *
     * @param str 字符串值
     * @return 是否为数字
     */
    public static boolean isNumber(CharSequence str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        char[] chars = str.toString().toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-' || chars[0] == '+') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && (chars[start + 1] == 'x' || chars[start + 1] == 'X')) {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A'
                        || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (false == foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }

    /**
     * 将指定字符串转换为{@link Number} 对象
     *
     * @param numberStr Number字符串
     * @return Number对象
     */
    public static Number parseNumber(String numberStr) {
        numberStr = removeNumberFlag(numberStr);
        try {
            return NumberFormat.getInstance().parse(numberStr);
        } catch (ParseException e) {
            throw new UtilException("parseNumber exception for:" + numberStr, e);
        }
    }

    public static Number parseNumber(String text, Class targetClass) {
        if (text == null) {
            return null;
        }
        if (Long.class.isAssignableFrom(targetClass) || long.class.equals(targetClass)) {
            return toLong(text);
        }
        if (Double.class.isAssignableFrom(targetClass) || double.class.equals(targetClass)) {
            return toDouble(text);
        }
        if (Integer.class.isAssignableFrom(targetClass) || int.class.equals(targetClass)) {
            return toInt(text);
        }
        if (Short.class.isAssignableFrom(targetClass) || short.class.equals(targetClass)) {
            return toShort(text);
        }
        if (Float.class.isAssignableFrom(targetClass) || float.class.equals(targetClass)) {
            return toFloat(text);
        }
        if (Byte.class.isAssignableFrom(targetClass) || byte.class.equals(targetClass)) {
            return toByte(text);
        }
        if (BigDecimal.class.isAssignableFrom(targetClass)) {
            toScaledBigDecimal(text);
        }
        throw new UtilException("invalid number class:" + targetClass);
    }

    public static boolean isDecimal(Object cs) {
        if (cs == null || cs.toString().length() == 0) {
            return false;
        } else {
            String s = cs.toString();
            int sz = s.length();

            int dotCount = 0;
            int numCount = 0;
            for (int i = 0; i < sz; ++i) {
                if (i != 0 && i != sz - 1 && s.charAt(i) == '.') {
                    dotCount++;
                }
                if (Character.isDigit(s.charAt(i))) {
                    numCount++;
                }
            }
            return numCount == sz - 1 && dotCount == 1;
        }
    }

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException var3) {
                return defaultValue;
            }
        }
    }

    public static long toLong(String str) {
        return toLong(str, 0L);
    }

    public static long toLong(String str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException var4) {
                return defaultValue;
            }
        }
    }

    public static float toFloat(String str) {
        return toFloat(str, 0.0F);
    }

    public static float toFloat(String str, float defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return Float.parseFloat(str);
            } catch (NumberFormatException var3) {
                return defaultValue;
            }
        }
    }

    public static double toDouble(String str) {
        return toDouble(str, 0.0D);
    }

    public static double toDouble(String str, double defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException var4) {
                return defaultValue;
            }
        }
    }

    public static double toDouble(BigDecimal value) {
        return toDouble(value, 0.0D);
    }

    public static double toDouble(BigDecimal value, double defaultValue) {
        return value == null ? defaultValue : value.doubleValue();
    }

    public static byte toByte(String str) {
        return toByte(str, (byte) 0);
    }

    public static byte toByte(String str, byte defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return Byte.parseByte(str);
            } catch (NumberFormatException var3) {
                return defaultValue;
            }
        }
    }

    public static short toShort(String str) {
        return toShort(str, (short) 0);
    }

    public static short toShort(String str, short defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return Short.parseShort(str);
            } catch (NumberFormatException var3) {
                return defaultValue;
            }
        }
    }

    public static BigDecimal toScaledBigDecimal(BigDecimal value) {
        return toScaledBigDecimal(value, INTEGER_TWO, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal toScaledBigDecimal(BigDecimal value, int scale, RoundingMode roundingMode) {
        return value == null ? BigDecimal.ZERO : value.setScale(scale,
            roundingMode == null ? RoundingMode.HALF_EVEN : roundingMode);
    }

    public static BigDecimal toScaledBigDecimal(Float value) {
        return toScaledBigDecimal(value, INTEGER_TWO, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal toScaledBigDecimal(Float value, int scale, RoundingMode roundingMode) {
        return value == null ? BigDecimal.ZERO : toScaledBigDecimal(BigDecimal.valueOf((double) value), scale,
            roundingMode);
    }

    public static BigDecimal toScaledBigDecimal(Double value) {
        return toScaledBigDecimal(value, INTEGER_TWO, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal toScaledBigDecimal(Double value, int scale, RoundingMode roundingMode) {
        return value == null ? BigDecimal.ZERO : toScaledBigDecimal(BigDecimal.valueOf(value), scale, roundingMode);
    }

    public static BigDecimal toScaledBigDecimal(String value) {
        return toScaledBigDecimal(value, INTEGER_TWO, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal toScaledBigDecimal(String value, int scale, RoundingMode roundingMode) {
        return value == null ? BigDecimal.ZERO : toScaledBigDecimal(createBigDecimal(value), scale, roundingMode);
    }

    public static BigDecimal createBigDecimal(String str) {
        if (str == null) {
            return null;
        } else if (StringUtils.isBlank(str)) {
            throw new NumberFormatException("A blank string is not a valid number");
        } else if (str.trim().startsWith("--")) {
            throw new NumberFormatException(str + " is not a valid number.");
        } else {
            return new BigDecimal(str);
        }
    }

    /**
     * 去掉数字尾部的数字标识，例如12D，44.0F，22L中的最后一个字母
     *
     * @param number 数字字符串
     * @return 去掉标识的字符串
     */
    private static String removeNumberFlag(String number) {
        // 去掉类型标识的结尾
        final int lastPos = number.length() - 1;
        final char lastCharUpper = Character.toUpperCase(number.charAt(lastPos));
        if ('D' == lastCharUpper || 'L' == lastCharUpper || 'F' == lastCharUpper) {
            number = StringUtils.subPre(number, lastPos);
        }
        return number;
    }

    /**
     * 解析转换数字字符串为int型数字，规则如下：
     *
     * <pre>
     * 1、0x开头的视为16进制数字
     * 2、0开头的视为8进制数字
     * 3、其它情况按照10进制转换
     * 4、空串返回0
     * 5、.123形式返回0（按照小于0的小数对待）
     * 6、123.56截取小数点之前的数字，忽略小数部分
     * </pre>
     *
     * @param number 数字，支持0x开头、0开头和普通十进制
     * @return int
     * @throws NumberFormatException 数字格式异常
     * @since 4.1.4
     */
    public static int parseInt(String number) throws NumberFormatException {
        if (StringUtils.isBlank(number)) {
            return 0;
        }

        // 对于带小数转换为整数采取去掉小数的策略
        number = StringUtils.subBefore(number, CharUtils.DOT, false);
        if (StringUtils.isEmpty(number)) {
            return 0;
        }

        if (StringUtils.startWithIgnoreCase(number, "0x")) {
            // 0x04表示16进制数
            return Integer.parseInt(number.substring(2), 16);
        }

        return Integer.parseInt(removeNumberFlag(number));
    }

    /**
     * 解析转换数字字符串为long型数字，规则如下：
     *
     * <pre>
     * 1、0x开头的视为16进制数字
     * 2、0开头的视为8进制数字
     * 3、空串返回0
     * 4、其它情况按照10进制转换
     * </pre>
     *
     * @param number 数字，支持0x开头、0开头和普通十进制
     * @return long
     */
    public static long parseLong(String number) {
        if (StringUtils.isBlank(number)) {
            return 0;
        }

        // 对于带小数转换为整数采取去掉小数的策略
        number = StringUtils.subBefore(number, CharUtils.DOT, false);
        if (StringUtils.isEmpty(number)) {
            return 0;
        }

        if (number.startsWith("0x")) {
            // 0x04表示16进制数
            return Long.parseLong(number.substring(2), 16);
        }

        return Long.parseLong(removeNumberFlag(number));
    }

}
