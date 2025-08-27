package com.commons.utils;

import static com.google.common.base.Joiner.on;

import com.commons.exception.BusinessException;
import com.commons.support.FrameworkConstants;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 *
 * @author allan
 */
public final class StringTool {

    public static final String EMPTY = "";

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static final int INDEX_NOT_FOUND = -1;
    /**
     * 连接字符串
     */
    public final static String JOIN_STR = "_";

    public final static char SPLIT_CHAR = ',';

    public static final String SPACE = " ";

    private static final int PAD_LIMIT = 8192;

    public final static String DEFAULT_ENCODING = "utf-8";

    static final int MAX_NAME_LENGTH = 100;

    static final char[] HEX_DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private StringTool() {

    }

    /**
     * 字符串截成指定长度
     */
    public static List<String> splitString(String str, int size) {
        List<String> chunks = new ArrayList<>();
        if (str == null || str.isEmpty() || size <= 0) return chunks;

        int start = 0;
        while (start < str.length()) {
            int end = Math.min(start + size, str.length());
            chunks.add(StringUtils.substring(str, start, end));
            start += size;
        }
        return chunks;
    }

    public static boolean isColor(String color) {
        if (StringUtils.isBlank(color)) {
            return false;
        }
        if (color.length() != 9 || !color.startsWith("#")) {
            return false;
        }
        return Pattern.matches("^#[0-9a-fA-F]{8}$", color);
    }

    /**
     * emoji 字符串截断
     */
    public static String emojiSubstring(String str, int startIndex, int endIndex) {
        return emojiSubstring(str, startIndex, endIndex, "");
    }

