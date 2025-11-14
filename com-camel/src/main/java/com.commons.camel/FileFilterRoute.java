package com.commons.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//扫描目录中的文件时，根据自定义规则（比如文件大小、文件名、文件内容）过滤文件，仅处理符合条件的文件。
//使用 filter 来根据自定义条件过滤文件。
//Exchange.FILE_LENGTH：获取文件的大小属性，用于文件尺寸判断。
//将符合条件的文件移动到 smallFilesFolder 中。
//@Component
//public class FileFilterRoute extends RouteBuilder {
//
//    @Override
//    public void configure() throws Exception {
//        from("file:inputFolder?noop=true") // 读取所有文件
//            .filter(exchange -> {
//                // 根据文件大小进行过滤，只处理小于10KB的文件
//                Long fileSize = (Long) exchange.getIn().getHeader(Exchange.FILE_LENGTH);
//                return fileSize != null && fileSize < 10 * 1024; // 文件小于10KB
//            })
//            .log("Processing small file: ${header.CamelFileName}")
//            .to("file:smallFilesFolder?fileName=small-${file:name}");
//    }
//}
