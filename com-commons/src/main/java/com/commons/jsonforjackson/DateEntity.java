package com.commons.jsonforjackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DateEntity {
    // 基本日期时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    // 仅日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    
    // 仅时间
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date workStartTime;
    
    // 序列化为时间戳
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date timestamp;
    
    // 序列化为字符串（默认格式）
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date updateTime;
}