package com.commons.jsonforjackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JavaTimeEntity {
    
    // LocalDate - 仅日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;
    
    // LocalTime - 仅时间
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime localTime;
    
    // LocalDateTime - 日期时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
    
    // ZonedDateTime - 带时区的日期时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss XXX")
    private ZonedDateTime zonedDateTime;
    
    // Instant - 时间戳
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant instant;
    
    // YearMonth - 年月
    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth yearMonth;
}