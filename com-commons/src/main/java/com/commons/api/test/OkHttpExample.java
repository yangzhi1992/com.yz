package com.commons.api.test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpExample {
    public static void main(String[] args) throws Exception {
        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        // 构建请求对象
        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts/1") // URL 地址
                .get() // GET 请求 (默认)
                .build();

        // 执行请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 读取响应数据
                System.out.println("Response Body: " + response.body().string());
            } else {
                System.out.println("Request Failed: " + response.code());
            }
        }
    }
}
