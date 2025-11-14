package com.commons.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
//定期（每 5 秒）扫描某目录中新增的文件，并打印文件的名称到日志。
//file:inputFolder：指定需要扫描的目录为 inputFolder。
//delete=true：在消费文件后将文件从输入目录中删除。
//delay=5000：每隔 5 秒扫描一次目录。
//${header.CamelFileName}：获取被扫描文件的文件名。
@Component
public class FileScanRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:D:\\data\\log\\xl-plot?noop=true&delay=5000") // 扫描 inputFolder 目录，每隔5秒获取文件
            .log("Found file: ${header.CamelFileName}") // 打印文件名
            .to("stream:out"); // 打印消息到控制台
    }
}