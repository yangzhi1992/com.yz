package com.commons.common.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.StringUtils;

public class RandomTool {

    /**
     * 返回无锁的ThreadLocalRandom
     */
    public static Random threadLocalRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 使用性能更好的SHA1PRNG, Tomcat的sessionId生成也用此算法.
     */
    public static SecureRandom secureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            return new SecureRandom();
        }
    }

    /**
     * 返回0到Intger.MAX_VALUE的随机Int, 使用ThreadLocalRandom.
     */
    public static int nextInt() {
        return nextInt(ThreadLocalRandom.current());
    }

    /**
     * 返回0到Intger.MAX_VALUE的随机Int, 可传入ThreadLocalRandom或SecureRandom
     */
    public static int nextInt(Random random) {
        int n = random.nextInt();
        if (n == Integer.MIN_VALUE) {
            n = 0;
        } else {
            n = Math.abs(n);
        }

        return n;
    }

    /**
     * 返回0到max的随机Int, 使用ThreadLocalRandom.
     */
    public static int nextInt(int max) {
        return nextInt(ThreadLocalRandom.current(), max);
    }

    /**
     * 返回0到max的随机Int, 可传入SecureRandom或ThreadLocalRandom
     */
    public static int nextInt(Random random, int max) {
        return random.nextInt(max);
    }

    /**
     * 返回min到max的随机Int, 使用ThreadLocalRandom.
     *
     * min必须大于0.
     */
    public static int nextInt(int min, int max) {
        return nextInt(ThreadLocalRandom.current(), min, max);
    }

    /**
     * 返回min到max的随机Int,可传入SecureRandom或ThreadLocalRandom.
     *
     * min必须大于0.
     *
     * JDK本身不具有控制两端范围的nextInt，因此参考Commons Lang RandomUtils的实现, 不直接复用是因为要传入Random实例
     */
    public static int nextInt(Random random, int min, int max) {
        if (min == max) {
            return min;
        }
        return min + random.nextInt(max - min);
    }

    /**
     * 返回0－Long.MAX_VALUE间的随机Long, 使用ThreadLocalRandom.
     */
    public static long nextLong() {
        return nextLong(ThreadLocalRandom.current());
    }

    /**
     * 返回0－Long.MAX_VALUE间的随机Long, 可传入SecureRandom或ThreadLocalRandom
     */
    public static long nextLong(Random random) {
        long n = random.nextLong();
        if (n == Long.MIN_VALUE) {
            n = 0;
        } else {
            n = Math.abs(n);
        }
        return n;
    }

    /**
     * 返回0－max间的随机Long, 使用ThreadLocalRandom.
     */
    public static long nextLong(long max) {
        return nextLong(ThreadLocalRandom.current(), 0, max);
    }

    /**
     * 返回0-max间的随机Long, 可传入SecureRandom或ThreadLocalRandom
     */
    public static long nextLong(Random random, long max) {
        return nextLong(random, 0, max);
    }

    /**
     * 返回min－max间的随机Long, 使用ThreadLocalRandom.
     *
     * min必须大于0.
     */
    public static long nextLong(long min, long max) {
        return nextLong(ThreadLocalRandom.current(), min, max);
    }

    /**
     * 返回min-max间的随机Long,可传入SecureRandom或ThreadLocalRandom.
     *
     * min必须大于0.
     *
     * JDK本身不具有控制两端范围的nextLong，因此参考Commons Lang RandomUtils的实现, 不直接复用是因为要传入Random实例
     *
     * @see org.apache.commons.lang3.RandomUtils#nextLong(long, long)
     */
    public static long nextLong(Random random, long min, long max) {
        if (min == max) {
            return min;
        }

        return (long) (min + ((max - min) * random.nextDouble()));
    }

    /**
     * 返回0-之间的double, 使用ThreadLocalRandom
     */
    public static double nextDouble() {
        return nextDouble(ThreadLocalRandom.current(), 0, Double.MAX_VALUE);
    }

    /**
     * 返回0-Double.MAX之间的double
     */
    public static double nextDouble(Random random) {
        return nextDouble(random, 0, Double.MAX_VALUE);
    }

    /**
     * 返回0-max之间的double, 使用ThreadLocalRandom
     *
     * 注意：与JDK默认返回0-1的行为不一致.
     */
    public static double nextDouble(double max) {
        return nextDouble(ThreadLocalRandom.current(), 0, max);
    }

    /**
     * 返回0-max之间的double
     */
    public static double nextDouble(Random random, double max) {
        return nextDouble(random, 0, max);
    }

    /**
     * 返回min-max之间的double,ThreadLocalRandom
     */
    public static double nextDouble(final double min, final double max) {
        return nextDouble(ThreadLocalRandom.current(), min, max);
    }

    /**
     * 返回min-max之间的double
     */
    public static double nextDouble(Random random, final double min, final double max) {
        if (Double.compare(min, max) == 0) {
            return min;
        }

        return min + ((max - min) * random.nextDouble());
    }
    //////////////////// String/////////

    /**
     * 随机字母或数字，固定长度
     */
    public static String randomStringFixLength(int length) {
        return random(length, 0, 0, true, true, null, threadLocalRandom());
    }

    /**
     * 随机字母或数字，固定长度
     */
    public static String randomStringFixLength(Random random, int length) {
        return random(length, 0, 0, true, true, null, random);
    }

    /**
     * 随机字母或数字，随机长度
     */
    public static String randomStringRandomLength(int minLength, int maxLength) {
        return
                random(nextInt(minLength, maxLength), 0, 0, true, true, null, threadLocalRandom());
    }

    /**
     * 随机字母或数字，随机长度
     */
    public static String randomStringRandomLength(Random random, int minLength, int maxLength) {
        return
                random(nextInt(random, minLength, maxLength), 0, 0, true, true, null, random);
    }

    /**
     * 随机字母，固定长度
     */
    public static String randomLetterFixLength(int length) {
        return random(length, 0, 0, true, false, null, threadLocalRandom());
    }

    /**
     * 随机字母，固定长度
     */
    public static String randomLetterFixLength(Random random, int length) {
        return random(length, 0, 0, true, false, null, random);
    }

    /**
     * 随机字母，随机长度
     */
    public static String randomLetterRandomLength(int minLength, int maxLength) {
        return random(nextInt(minLength, maxLength), 0, 0, true, false, null,
                threadLocalRandom());
    }

    /**
     * 随机字母，随机长度
     */
    public static String randomLetterRandomLength(Random random, int minLength, int maxLength) {
        return
                random(nextInt(random, minLength, maxLength), 0, 0, true, false, null, random);
    }

    /**
     * 随机ASCII字符(含字母，数字及其他符号)，固定长度
     */
    public static String randomAsciiFixLength(int length) {
        return random(length, 32, 127, false, false, null, threadLocalRandom());
    }

    /**
     * 随机ASCII字符(含字母，数字及其他符号)，固定长度
     */
    public static String randomAsciiFixLength(Random random, int length) {
        return random(length, 32, 127, false, false, null, random);
    }

    /**
     * 随机ASCII字符(含字母，数字及其他符号)，随机长度
     */
    public static String randomAsciiRandomLength(int minLength, int maxLength) {
        return random(nextInt(minLength, maxLength), 32, 127, false, false, null,
                threadLocalRandom());
    }

    /**
     * 随机ASCII字符(含字母，数字及其他符号)，随机长度
     */
    public static String randomAsciiRandomLength(Random random, int minLength, int maxLength) {
        return
                random(nextInt(random, minLength, maxLength), 32, 127, false, false, null, random);
    }

    public static String random(int count, int start, int end, final boolean letters,
            final boolean numbers,
            final char[] chars, final Random random) {
        if (count == 0) {
            return StringUtils.EMPTY;
        } else if (count < 0) {
            throw new IllegalArgumentException(
                    "Requested random string length " + count + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }

        if (start == 0 && end == 0) {
            if (chars != null) {
                end = chars.length;
            } else {
                if (!letters && !numbers) {
                    end = Integer.MAX_VALUE;
                } else {
                    end = 'z' + 1;
                    start = ' ';
                }
            }
        } else {
            if (end <= start) {
                throw new IllegalArgumentException(
                        "Parameter end (" + end + ") must be greater than start (" + start + ")");
            }
        }

        final char[] buffer = new char[count];
        final int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if (letters && Character.isLetter(ch)
                    || numbers && Character.isDigit(ch)
                    || !letters && !numbers) {
                if (ch >= 56320 && ch <= 57343) {
                    if (count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if (ch >= 55296 && ch <= 56191) {
                    if (count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }
}
