package com.commons.api.aop;

import com.commons.api.retrofit.ResponseInterceptor;
import com.commons.exception.NetworkException;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.springframework.stereotype.Component;

/**
 * api拦截器
 */
@Component("apiResponseInterceptor")
public class ApiResponseInterceptor implements ResponseInterceptor {

    /**
     * 拦截处理
     *
     * @param code 状态码
     * @param message 提示信息
     * @param headers 请求头
     * @param response 响应
     * @param request 请求
     * @return void
     * @author zhangyan02@qiyi.com
     * @date 2019/7/24 16:46
     */
    @Override
    public void preHandle(int code, String message, Headers headers, ResponseBody response, Request request) {
        String result = null;
        String url = request.url().toString();
        String param = null;
        try {
            result = response.string();
            if (null != request.body()) {
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                param = buffer.readUtf8();
            }
        } catch (IOException e) {
        }
        if (!isSuccessful(code)) {
            throw new NetworkException(message);
        }
    }

    /**
     *
     */
    private boolean isSuccessful(int code) {
        return code >= 200 && code < 300;
    }

}
