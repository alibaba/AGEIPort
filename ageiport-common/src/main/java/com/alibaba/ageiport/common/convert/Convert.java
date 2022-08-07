package com.alibaba.ageiport.common.convert;


import com.alibaba.ageiport.common.lang.TypeReference;
import com.alibaba.ageiport.common.utils.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 类型转换器
 *
 * @author xuechao.sxc
 */
public class Convert {

    /**
     * 转换为字符串<br>
     * 如果给定的值为null，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static String toStr(Object value, String defaultValue) {
        return convertQuietly(String.class, value, defaultValue);
    }

    /**
     * 转换为字符串<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static String toStr(Object value) {
        return toStr(value, null);
    }

    /**
     * 转换为String数组
     *
     * @param value 被转换的值
     * @return String数组
     */
    public static String[] toStrArray(Object value) {
        return convert(String[].class, value);
    }

    /**
     * 转换为字符<br>
     * 如果给定的值为null，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Character toChar(Object value, Character defaultValue) {
        return convertQuietly(Character.class, value, defaultValue);
    }

    /**
     * 转换为字符<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Character toChar(Object value) {
        return toChar(value, null);
    }

    /**
     * 转换为Character数组
     *
     * @param value 被转换的值
     * @return Character数组
     */
    public static Character[] toCharArray(Object value) {
        return convert(Character[].class, value);
    }

    /**
     * 转换为byte<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Byte toByte(Object value, Byte defaultValue) {
        return convertQuietly(Byte.class, value, defaultValue);
    }

    /**
     * 转换为byte<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Byte toByte(Object value) {
        return toByte(value, null);
    }

    /**
     * 转换为Byte数组
     *
     * @param value 被转换的值
     * @return Byte数组
     */
    public static Byte[] toByteArray(Object value) {
        return convert(Byte[].class, value);
    }

    /**
     * 转换为Byte数组
     *
     * @param value 被转换的值
     * @return Byte数组
     */
    public static byte[] toPrimitiveByteArray(Object value) {
        return convert(byte[].class, value);
    }

    /**
     * 转换为Short<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Short toShort(Object value, Short defaultValue) {
        return convertQuietly(Short.class, value, defaultValue);
    }

    /**
     * 转换为Short<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Short toShort(Object value) {
        return toShort(value, null);
    }

    /**
     * 转换为Short数组
     *
     * @param value 被转换的值
     * @return Short数组
     */
    public static Short[] toShortArray(Object value) {
        return convert(Short[].class, value);
    }

    /**
     * 转换为Number<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Number toNumber(Object value, Number defaultValue) {
        return convertQuietly(Number.class, value, defaultValue);
    }

    /**
     * 转换为Number<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Number toNumber(Object value) {
        return toNumber(value, null);
    }

    /**
     * 转换为Number数组
     *
     * @param value 被转换的值
     * @return Number数组
     */
    public static Number[] toNumberArray(Object value) {
        return convert(Number[].class, value);
    }

