package com.commons.api.test;

import okhttp3.*;

import java.io.IOException;

public class OkHttpInterceptorExample {
    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        // 添加统一请求头
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer your_token_here")
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                })
                .build();

        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }
}
