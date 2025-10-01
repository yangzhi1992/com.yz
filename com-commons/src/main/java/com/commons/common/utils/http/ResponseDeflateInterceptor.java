package com.commons.common.utils.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**

 * 解压缩返回值
 */
public class ResponseDeflateInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if ("deflate".equalsIgnoreCase(response.header("Content-Encoding"))) {
            try {
                byte[] decompressed = decompress(response.body()
                                                         .bytes());
                return response.newBuilder()
                               .removeHeader("Content-Encoding")
                               .body(ResponseBody.create(decompressed, response.body()
                                                                               .contentType()))
                               .build();
            } catch (Exception e) {
                throw new IOException("Deflate decompression failed", e);
            }
        }
        return response;
    }

    // 方法1：先尝试zlib格式，再尝试raw deflate
    public static byte[] decompress(byte[] data) throws DataFormatException {
        if (data.length >= 2 && data[0] == 0x78 && data[1] == 0x9C) {
            return decompressInternal(data, true);
        } else {
            return decompressInternal(data, false);
        }
    }

    private static byte[] decompressInternal(byte[] data, boolean nowrap) throws DataFormatException {
        Inflater inflater = new Inflater(nowrap);
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } finally {
            inflater.end();
        }
    }
}
