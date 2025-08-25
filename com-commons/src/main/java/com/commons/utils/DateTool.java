package com.commons.utils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 *
 */
public final class DateTool {

    private static final String LIBRARY_NAME = "microtime";

    private static boolean useNative = false;

    private static final int[] MONTH_LENGTH = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    private enum ModifyType {
        /**
         * Truncation.
         */
        TRUNCATE,

        /**
         * Rounding.
         */
        ROUND,

        /**
         * Ceiling.
         */
        CEILING
    }

    private static final int[][] fields = {
            {Calendar.MILLISECOND},
            {Calendar.SECOND},
            {Calendar.MINUTE},
            {Calendar.HOUR_OF_DAY, Calendar.HOUR},
            {Calendar.DATE, Calendar.DAY_OF_MONTH, Calendar.AM_PM},
            {Calendar.MONTH},
            {Calendar.YEAR},
            {Calendar.ERA}};

    // 以T分隔日期和时间，并带时区信息，符合ISO8601规范
    public static final String PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
    public static final String PATTERN_ISO_ON_SECOND = "yyyy-MM-dd'T'HH:mm:ssZZ";
    public static final String PATTERN_ISO_ON_DATE = "yyyy-MM-dd";

    // 以空格分隔日期和时间，不带时区信息
    public static final String PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_DEFAULT_ON_SECOND = "yyyy-MM-dd HH:mm:ss";

    // 使用工厂方法FastDateFormat.getInstance(), 从缓存中获取实例

    /**
     * 以T分隔日期和时间，并带时区信息，符合ISO8601规范
     */
    public static final FastDateFormat ISO_FORMAT = FastDateFormat.getInstance(PATTERN_ISO);
    public static final FastDateFormat ISO_ON_SECOND_FORMAT = FastDateFormat
            .getInstance(PATTERN_ISO_ON_SECOND);
    public static final FastDateFormat ISO_ON_DATE_FORMAT = FastDateFormat
            .getInstance(PATTERN_ISO_ON_DATE);

    /**
     * 以空格分隔日期和时间，不带时区信息
     */
    public static final FastDateFormat DEFAULT_FORMAT = FastDateFormat.getInstance(PATTERN_DEFAULT);
    public static final FastDateFormat DEFAULT_ON_SECOND_FORMAT = FastDateFormat
            .getInstance(PATTERN_DEFAULT_ON_SECOND);

    private DateTool() {
    }

    public static String currentDay() {
        return format(now(), "yyyyMMdd");
    }

