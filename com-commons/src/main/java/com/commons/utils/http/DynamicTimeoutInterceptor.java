package com.commons.utils.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DynamicTimeoutInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // 从请求头中获取超时设置
        String timeoutHeader = request.header("X-Timeout-Seconds");
        if (timeoutHeader != null) {
            int timeout = Integer.parseInt(timeoutHeader);
            return chain.withReadTimeout(timeout, TimeUnit.SECONDS)
                        .withConnectTimeout(timeout, TimeUnit.SECONDS)
                        .proceed(request);
        }

        return chain.proceed(request);
    }
}
