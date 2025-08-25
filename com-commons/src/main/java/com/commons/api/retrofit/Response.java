
package com.commons.api.retrofit;

import javax.annotation.Nullable;
import okhttp3.Headers;
import okhttp3.ResponseBody;


public final class Response<T> {

    private final okhttp3.Response rawResponse;

    private final String rawBody;

    private final
    @Nullable
    T body;

    private final
    @Nullable
    ResponseBody errorBody;

    public Response(okhttp3.Response rawResponse, String rawBody, @Nullable T body,
            @Nullable ResponseBody errorBody) {
        this.rawResponse = rawResponse;
        this.rawBody = rawBody;
        this.body = body;
        this.errorBody = errorBody;
    }

    public okhttp3.Response raw() {
        return rawResponse;
    }

    public int code() {
        return rawResponse.code();
    }

    public String message() {
        return rawResponse.message();
    }

    public Headers headers() {
        return rawResponse.headers();
    }

    public boolean isSuccessful() {
        return rawResponse.isSuccessful();
    }

    public
    @Nullable
    T body() {
        return body;
    }

    public
    @Nullable
    ResponseBody errorBody() {
        return errorBody;
    }

    @Override
    public String toString() {
        return "{code="
                + code()
                + ", message="
                + message()
                + ", body="
                + rawBody
                + ", url="
                + rawResponse.request().url()
                + '}';
    }
}
