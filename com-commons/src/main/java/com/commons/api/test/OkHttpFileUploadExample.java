package com.commons.api.test;

import okhttp3.*;

import java.io.File;

public class OkHttpFileUploadExample {
    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // 设置文件
        File file = new File("path/to/your/file.jpg");
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/jpeg"));

        // 构建 Multipart 请求
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "file.jpg", fileBody)
                .addFormDataPart("description", "This is an image file")
                .build();

        Request request = new Request.Builder()
                .url("https://example.com/upload")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Upload Success: " + response.body().string());
            }
        }
    }
}