    /**
     * 转换为int<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Integer toInt(Object value, Integer defaultValue) {
        return convertQuietly(Integer.class, value, defaultValue);
    }

    /**
     * 转换为int<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Integer toInt(Object value) {
        return toInt(value, null);
    }

    /**
     * 转换为Integer数组<br>
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Integer[] toIntArray(Object value) {
        return convert(Integer[].class, value);
    }

    /**
     * 转换为long<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Long toLong(Object value, Long defaultValue) {
        return convertQuietly(Long.class, value, defaultValue);
    }

    /**
     * 转换为long<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Long toLong(Object value) {
        return toLong(value, null);
    }

    /**
     * 转换为Long数组<br>
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Long[] toLongArray(Object value) {
        return convert(Long[].class, value);
    }

    /**
     * 转换为double<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Double toDouble(Object value, Double defaultValue) {
        return convertQuietly(Double.class, value, defaultValue);
    }

    /**
     * 转换为double<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Double toDouble(Object value) {
        return toDouble(value, null);
    }

    /**
     * 转换为Double数组<br>
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Double[] toDoubleArray(Object value) {
        return convert(Double[].class, value);
    }

    /**
     * 转换为Float<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Float toFloat(Object value, Float defaultValue) {
        return convertQuietly(Float.class, value, defaultValue);
    }

    /**
     * 转换为Float<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Float toFloat(Object value) {
        return toFloat(value, null);
    }

    /**
     * 转换为Float数组<br>
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Float[] toFloatArray(Object value) {
        return convert(Float[].class, value);
    }

    /**
     * 转换为boolean<br>
     * String支持的值为：true、false、yes、ok、no，1,0 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Boolean toBool(Object value, Boolean defaultValue) {
        return convertQuietly(Boolean.class, value, defaultValue);
    }

    /**
     * 转换为boolean<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Boolean toBool(Object value) {
        return toBool(value, null);
    }

    /**
     * 转换为Boolean数组<br>
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Boolean[] toBooleanArray(Object value) {
        return convert(Boolean[].class, value);
    }

    /**
     * 转换为BigInteger<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
        return convertQuietly(BigInteger.class, value, defaultValue);
    }

    /**
     * 转换为BigInteger<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static BigInteger toBigInteger(Object value) {
        return toBigInteger(value, null);
    }

    /**
     * 转换为BigDecimal<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        return convertQuietly(BigDecimal.class, value, defaultValue);
    }

    /**
     * 转换为BigDecimal<br>
     * 如果给定的值为空，或者转换失败，返回null<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static BigDecimal toBigDecimal(Object value) {
        return toBigDecimal(value, null);
    }

    /**
     * 转换为Date<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Date toDate(Object value, Date defaultValue) {
        return convertQuietly(Date.class, value, defaultValue);
    }

    /**
     * LocalDateTime<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static LocalDateTime toLocalDateTime(Object value, LocalDateTime defaultValue) {
        return convertQuietly(LocalDateTime.class, value, defaultValue);
    }

    /**
     * 转换为LocalDateTime<br>
     * 如果给定的值为空，或者转换失败，返回<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static LocalDateTime toLocalDateTime(Object value) {
        return toLocalDateTime(value, null);
    }

    /**
     * Instant<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Date toInstant(Object value, Date defaultValue) {
        return convertQuietly(Instant.class, value, defaultValue);
    }

    /**
     * 转换为Date<br>
     * 如果给定的值为空，或者转换失败，返回<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Date toDate(Object value) {
        return toDate(value, null);
    }


    /**
     * 转换为ArrayList，元素类型默认Object
     *
     * @param value 被转换的值
     * @return {@link List}
     */
    public static List<?> toList(Object value) {
        return convert(List.class, value);
    }


    /**
     * 转换值为指定类型
     *
     * @param <T>   目标类型
     * @param type  类型
     * @param value 值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Class<T> type, Object value) throws ConvertException {
        return convert((Type) type, value);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>       目标类型
     * @param reference 类型参考，用于持有转换后的泛型类型
     * @param value     值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(TypeReference<T> reference, Object value) throws ConvertException {
        return convert(reference.getType(), value, null);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>   目标类型
     * @param type  类型
     * @param value 值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Type type, Object value) throws ConvertException {
        return convert(type, value, null);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>          目标类型
     * @param type         类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Class<T> type, Object value, T defaultValue) throws ConvertException {
        return convert((Type) type, value, defaultValue);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>          目标类型
     * @param type         类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Type type, Object value, T defaultValue) throws ConvertException {
        return ConverterRegistry.getInstance().convert(type, value, defaultValue);
    }

    /**
     * 转换值为指定类型，不抛异常转换<br>
     * 当转换失败时返回{@code null}
     *
     * @param <T>   目标类型
     * @param type  目标类型
     * @param value 值
     * @return 转换后的值，转换失败返回null
     */
    public static <T> T convertQuietly(Type type, Object value) {
        return convertQuietly(type, value, null);
    }

