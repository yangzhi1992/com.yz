package com.commons.api.test;

import okhttp3.*;

public class OkHttpPostJsonExample {
    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // JSON 数据
        String json = "{\"title\":\"foo\",\"body\":\"bar\",\"userId\":1}";
        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Response Body: " + response.body().string());
            }
        }
    }
}
