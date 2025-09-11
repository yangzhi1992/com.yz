package com.commons.api.test;

import okhttp3.*;

import java.io.IOException;

public class OkHttpAsyncExample {
    public static void main(String[] args) {
        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        // 构建请求对象
        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts/1")
                .get()
                .build();

        // 异步执行请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 请求失败
                System.err.println("Request Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 请求成功
                    System.out.println("Response Body: " + response.body().string());
                } else {
                    System.out.println("Request Failed: " + response.code());
                }
            }
        });
    }
}