    /**
     * 转换值为指定类型，不抛异常转换<br>
     * 当转换失败时返回默认值
     *
     * @param <T>          目标类型
     * @param type         目标类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    public static <T> T convertQuietly(Type type, Object value, T defaultValue) {
        try {
            return convert(type, value, defaultValue);
        } catch (Throwable e) {
            return defaultValue;
        }
    }

    // ----------------------------------------------------------------------- 全角半角转换

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        return toSBC(input, null);
    }

    /**
     * 半角转全角
     *
     * @param input         String
     * @param notConvertSet 不替换的字符集合
     * @return 全角字符串.
     */
    public static String toSBC(String input, Set<Character> notConvertSet) {
        final char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (null != notConvertSet && notConvertSet.contains(c[i])) {
                // 跳过不替换的字符
                continue;
            }

            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {
        return toDBC(input, null);
    }

    /**
     * 替换全角为半角
     *
     * @param text          文本
     * @param notConvertSet 不替换的字符集合
     * @return 替换后的字符
     */
    public static String toDBC(String text, Set<Character> notConvertSet) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        final char[] c = text.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (null != notConvertSet && notConvertSet.contains(c[i])) {
                // 跳过不替换的字符
                continue;
            }

            if (c[i] == '\u3000' || c[i] == '\u00a0' || c[i] == '\u2007' || c[i] == '\u202F') {
                // \u3000是中文全角空格，\u00a0、\u2007、\u202F是不间断空格
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }

        return new String(c);
    }


    // --------------------------------------------------------------- 原始包装类型转换

    /**
     * 原始类转为包装类，非原始类返回原类
     *
     * @param clazz 原始类
     * @return 包装类
     * @see BasicType#wrap(Class)
     * @see BasicType#wrap(Class)
     */
    public static Class<?> wrap(Class<?> clazz) {
        return BasicType.wrap(clazz);
    }

    /**
     * 包装类转为原始类，非包装类返回原类
     *
     * @param clazz 包装类
     * @return 原始类
     * @see BasicType#unWrap(Class)
     * @see BasicType#unWrap(Class)
     */
    public static Class<?> unWrap(Class<?> clazz) {
        return BasicType.unWrap(clazz);
    }


    // -------------------------------------------------------------------------- 数字转换

    /**
     * int转byte
     *
     * @param intValue int值
     * @return byte值
     */
    public static byte intToByte(int intValue) {
        return (byte) intValue;
    }

    /**
     * byte转无符号int
     *
     * @param byteValue byte值
     * @return 无符号int值
     */
    public static int byteToUnsignedInt(byte byteValue) {
        // Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return byteValue & 0xFF;
    }

    /**
     * byte数组转short
     *
     * @param bytes byte数组
     * @return short值
     */
    public static short bytesToShort(byte[] bytes) {
        return (short) (bytes[1] & 0xff | (bytes[0] & 0xff) << 8);
    }

    /**
     * short转byte数组
     *
     * @param shortValue short值
     * @return byte数组
     */
    public static byte[] shortToBytes(short shortValue) {
        byte[] b = new byte[2];
        b[1] = (byte) (shortValue & 0xff);
        b[0] = (byte) ((shortValue >> 8) & 0xff);
        return b;
    }

    /**
     * byte[]转int值
     *
     * @param bytes byte数组
     * @return int值
     */
    public static int bytesToInt(byte[] bytes) {
        return bytes[3] & 0xFF | //
            (bytes[2] & 0xFF) << 8 | //
            (bytes[1] & 0xFF) << 16 | //
            (bytes[0] & 0xFF) << 24; //
    }

    /**
     * int转byte数组
     *
     * @param intValue int值
     * @return byte数组
     */
    public static byte[] intToBytes(int intValue) {
        return new byte[]{ //
            (byte) ((intValue >> 24) & 0xFF), //
            (byte) ((intValue >> 16) & 0xFF), //
            (byte) ((intValue >> 8) & 0xFF), //
            (byte) (intValue & 0xFF) //
        };
    }

    /**
     * long转byte数组<br>
     * from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     *
     * @param longValue long值
     * @return byte数组
     */
    public static byte[] longToBytes(long longValue) {
        // Magic number 8 should be defined as Long.SIZE / Byte.SIZE
        final byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (longValue & 0xFF);
            longValue >>= 8;
        }
        return result;
    }

    /**
     * byte数组转long<br>
     * from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     *
     * @param bytes byte数组
     * @return long值
     */
    public static long bytesToLong(byte[] bytes) {
        // Magic number 8 should be defined as Long.SIZE / Byte.SIZE
        long values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8;
            values |= (bytes[i] & 0xff);
        }
        return values;
    }
}
