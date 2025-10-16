package com.commons.jsonforjackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NumberEntity {
    
    // 金额格式
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "##0.00")
    private BigDecimal price;
    
    // 百分比
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "##0.00%")
    private Double discount;
    
    // 科学计数法
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.###E0")
    private Double scientificNumber;
    
    // 千分位格式化
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long population;
    
    // 自定义数字格式
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "000000")
    private Integer serialNumber;
}