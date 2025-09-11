package com.commons.api.test;

import okhttp3.*;

public class OkHttpPostFormExample {
    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // 表单参数
        FormBody body = new FormBody.Builder()
                .add("username", "admin")
                .add("password", "123456")
                .build();

        Request request = new Request.Builder()
                .url("https://example.com/login")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Response Body: " + response.body().string());
            }
        }
    }
}
