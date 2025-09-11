package com.commons.api.test;

import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class OkHttpFileDownloadExample {
    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://example.com/file-to-download.jpg")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 保存到本地
                File file = new File("downloaded-file.jpg");
                try (InputStream inputStream = response.body().byteStream();
                     FileOutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[2048];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    System.out.println("File downloaded: " + file.getAbsolutePath());
                }
            }
        }
    }
}
