package com.commons.utils.http;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkhttpClientTool {

    private static final OkHttpClient CLIENT;

    static {
        /**
         * 1、默认连接池配置
         * OkHttp 的默认连接池配置如下：
         *  最大空闲连接数：5 个
         *  保持存活时间：5 分钟
         *  清理周期：每 5 分钟执行一次清理任务
         * 2、默认线程池配置
         * OkHttp 通过 Dispatcher 管理线程池，默认配置如下：
         *  最大并发请求数：64 个
         *  每个主机的最大并发请求数：5 个
         *  线程池类型：无界缓存线程池（类似于 Executors.newCachedThreadPool()）
         *  线程空闲超时：60 秒
         * 3、OkHttpClient 确实有线程池：即使没有显式配置，OkHttpClient 内部也使用线程池管理异步请求和连接。
         * 连接池 vs 线程池：
         *    连接池(ConnectionPool)：管理物理连接的复用
         *    线程池(Dispatcher)：管理异步请求的执行
         * 建议显式配置：通过显式配置 Dispatcher，可以更好地控制并发行为，避免资源过度消耗。
         */
        // 创建连接池，这个连接池管理着HTTP/1.x和HTTP/2连接的复用，但它不是执行请求的线程池。
        ConnectionPool connectionPool = new ConnectionPool(200, 5, TimeUnit.MINUTES);

        // 创建自定义Dispatcher，明确控制线程池
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(100); // 最大并发请求数
        dispatcher.setMaxRequestsPerHost(20); // 每个主机的最大并发请求数

        CLIENT = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .addInterceptor(new DynamicTimeoutInterceptor())
                .retryOnConnectionFailure(true)
                .build();
        ;
    }

    /**
     * GET请求，返回的String可直接JSONObject.parseObject(response.body().string())
     */
    public static String performGetRequest(String baseUrl, Map<String, String> params, Map<String, String> headers,
            Integer timeout) {
        try {
            // 1. 构建 URL 并动态添加 Query 参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl)
                                                .newBuilder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpUrl url = urlBuilder.build();

            // 2. 构建 HTTP GET 请求
            Request.Builder requestBuilder = new Request.Builder().url(url)
                                                                  .get();
            //或 Request.Builder requestBuilder = new Request.Builder().url(url).method("GET",null);
            if (null != headers) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 添加超时头信息（如果需要通过拦截器处理超时）
            if (timeout != null) {
                requestBuilder.addHeader("X-Timeout-Seconds", String.valueOf(timeout));
            }
            Request request = requestBuilder.build();

            // 3. 执行请求并获取响应
            try (okhttp3.Response response = CLIENT.newCall(request)
                                                   .execute()) {  // ensure close resource
                if (response.isSuccessful() && response.body() != null) {
                    return response.body()
                                   .string(); // 返回响应体
                } else {
                    throw new RuntimeException(
                            "Unexpected response: " + response.code() + ", message: " + response.message());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error performing GET request", e);
        }
    }

    /**
     * POST JSON请求，返回的String可直接JSONObject.parseObject(response.body().string())
     */
    public static String performPostJsonRequest(
            String baseUrl,
            Map<String, String> params,
            Map<String, String> headers,
            JSONObject jsonBody,
            Integer timeout) {
        try {
            // 1. 构建 URL 并动态添加 Query 参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl)
                                                .newBuilder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpUrl url = urlBuilder.build();

            // 2. 创建JSON请求体
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonBody.toJSONString());

            // 3. 构建 HTTP POST 请求
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(body); // 设置POST方法和请求体

            // 添加请求头
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }

            // 确保有Content-Type头
            requestBuilder.addHeader("Content-Type", "application/json");

            // 添加超时头信息（如果需要通过拦截器处理超时）
            if (timeout != null) {
                requestBuilder.addHeader("X-Timeout-Seconds", String.valueOf(timeout));
            }

            Request request = requestBuilder.build();

            // 4. 执行请求并获取响应
            try (okhttp3.Response response = CLIENT.newCall(request)
                                                   .execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body()
                                   .string(); // 返回响应体
                } else {
                    throw new RuntimeException(
                            "Unexpected response: " + response.code() + ", message: " + response.message());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error performing POST JSON request", e);
        }
    }

    /**
     * POST Form表单请求，用于提交键值对数据
     */
    public static String performPostFormRequest(String baseUrl, Map<String, String> params,
            Map<String, String> headers, Integer timeout, Map<String, String> formData) {
        try {
            // 1. 构建 URL 并动态添加 Query 参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl)
                                                .newBuilder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpUrl url = urlBuilder.build();

            // 2. 创建Form表单请求体
            FormBody.Builder formBuilder = new FormBody.Builder();
            if (formData != null) {
                for (Map.Entry<String, String> entry : formData.entrySet()) {
                    formBuilder.add(entry.getKey(), entry.getValue());
                }
            }
            RequestBody body = formBuilder.build();

            // 3. 构建 HTTP POST 请求
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(body);

            // 添加请求头
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }

            // 添加超时头信息
            if (timeout != null) {
                requestBuilder.addHeader("X-Timeout-Seconds", String.valueOf(timeout));
            }

            Request request = requestBuilder.build();

            // 4. 执行请求并获取响应
            try (okhttp3.Response response = CLIENT.newCall(request)
                                                   .execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body()
                                   .string(); // 返回响应体
                } else {
                    throw new RuntimeException(
                            "Unexpected response: " + response.code() + ", message: " + response.message());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error performing POST Form request", e);
        }
    }
}
