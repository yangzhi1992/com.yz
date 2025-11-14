package com.commons.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//动态扫描多个文件夹，处理不同类型的文件，重命名并移动到对应的文件夹中。
//include=.*\\.txt 和 include=.*\\.csv：仅匹配特定扩展名的文件。
//根据文件类型，将文件路由到不同的处理函数或目录中。
//通过动态的 direct 路由提高代码可读性和扩展性。
@Component
public class DynamicFileScanRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file://inputFolder1?noop=true&include=.*\\.txt") // 仅扫描 .txt 文件
            .to("direct:processTextFiles");

        from("file://inputFolder2?noop=true&include=.*\\.csv") // 仅扫描 .csv 文件
            .to("direct:processCsvFiles");

        from("direct:processTextFiles")
            .log("Processing Text File: ${header.CamelFileName}")
            .to("file:/processedTextFiles?fileName=text-${file:name.noext}.processed.txt"); // 处理后存储

        from("direct:processCsvFiles")
            .log("Processing CSV File: ${header.CamelFileName}")
            .to("file:/processedCsvFiles?fileName=csv-${file:name.noext}.processed.csv"); // 处理后存储
    }
}
