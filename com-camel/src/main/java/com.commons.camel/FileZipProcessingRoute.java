package com.commons.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//扫描目标文件夹，将所有文件压缩为 ZIP 文件，并定期移动到备份文件夹。
//marshal().zip()：用于将输入的文件压缩成 ZIP 格式。
//file:onlyname.noext：不包含扩展名的文件名，用于保持压缩文件名一致性。
//noop=false：文件会在处理后被移动到 .camel 子目录，确保文件不会被多次处理。
//@Component
//public class FileZipProcessingRoute extends RouteBuilder {
//    @Override
//    public void configure() throws Exception {
//        from("file:inputFolder?noop=false&delay=60000") // 每分钟扫描文件夹，并处理文件
//            .log("Zip file ${header.CamelFileName}")
//            .marshal().zipFile() // 将文件打包成ZIP压缩格式
//            .to("file:outputFolder?fileName=${file:onlyname.noext}.zip"); // 保存为 .zip 文件
//    }
//}
