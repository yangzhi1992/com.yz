package com.commons.api.retrofit;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 *
 */
public interface ResponseInterceptor {

    void preHandle(int code, String message, Headers headers, ResponseBody response, Request request);

    class Default implements ResponseInterceptor {

        @Override
        public void preHandle(int code, String message, Headers headers, ResponseBody response,Request request) {
        }
    }
}
