package com.commons.api.retrofit;

import okhttp3.Request;

/**
 * @author allan
 * @version V1.0
 * @Description: okhttp自定义请求
 * @date 18/5/2 下午4:47
 */
public interface RequestInterceptor {

    Request preHandle(Request request);

    class Default implements RequestInterceptor {

        @Override
        public Request preHandle(Request request) {
            return request;
        }
    }
}
