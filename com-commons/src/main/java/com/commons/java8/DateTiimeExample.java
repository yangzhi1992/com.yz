package com.commons.java8;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTiimeExample {
    public static void main(String[] args) {
        //
    }

    //Date 和 LocalDateTime 的区别：Date 是基于时间戳的，始终以 UTC 存储。而 LocalDateTime 是时区无关的，并且其范围不包括毫秒，因此它们的语义有些不同。
    //必须借助 Instant 和 ZoneId，以桥接二者。
    //LocalDateTime 是不可变对象，线程安全；Date 则是可变对象，非线程安全（需要手动同步）。
    public void swap(){
        //date -> long,long->date
        Date date = new Date();
        Long dateLong = date.getTime();
        date = new Date(dateLong);

        //date -> localDateTime
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault()) // 使用系统默认时区
                .toLocalDateTime();

        // LocalDateTime -> Date
        LocalDateTime localDateTime1 = LocalDateTime.now();
        Date date1 = Date.from(localDateTime1.atZone(ZoneId.systemDefault()) // 转为 ZonedDateTime
                .toInstant()); // ZonedDateTime -> Instant

    }

    //1、LocalDate 表示没有时区的日期，例如 2023-10-10。它是不可变的且线程安全的。
    public void execLocalDate() {
        LocalDate today = LocalDate.now();  // 获取当前日期
        LocalDate birthday = LocalDate.of(1990, 1, 1);  // 指定日期
        LocalDate parsedDate = LocalDate.parse("2023-10-10");   // 解析字符串为日期

        LocalDate tomorrow = today.plusDays(1); // 增加一天
        LocalDate lastWeek = today.minusWeeks(1); // 减去一周

        int year = today.getYear(); // 获取年份
        int month = today.getMonthValue(); // 获取月份（1-12）
        int dayOfMonth = today.getDayOfMonth(); // 获取月中日期
        DayOfWeek dayOfWeek = today.getDayOfWeek(); // 获取星期几

        boolean isLeapYear = LocalDate.of(2024, 1, 1).isLeapYear(); // 判断是否是闰年
        boolean isBefore = today.isBefore(LocalDate.of(2024, 1, 1)); // 比较日期小
        boolean isAfter = today.isAfter(LocalDate.of(2021, 1, 1));  // 比较日期大
    }

    //2、LocalTime 表示一段时间，不带日期，例如 10:15:30。
    public void execLocalTime() {
        LocalTime nowTime = LocalTime.now(); // 当前时间
        LocalTime specificTime = LocalTime.of(14, 30, 15); // 指定时间（14:30:15）
        LocalTime parsedTime = LocalTime.parse("14:30:15"); // 解析时间字符串

        LocalTime twoHoursLater = nowTime.plusHours(2); // 增加 2 小时
        LocalTime tenMinutesBefore = nowTime.minusMinutes(10); // 减少 10 分钟

        int hour = nowTime.getHour(); // 获取小时（0-23）
        int minute = nowTime.getMinute(); // 获取分钟
        int second = nowTime.getSecond(); // 获取秒
    }

    //3、LocalDateTime 表示一个日期和时间的组合，不包含时区。例如 2023-10-10T10:15:30。
    public void execLocalDateTime() {
        LocalDateTime now = LocalDateTime.now(); // 当前日期时间
        LocalDateTime specificDateTime = LocalDateTime.of(2023, 10, 10, 14, 30, 15); // 指定日期时间
        LocalDateTime parsedDateTime = LocalDateTime.parse("2023-10-10T14:30:15"); // 解析字符串为日期时间

        LocalDateTime plusOneDay = now.plusDays(1); // 增加一天
        LocalDateTime minusTwoHours = now.minusHours(2); // 减少两个小时

        LocalDate datePart = now.toLocalDate(); // 提取日期部分
        LocalTime timePart = now.toLocalTime(); // 提取时间部分
    }

    //4、ZonedDateTime 和 ZoneId ZonedDateTime 表示带有时区的日期时间。例如，2023-10-10T14:30:15+02:00[Europe/Paris]。
    public void execZonedDateTime() {
        ZonedDateTime nowInDefaultZone = ZonedDateTime.now(); // 当前时区日期时间
        ZoneId parisZone = ZoneId.of("Europe/Paris"); // 获取指定时区
        ZonedDateTime nowInParis = ZonedDateTime.now(parisZone); // 当前时间在巴黎

        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), parisZone); // 使用 LocalDateTime 创建带时区时间
        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC); // 当前时间的 UTC 时间
    }

    //5、Instant 表示时间戳（从 1970 年 1 月 1 日 00:00:00 UTC 开始的秒数）。它可以用来表示机器时间。
    public void execInstant() {
        Instant now1 = Instant.now(); // 当前时间戳
        Instant tenSecondsLater = now1.plusSeconds(10); // 增加 10 秒
        Instant fiveSecondsEarlier = now1.minusSeconds(5); // 减少 5 秒

        long epochSecond = now1.getEpochSecond(); // 获取秒数
        int nanoAdjustment = now1.getNano(); // 获取纳秒（0-999,999,999）

        // Instant 与 ZonedDateTime 互相转换
        ZonedDateTime zonedDateTime1 = now1.atZone(ZoneId.of("Europe/Paris")); // 转换为带时区的时间
    }

    /**
     * 6
     * Period 和 Duration
     * Period：表示两个日期之间的间隔，按年、月、日计算。
     * Duration：表示两个时间之间的间隔，按秒和纳秒计算。
     */
    public void preiodAndDuration() {
        // Period 示例
        Period oneMonth = Period.ofMonths(1); // 创建一个 1 个月的间隔
        Period betweenDates = Period.between(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1)); // 两个日期的间隔：P1Y

        // Duration 示例
        Duration tenMinutes = Duration.ofMinutes(10); // 10 分钟
        Duration betweenInstants = Duration.between(Instant.now(), Instant.now().plusSeconds(3600)); // 1 小时
    }

    //7、格式化与解析（DateTimeFormatter） DateTimeFormatter 用于将日期/时间对象转换为字符串，或者将字符串解析为日期/时间对象。
    public void format(){
        // 格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter); // 格式化日期时间

        // 解析
        LocalDateTime parsedDateTime = LocalDateTime.parse("2023-10-10 14:30:15", formatter); // 解析字符串
    }
}
