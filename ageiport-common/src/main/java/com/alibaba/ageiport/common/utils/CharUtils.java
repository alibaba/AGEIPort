package com.alibaba.ageiport.common.utils;

/**
 * 字符工具类<br>
 * 部分工具来自于Apache Commons系列
 *
 * @author lingyi
 */
public class CharUtils {
    public static final char DOT = '.';

    private static final int ASCII_LENGTH = 128;
    private static final String[] CACHE = new String[ASCII_LENGTH];

    static {
        for (char c = 0; c < ASCII_LENGTH; c++) {
            CACHE[c] = String.valueOf(c);
        }
    }

    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     */
    public static boolean isBlankChar(char c) {
        return isBlankChar((int) c);
    }

    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     */
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a';
    }

    /**
     * 给定对象对应的类是否为字符类，字符类包括：
     *
     * <pre>
     * Character.class
     * char.class
     * </pre>
     *
     * @param value 被检查的对象
     * @return true表示为字符类
     */
    public static boolean isChar(Object value) {
        //noinspection ConstantConditions
        return value instanceof Character || value.getClass() == char.class;
    }

    /**
     * 字符转为字符串<br>
     * 如果为ASCII字符，使用缓存
     *
     * @param c 字符
     * @return 字符串
     */
    public static String toString(char c) {
        return c < ASCII_LENGTH ? CACHE[c] : String.valueOf(c);
    }
}