    public static String emojiSubstring(String str, int startIndex, int endIndex, String tail) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        if (startIndex >= endIndex) {
            throw new IllegalArgumentException("startIndex bigger then endIndex");
        }
        int length = str.codePointCount(0, str.length());
        if (startIndex >= length) {
            throw new IllegalArgumentException("startIndex bigger then length");
        }
        if (length <= endIndex) {
            return str;
        }
        return str.substring(str.offsetByCodePoints(0, startIndex), str.offsetByCodePoints(0, endIndex)) + tail;
    }

    /**
     * 首字母小写
     */
    public static String decapitalize(String word) {
        return onCapitalize(word, false);
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String word) {
        return onCapitalize(word, true);
    }

    private static String onCapitalize(String word, boolean on) {
        if (isBlank(word)) {
            return "";
        }
        String trimWord = word.trim();
        return (trimWord.isEmpty())
                ? trimWord
                : new StringBuilder(trimWord.length())
                .append(on ? Ascii.toUpperCase(trimWord.charAt(0))
                        : Ascii.toLowerCase(trimWord.charAt(0)))
                .append(trimWord.substring(1))
                .toString();
    }

    public static String rtrim(String word, String match) {
        return CharMatcher.anyOf(match).trimTrailingFrom(word);
    }

    public static String repeat(String word, int count) {
        return Strings.repeat(word, count);
    }

    public static <T> String repeat(String word, int count, T insert) {
        String repeat = repeat(word + insert, count);
        return repeat.substring(0, repeat.length() - insert.toString().length());
    }

    public static String join(String... args) {
        if (ArrayTool.isBlank(args)) {
            return null;
        }
        return on("").skipNulls().join(args);
    }


    public static boolean isNotBlank(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return true;
    }

    public static boolean isBlank(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static String replace(String sentence, char from, char to) {
        return CharMatcher.anyOf(String.valueOf(from)).replaceFrom(sentence, to);
    }

    public static String replaceAll(String str, String find, String replace) {
        return replaceAll(str, find, replace, false);
    }

    public static String replaceAll(String str, String find, String replace, boolean ignorecase) {
        String findRegex = ignorecase ? "(?i)" + find : find;
        return Pattern.compile(findRegex).matcher(defaultIfBlank(str, "")).replaceAll(replace);
    }

    public static String defaultIfBlank(String str, String defaultString) {
        return isNotBlank(str) ? str : defaultString;
    }

    /**
     * 字符串转换为整数,如果失败,返回null
     */
    public static Integer parseInt(String str) {
        if (isBlank(str)) {
            return null;
        }
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Long parseLong(String str) {
        if (isBlank(str)) {
            return null;
        }
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Short parseShort(String str) {
        if (isBlank(str)) {
            return null;
        }
        try {
            return Short.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Byte parseByte(String str) {
        if (isBlank(str)) {
            return null;
        }
        try {
            return Byte.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 字符串转换为长整型数,如果失败,返回1L
     */
    public static Long convert2Long(Object obj) {
        return convert2Long(obj, 1L);
    }

    public static Long convert2Long(Object obj, Long defaultVal) {
        if (obj == null) {
            return defaultVal;
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else {
            Long result = parseLong(obj.toString());
            return ObjectTool.defaultIfNull(result, defaultVal);
        }
    }

    /**
     * 字符串转换为整数,如果失败,返回1
     */
    public static Integer convert2Int(Object obj) {
        return convert2Int(obj, 1);
    }

    public static Integer convert2Int(Object obj, Integer defaultVal) {
        if (obj == null) {
            return defaultVal;
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else {
            Integer result = parseInt(obj.toString());
            return ObjectTool.defaultIfNull(result, defaultVal);
        }
    }

    /**
     * 字符串转换为Byte,如果失败,返回1
     */
    public static Byte convert2Byte(Object obj) {
        return convert2Byte(obj, (byte) 1);
    }

    public static Byte convert2Byte(Object obj, Byte defaultVal) {
        if (obj == null) {
            return defaultVal;
        } else if (obj instanceof Number) {
            return ((Number) obj).byteValue();
        } else {
            Byte result = parseByte(obj.toString());
            return ObjectTool.defaultIfNull(result, defaultVal);
        }
    }

    /**
     * 字符串转换为Short,如果失败,返回1
     */
    public static Short convert2Short(Object obj) {
        return convert2Short(obj, (short) 1);
    }

    public static Short convert2Short(Object obj, Short defaultVal) {
        if (obj == null) {
            return defaultVal;
        } else if (obj instanceof Number) {
            return ((Number) obj).shortValue();
        } else {
            Short result = parseShort(obj.toString());
            return ObjectTool.defaultIfNull(result, defaultVal);
        }
    }

    public static String valueOf(Object s) {
        if (s == null) {
            return "";
        }
        return String.valueOf(s);
    }

    public static byte[] toBytes(String str) {
        return toBytes(str, DEFAULT_ENCODING);
    }

    public static byte[] toBytes(String str, String encoding) {
        if (isBlank(str)) {
            return null;
        }
        try {
            return str.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException("将字符转换为字节数组出错!", e);
        }
    }

    public static String toString(byte[] bytes, String encoding) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException("将字节数组转换为字符出错!", e);
        }
    }

    public static String toString(byte[] bytes) {
        return toString(bytes, DEFAULT_ENCODING);
    }

    public static String substringBefore(final String str, final String separator) {
        if (isBlank(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static String exceptionToString(Throwable ex) {
        String exStr;
        if (ex.toString().length() > FrameworkConstants.MAX_LOG_MSG_LENGTH) {
            exStr = ex.toString();
        } else {
            StringBuilder sb = new StringBuilder(
                    ex.toString().replace("\r\n", FrameworkConstants.NEXT_DELIM)
                            .replace("\n", FrameworkConstants.NEXT_DELIM));
            sb.append(FrameworkConstants.NEXT_DELIM);
            StackTraceElement[] stackTraceElements = ex.getStackTrace();
            int length = stackTraceElements.length;
            if (length > FrameworkConstants.MAX_STACK_DEPTH) {
                length = FrameworkConstants.MAX_STACK_DEPTH;
            }
            for (int i = 0; i < length; i++) {
                sb.append(stackTraceElements[i]).append(FrameworkConstants.NEXT_DELIM);
                if (sb.length() > FrameworkConstants.MAX_LOG_MSG_LENGTH) {
                    break;
                }
            }
            exStr = sb.toString();
        }
        return exStr.replace(',', '.');
    }

    /**
     * 根据分隔符将字符串拆分成数组
     */
    public static String[] split(final String str) {
        return split(str, SPLIT_CHAR, true);
    }

    public static String[] split(final String str, final char separatorChar) {
        return split(str, separatorChar, true);
    }

    private static String[] split(final String str, final char separatorChar, final boolean preserveAllTokens) {
        if (isBlank(str)) {
            return EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        int len = str.length();
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 高性能的Split，针对char的分隔符号，比JDK String自带的高效.
     *
     * @param expectParts 预估分割后的List大小，初始化数据更精准
     * @return 如果为null返回null, 如果为""返回空数组
     */
    public static List<String> split(final String str, final char separatorChar, int expectParts) {
        if (str == null) {
            return null;
        }

        final int len = str.length();
        if (len == 0) {
            return new ArrayList<>();
        }

        final List<String> list = new ArrayList<>(expectParts);
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            i++;
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return list;
    }


    public static String underscoreToCamelCase(String param) {
        if (isBlank(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == '-' || c == '_') {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isChinese(String s) {
        if (StringTool.isBlank(s)) {
            return false;
        }
        char[] ch = s.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];

            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            boolean isChinese = ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
            if (!isChinese) {
                return false;
            }
        }
        return true;
    }

    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    public static String leftPad(final String str, final int size) {
        return leftPad(str, size, ' ');
    }

    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return EMPTY;
        }
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    public static String join(final Object[] array, String separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }

        final StringBuilder buf = new StringBuilder(noOfItems * 16);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(final Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }


    public static String shorten(String name) {
        if (org.springframework.util.StringUtils.isEmpty(name)) {
            return name;
        }
        int maxLength = name.length() > MAX_NAME_LENGTH ? MAX_NAME_LENGTH : name.length();
        return name.substring(0, maxLength);
    }

    public static String idToHex(long id) {
        char[] data = new char[16];
        writeHexLong(data, 0, id);
        return new String(data);
    }

    public static long hexToId(String hexString) {
        int length = hexString.length();
        if (length < 1 || length > 32) {
            throw new IllegalArgumentException("Malformed id: " + hexString);
        }

        int beginIndex = length > 16 ? length - 16 : 0;

        return hexToId(hexString, beginIndex);
    }

    public static long hexToId(String lowerHex, int index) {
        long result = 0;
        for (int endIndex = Math.min(index + 16, lowerHex.length()); index < endIndex; index++) {
            char c = lowerHex.charAt(index);
            result <<= 4;
            if (c >= '0' && c <= '9') {
                result |= c - '0';
            } else if (c >= 'a' && c <= 'f') {
                result |= c - 'a' + 10;
            } else {
                throw new IllegalArgumentException("Malformed id: " + lowerHex);
            }
        }
        return result;
    }

    private static void writeHexLong(char[] data, int pos, long v) {
        writeHexByte(data, pos + 0, (byte) ((v >>> 56L) & 0xff));
        writeHexByte(data, pos + 2, (byte) ((v >>> 48L) & 0xff));
        writeHexByte(data, pos + 4, (byte) ((v >>> 40L) & 0xff));
        writeHexByte(data, pos + 6, (byte) ((v >>> 32L) & 0xff));
        writeHexByte(data, pos + 8, (byte) ((v >>> 24L) & 0xff));
        writeHexByte(data, pos + 10, (byte) ((v >>> 16L) & 0xff));
        writeHexByte(data, pos + 12, (byte) ((v >>> 8L) & 0xff));
        writeHexByte(data, pos + 14, (byte) (v & 0xff));
    }

    private static void writeHexByte(char[] data, int pos, byte b) {
        data[pos + 0] = HEX_DIGITS[(b >> 4) & 0xf];
        data[pos + 1] = HEX_DIGITS[b & 0xf];
    }

}