    public static String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date parse(String dateStr, String pattern) {
        if (StringTool.isBlank(dateStr) || StringTool.isBlank(pattern)) {
            return null;
        }
        try {
            return FastDateFormat.getInstance(pattern).parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parse(String dateStr) {
        return parse(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        return FastDateFormat.getInstance(pattern).format(date);
    }

    public static Date now() {
        return new Date();
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 设置年份, 公元纪年.
     */
    public static Date setYears(final Date date, int amount) {
        return set(date, Calendar.YEAR, amount);
    }

    /**
     * 设置月份, 0-11.
     */
    public static Date setMonths(final Date date, int amount) {
        return set(date, Calendar.MONTH, amount);
    }

    /**
     * 设置日期, 1-31.
     */
    public static Date setDays(final Date date, int amount) {
        return set(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * 设置小时, 0-23.
     */
    public static Date setHours(final Date date, int amount) {
        return set(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 设置分钟, 0-59.
     */
    public static Date setMinutes(final Date date, int amount) {
        return set(date, Calendar.MINUTE, amount);
    }

    /**
     * 设置秒, 0-59.
     */
    public static Date setSeconds(final Date date, int amount) {
        return set(date, Calendar.SECOND, amount);
    }

    /**
     * 设置毫秒.
     */
    public static Date setMilliseconds(final Date date, int amount) {
        return set(date, Calendar.MILLISECOND, amount);
    }

    private static Date set(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        // getInstance() returns a new object, so this method is thread safe.
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }

    /**
     * 在当前时间上减去deltaInSeconds秒
     *
     * @param deltaInSeconds 单位为秒,可为正或负数
     */
    public static Date beforeNow(int deltaInSeconds) {
        return calculate(null, Calendar.SECOND, deltaInSeconds, true);
    }

    /**
     * 在当前时间上减去amount指定的时间,单位通过field指定;
     *
     * @param field 例如Calendar.SECOND
     */
    public static Date beforeNow(int field, int amount) {
        return calculate(null, field, amount, true);
    }

    public static Date beforeDate(Date date, int field, int amount) {
        return calculate(date, field, amount, true);
    }

    /**
     * 在当前时间上加上amount指定的时间,单位通过field指定;
     *
     * @param field 例如Calendar.SECOND
     */
    public static Date afterNow(int field, int amount) {
        return calculate(null, field, amount, false);
    }

    public static Date afterDate(Date date, int field, int amount) {
        return calculate(date, field, amount, false);
    }

    /**
     * 将Date转换为JDK8的LocalDateTime
     */
    public static LocalDateTime convert2LocalDateTime(Date date) {
        Date d = date == null ? now() : date;
        Instant instant = d.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    /**
     * 将Date转换为JDK8的LocalDate
     */
    public static LocalDate convert2LocalDate(Date date) {
        return convert2LocalDateTime(date).toLocalDate();
    }

    /**
     * 将Date转换为JDK8的LocalTime
     */
    public static LocalTime convert2LocalTime(Date date) {
        return convert2LocalDateTime(date).toLocalTime();
    }

    public static Date convertFromLocalDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }


    public static Date convertFromLocalDate(LocalDate localDate) {
        LocalDate ld = localDate == null ? LocalDate.now() : localDate;
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = ld.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static Date convertFromLocalDateAndTime(LocalDate localDate, LocalTime localTime) {
        LocalDate ld = localDate == null ? LocalDate.now() : localDate;
        LocalTime lt = localTime == null ? LocalTime.now() : localTime;
        LocalDateTime localDateTime = LocalDateTime.of(ld, lt);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }


    private static Date calculate(Date date, int field, int amount, boolean before) {
        Calendar c = Calendar.getInstance();
        if (date != null) {
            c.setTime(date);
        }
        if (before) {
            c.add(field, -1 * amount);
        } else {
            c.add(field, amount);
        }
        return c.getTime();
    }

    public static long getCurrentTimeMicros() {
        if (useNative) {
            return currentTimeMicros();
        } else {
            return System.currentTimeMillis() * 1000;
        }
    }

    /**
     * 获得日期是一周的第几天. 已改为中国习惯，1 是Monday，而不是Sundays.
     */
    public static int getDayOfWeek(final Date date) {
        int result = getWithMondayFirst(date, Calendar.DAY_OF_WEEK);
        return result == 1 ? 7 : result - 1;
    }

    /**
     * 获得日期是一年的第几天，返回值从1开始
     */
    public static int getDayOfYear(final Date date) {
        return get(date, Calendar.DAY_OF_YEAR);
    }

    /**
     * 获得日期是一月的第几周，返回值从1开始.
     *
     * 开始的一周，只要有一天在那个月里都算. 已改为中国习惯，1 是Monday，而不是Sunday
     */
    public static int getWeekOfMonth(final Date date) {
        return getWithMondayFirst(date, Calendar.WEEK_OF_MONTH);
    }

    /**
     * 获得日期是一年的第几周，返回值从1开始.
     *
     * 开始的一周，只要有一天在那一年里都算.已改为中国习惯，1 是Monday，而不是Sunday
     */
    public static int getWeekOfYear(final Date date) {
        return getWithMondayFirst(date, Calendar.WEEK_OF_YEAR);
    }

    /**
     * 是否闰年.
     */
    public static boolean isLeapYear(final Date date) {
        return isLeapYear(get(date, Calendar.YEAR));
    }

    /**
     * 是否闰年，copy from Jodd Core的TimeUtil
     *
     * 参数是公元计数, 如2016
     */
    public static boolean isLeapYear(int y) {
        boolean result = false;

        if (((y % 4) == 0) && // must be divisible by 4...
                ((y < 1582) || // and either before reform year...
                        ((y % 100) != 0) || // or not a century...
                        ((y % 400) == 0))) { // or a multiple of 400...
            result = true; // for leap year.
        }
        return result;
    }

    /**
     * 获取某个月有多少天, 考虑闰年等因数
     */
    public static int getMonthLength(final Date date) {
        int year = get(date, Calendar.YEAR);
        int month = get(date, Calendar.MONTH);
        return getMonthLength(year, month);
    }

    /**
     * 获取某个月有多少天, 考虑闰年等因数, 移植Jodd Core的TimeUtil
     */
    public static int getMonthLength(int year, int month) {
        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Invalid month: " + month);
        }
        if (month == 2) {
            return isLeapYear(year) ? 29 : 28;
        }

        return MONTH_LENGTH[month];
    }

    /**
     * 判断日期是否在范围内，包含相等的日期
     */
    public static boolean isBetween(final Date date, final Date start, final Date end) {
        if (date == null || start == null || end == null || start.after(end)) {
            throw new IllegalArgumentException(
                    "some date parameters is null or start after end");
        }
        return !date.before(start) && !date.after(end);
    }

    public static Date truncate(final Date date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.TRUNCATE);
        return gval.getTime();
    }

    private static void modify(final Calendar val, final int field, final ModifyType modType) {
        if (val.get(Calendar.YEAR) > 280000000) {
            throw new ArithmeticException("Calendar value too large for accurate calculations");
        }

        if (field == Calendar.MILLISECOND) {
            return;
        }

        final Date date = val.getTime();
        long time = date.getTime();
        boolean done = false;

        final int millisecs = val.get(Calendar.MILLISECOND);
        if (ModifyType.TRUNCATE == modType || millisecs < 500) {
            time = time - millisecs;
        }
        if (field == Calendar.SECOND) {
            done = true;
        }

        final int seconds = val.get(Calendar.SECOND);
        if (!done && (ModifyType.TRUNCATE == modType || seconds < 30)) {
            time = time - (seconds * 1000L);
        }
        if (field == Calendar.MINUTE) {
            done = true;
        }

        final int minutes = val.get(Calendar.MINUTE);
        if (!done && (ModifyType.TRUNCATE == modType || minutes < 30)) {
            time = time - (minutes * 60000L);
        }

        if (date.getTime() != time) {
            date.setTime(time);
            val.setTime(date);
        }

        boolean roundUp = false;
        for (final int[] aField : fields) {
            for (final int element : aField) {
                if (element == field) {
                    if (modType == ModifyType.CEILING || modType == ModifyType.ROUND && roundUp) {
                        if (field == Calendar.AM_PM) {
                            if (val.get(Calendar.HOUR_OF_DAY) == 0) {
                                val.add(Calendar.HOUR_OF_DAY, 12);
                            } else {
                                val.add(Calendar.HOUR_OF_DAY, -12);
                                val.add(Calendar.DATE, 1);
                            }
                        } else {
                            val.add(aField[0], 1);
                        }
                    }
                    return;
                }
            }
            int offset = 0;
            boolean offsetSet = false;
            switch (field) {
                case Calendar.AM_PM:
                    if (aField[0] == Calendar.HOUR_OF_DAY) {
                        offset = val.get(Calendar.HOUR_OF_DAY);
                        if (offset >= 12) {
                            offset -= 12;
                        }
                        roundUp = offset >= 6;
                        offsetSet = true;
                    }
                    break;
                default:
                    break;
            }
            if (!offsetSet) {
                final int min = val.getActualMinimum(aField[0]);
                final int max = val.getActualMaximum(aField[0]);
                offset = val.get(aField[0]) - min;
                roundUp = offset > ((max - min) / 2);
            }
            if (offset != 0) {
                val.set(aField[0], val.get(aField[0]) - offset);
            }
        }
        throw new IllegalArgumentException("The field " + field + " is not supported");

    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-1-1 00:00:00
     */
    public static Date beginOfYear(final Date date) {
        return truncate(date, Calendar.YEAR);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-12-31 23:59:59.999
     */
    public static Date endOfYear(final Date date) {
        return new Date(nextYear(date).getTime() - 1);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2017-1-1 00:00:00
     */
    public static Date nextYear(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, Calendar.YEAR, ModifyType.CEILING);
        return gval.getTime();
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-11-1 00:00:00
     */
    public static Date beginOfMonth(final Date date) {
        return truncate(date, Calendar.MONTH);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-11-30 23:59:59.999
     */
    public static Date endOfMonth(final Date date) {
        return new Date(nextMonth(date).getTime() - 1);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-12-1 00:00:00
     */
    public static Date nextMonth(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, Calendar.MONTH, ModifyType.CEILING);
        return gval.getTime();
    }

    /**
     * 2017-1-20 07:33:23, 则返回2017-1-16 00:00:00
     */
    public static Date beginOfWeek(final Date date) {
        return truncate(beforeDate(date, Calendar.DAY_OF_MONTH, getDayOfWeek(date) - 1),
                Calendar.DATE);
    }

    /**
     * 2017-1-20 07:33:23, 则返回2017-1-22 23:59:59.999
     */
    public static Date endOfWeek(final Date date) {
        return new Date(nextWeek(date).getTime() - 1);
    }

    /**
     * 2017-1-23 07:33:23, 则返回2017-1-22 00:00:00
     */
    public static Date nextWeek(final Date date) {
        return truncate(afterDate(date, Calendar.DAY_OF_MONTH, 8 - getDayOfWeek(date)),
                Calendar.DATE);
    }


    /**
     * 2016-11-10 07:33:23, 则返回2016-11-10 00:00:00
     */
    public static Date beginOfDay(final Date date) {
        return truncate(date, Calendar.DATE);
    }

    /**
     * 2017-1-23 07:33:23, 则返回2017-1-23 23:59:59.999
     */
    public static Date endOfDay(final Date date) {
        return new Date(nextDay(date).getTime() - 1);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-11-11 00:00:00
     */
    public static Date nextDay(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, Calendar.DATE, ModifyType.CEILING);
        return gval.getTime();
    }

    /**
     * 2016-12-10 07:33:23, 则返回2016-12-10 07:00:00
     */
    public static Date beginOfHour(final Date date) {
        return truncate(date, Calendar.HOUR_OF_DAY);
    }

    /**
     * 2017-1-23 07:33:23, 则返回2017-1-23 07:59:59.999
     */
    public static Date endOfHour(final Date date) {
        return new Date(nextHour(date).getTime() - 1);
    }

    /**
     * 2016-12-10 07:33:23, 则返回2016-12-10 08:00:00
     */
    public static Date nextHour(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, Calendar.HOUR_OF_DAY, ModifyType.CEILING);
        return gval.getTime();
    }

    /**
     * 2016-12-10 07:33:23, 则返回2016-12-10 07:33:00
     */
    public static Date beginOfMinute(final Date date) {
        return truncate(date, Calendar.MINUTE);
    }

    /**
     * 2017-1-23 07:33:23, 则返回2017-1-23 07:33:59.999
     */
    public static Date endOfMinute(final Date date) {
        return new Date(nextMinute(date).getTime() - 1);
    }

    /**
     * 2016-12-10 07:33:23, 则返回2016-12-10 07:34:00
     */
    public static Date nextMinute(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, Calendar.MINUTE, ModifyType.CEILING);
        return gval.getTime();
    }

    /**
     * 打印用户友好的，与当前时间相比的时间差，如刚刚，5分钟前，今天XXX，昨天XXX
     */
    public static String formatFriendlyTimeSpanByNow(Date date) {
        return formatFriendlyTimeSpanByNow(date.getTime());
    }

    public static String formatFriendlyTimeSpanByNow(long timeStampMillis) {
        long now = System.currentTimeMillis();
        long span = now - timeStampMillis;
        if (span < 0) {
            // 'c' 日期和时间，被格式化为 "%ta %tb %td %tT %tZ %tY"，例如 "Sun Jul 20 16:17:00 EDT 1969"。
            return String.format("%tc", timeStampMillis);
        }
        if (span < MILLIS_PER_SECOND) {
            return "刚刚";
        } else if (span < MILLIS_PER_MINUTE) {
            return String.format("%d秒前", span / MILLIS_PER_SECOND);
        } else if (span < MILLIS_PER_HOUR) {
            return String.format("%d分钟前", span / MILLIS_PER_MINUTE);
        }
        // 获取当天00:00
        long wee = beginOfDay(new Date(now)).getTime();
        if (timeStampMillis >= wee) {
            // 'R' 24 小时制的时间，被格式化为 "%tH:%tM"
            return String.format("今天%tR", timeStampMillis);
        } else if (timeStampMillis >= wee - MILLIS_PER_DAY) {
            return String.format("昨天%tR", timeStampMillis);
        } else {
            // 'F' ISO 8601 格式的完整日期，被格式化为 "%tY-%tm-%td"。
            return String.format("%tF", timeStampMillis);
        }
    }

    private static int getWithMondayFirst(final Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        return cal.get(field);
    }

    public static Date getMondayOfWeek(Date currDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currDate);

        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的
        day = (day == 1) ? 8 : day;
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, 2 - day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // 所在周星期一的日期
        return cal.getTime();
    }

    private static int get(final Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(field);
    }

    static {
        try {
            NativeLoaderTool.load(LIBRARY_NAME, DateTool.class.getClassLoader());
            useNative = true;
        } catch (UnsatisfiedLinkError ignore) {
            System.err.println("Failed to load native library,using System.currentTimeMillis!");
        }
    }

    native static long currentTimeMicros();


}
