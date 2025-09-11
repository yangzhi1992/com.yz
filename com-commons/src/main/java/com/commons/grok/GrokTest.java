package com.commons.grok;

import com.alibaba.fastjson.JSON;
import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;
import java.util.Map;

/**
 * io.krakens:java-grok 是一个 Java 库，用于解析和匹配非结构化的日志数据，将其转换为结构化的键值对格式。
 * 这个库基于流行的 Grok 模式，最初由 Elasticsearch 的 Logstash项目推广使用。
 * <dependency>
 *      <groupId>io.krakens</groupId>
 *      <artifactId>java-grok</artifactId>
 *      <version>0.1.9</version>
 * </dependency>
 */
public class GrokTest {
    public static void main(String[] args) {
        /* Create a new grokCompiler instance */
        GrokCompiler grokCompiler = GrokCompiler.newInstance();
        grokCompiler.registerDefaultPatterns();

        /* Grok pattern to compile, here httpd logs */
        final Grok grok = grokCompiler.compile("%{COMBINEDAPACHELOG}");

        /* Line of log to match */
        String log =
                "112.169.19.192 - - [06/Mar/2013:01:36:30 +0900] \"GET / HTTP/1.1\" 200 44346 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22\"";
        Match gm = grok.match(log);
        /* Get the map with matches */
        final Map<String, Object> capture = gm.capture();
        System.out.println(JSON.toJSONString(capture));

        final Grok grok2 = grokCompiler.compile("a:%{a}|b:%{b}");
        String log2 = "a:a1|b:b1|c:c1";
        Match gm2 = grok2.match(log2);
        /* Get the map with matches */
        final Map<String, Object> capture2 = gm2.capture();
        System.out.println(JSON.toJSONString(capture2));

    }
}
