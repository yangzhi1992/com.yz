package com.commons.camel;

//扫描目录中新增的文件，并读取文件内容进行处理。
//noop=true：文件不会从 inputFolder 中删除，只读取文件内容。
//file:name.noext：文件名（去掉扩展名），附加上 processed- 前缀创建新的文件名称。
//在 process 中，将文件内容读取出来进行大写转换后写到另一个文件夹中。
//路由会在 outputFolder 目录生成新的处理过的文件（以 processed- 为前缀）。
//@Component
//public class FileContentProcessingRoute extends RouteBuilder {
//
//    @Override
//    public void configure() throws Exception {
//        from("file:inputFolder?noop=true&delay=10000") // 定时扫描文件夹且不删除文件
//            .log("Processing file: ${header.CamelFileName}") // 打印文件名称
//            .process(exchange -> {
//                // 获取文件的内容
//                String fileContent = exchange.getIn().getBody(String.class);
//                exchange.getMessage().setBody("Processed File Content:\n" + fileContent.toUpperCase());
//            })
//            .log("Processed content: ${body}") // 打印处理后的文件内容
//            .to("file:outputFolder?fileName=processed-${file:name.noext}.txt"); // 处理后保存到 outputFolder 中
//    }
//}
