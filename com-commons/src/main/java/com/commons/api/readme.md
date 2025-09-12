1、代码示例
  com.commons.api包下
  CommonApiController 接口请求
  OkhttpClientTool 工具类使用
  注意：重新新建一个okhttp实例 new OkHttpClient() 性能较使用线程池获取实例性能较低
  配置见api.yml
1. OkHttp 特点
    1.1 关键特性
    高效的连接池： 支持 HTTP/1.1 和 HTTP/2，复用连接以减少延迟。
    异步请求： 提供同步和异步的 API，适合高并发场景。
    拦截器机制： 内置四种拦截器（应用拦截器、网络拦截器）用于灵活处理请求和响应。
    GZIP压缩： 默认支持 GZIP 压缩，提升数据传输效率。
    超时管理： 支持连接、写入、读取等多种超时设置。
    文件流下载和上传： 轻松处理大文件的上传与下载。
    简单清晰的API： 更易用且可扩展。
2. OkHttp 依赖引入
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <version>4.11.0</version>
    </dependency>
3. 请求方式详解
    OkHttpClient client = new OkHttpClient();
4. 相关请求 见com.commons.api.test
    同步 GET 请求
    异步 GET 请求
    POST 表单提交
    POST JSON 上传
    文件上传
    文件下载
    自定义拦截器
5. OkHttp 支持设置请求连接超时、读取超时和写入超时：
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
            .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
            .writeTimeout(15, TimeUnit.SECONDS)  // 写入超时
            .build();

