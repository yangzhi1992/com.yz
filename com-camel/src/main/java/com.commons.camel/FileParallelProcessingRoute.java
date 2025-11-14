package com.commons.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//当扫描的文件量大时，可以使用并行处理来提高吞吐量，分发到多个处理线程中执行。
//split().parallelProcessing()：对一个大文件的每一行进行拆分，并使用多个线程并行处理每一行。
//可以提高处理密集型任务的效率。
@Component
public class FileParallelProcessingRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:inputFolder?noop=true&delay=2000")
            .split(body().tokenize("\n")).parallelProcessing() // 多线程处理
            .log("Processing line: ${body}")
            .end();
    }
}
