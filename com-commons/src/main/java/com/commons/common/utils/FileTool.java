package com.commons.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class FileTool {
    // 需要添加依赖：org.apache.commons:commons-io
    public static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        return IOUtils.toByteArray(inputStream);
    }

    public static byte[] inputStreamToByteArray2(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024]; // 缓冲区大小
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
