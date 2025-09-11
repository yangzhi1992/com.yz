
package com.commons.api.retrofit;

import static com.commons.api.retrofit.Utils.checkNotNull;
import static com.commons.api.retrofit.Utils.throwIfFatal;

import com.commons.common.utils.StringTool;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

public final class
OkHttpCall<T> implements Call<T> {

    private final ServiceMethod<T, ?> serviceMethod;
    private final
    @Nullable
    Object[] args;

    private volatile boolean canceled;

    @GuardedBy("this")
    private
    @Nullable
    okhttp3.Call rawCall;
    private
    @Nullable
    Throwable creationFailure;

    private ResponseInterceptor responseInterceptor;

    @GuardedBy("this")
    private boolean executed;


    OkHttpCall(ServiceMethod<T, ?> serviceMethod, @Nullable Object[] args,
            ResponseInterceptor responseInterceptor) {
        this.serviceMethod = serviceMethod;
        this.args = args;
        this.responseInterceptor = responseInterceptor;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public OkHttpCall<T> clone() {
        return new OkHttpCall<>(serviceMethod, args, responseInterceptor);
    }

    @Override
    public synchronized Request request() {
        okhttp3.Call call = rawCall;
        if (call != null) {
            return call.request();
        }
        if (creationFailure != null) {
            if (creationFailure instanceof IOException) {
                throw new RuntimeException("Unable to create request.", creationFailure);
            } else if (creationFailure instanceof RuntimeException) {
                throw (RuntimeException) creationFailure;
            } else {
                throw (Error) creationFailure;
            }
        }
        try {
            return (rawCall = createRawCall()).request();
        } catch (RuntimeException | Error e) {
            throwIfFatal(e);
            creationFailure = e;
            throw e;
        } catch (IOException e) {
            creationFailure = e;
            throw new RuntimeException("Unable to create request.", e);
        }
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        checkNotNull(callback, "callback == null");

        okhttp3.Call call;
        Throwable failure;

        synchronized (this) {
            if (executed) {
                throw new IllegalStateException("Already executed.");
            }
            executed = true;

            call = rawCall;
            failure = creationFailure;
            if (call == null && failure == null) {
                try {
                    call = rawCall = createRawCall();
                } catch (Throwable t) {
                    throwIfFatal(t);
                    failure = creationFailure = t;
                }
            }
        }

        if (failure != null) {
            callback.onFailure(this, failure);
            return;
        }

        if (canceled) {
            call.cancel();
        }

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse) {
                Response<T> response;
                try {
                    response = parseResponse(rawResponse);
                } catch (Throwable e) {
                    throwIfFatal(e);
                    callFailure(e);
                    return;
                }

                try {
                    callback.onResponse(OkHttpCall.this, response);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callFailure(e);
            }

            private void callFailure(Throwable e) {
                try {
                    callback.onFailure(OkHttpCall.this, e);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    @Override
    public synchronized boolean isExecuted() {
        return executed;
    }

    @Override
    public Response<T> execute() throws IOException {
        okhttp3.Call call;

        synchronized (this) {
            if (executed) {
                throw new IllegalStateException("Already executed.");
            }
            executed = true;

            if (creationFailure != null) {
                if (creationFailure instanceof IOException) {
                    throw (IOException) creationFailure;
                } else if (creationFailure instanceof RuntimeException) {
                    throw (RuntimeException) creationFailure;
                } else {
                    throw (Error) creationFailure;
                }
            }

            call = rawCall;
            if (call == null) {
                try {
                    call = rawCall = createRawCall();
                } catch (IOException | RuntimeException | Error e) {
                    throwIfFatal(e);
                    creationFailure = e;
                    throw e;
                }
            }
        }

        if (canceled) {
            call.cancel();
        }
        RequestOptions requestOptions = serviceMethod.requestOptions();
        int i = 0;
        while (true) {
            try {
                return parseResponse(i == 0 ? call.execute() : call.clone().execute());
            } catch (Exception e) {
                i++;
                if (requestOptions != null && i < requestOptions.getRetries()) {
                    continue;
                }
                throw e;
            }
        }
    }


    private okhttp3.Call createRawCall() throws IOException {
        okhttp3.Call call = serviceMethod.toCall(args);
        if (call == null) {
            throw new NullPointerException("Call.Factory returned null.");
        }
        return call;
    }

    Response<T> parseResponse(okhttp3.Response rawResponse) throws IOException {
        ResponseBody rawBody = rawResponse.body();
        int code = rawResponse.code();
        byte[] bodyBytes = rawBody.bytes();
        Headers headers = rawResponse.headers().newBuilder().build();
        String message = rawResponse.message();

        rawResponse = rawResponse.newBuilder()
                .body(new NoContentResponseBody(rawBody.contentType(), rawBody.contentLength()))
                .build();

        ResponseBody bufferedBody = null;
        try {
            bufferedBody = Utils.buffer(rawBody.contentType(), bodyBytes);
            responseInterceptor
                    .preHandle(code, message, headers, bufferedBody, rawResponse.request());
        } catch (Exception e) {
            throw e;
        } finally {
            rawBody.close();
            bufferedBody.close();
        }

        ExceptionCatchingRequestBody catchingBody = new ExceptionCatchingRequestBody(
                Utils.buffer(rawBody.contentType(), bodyBytes));
        try {
            T body = serviceMethod.toResponse(catchingBody);
            return new Response<>(rawResponse, StringTool.toString(bodyBytes), body, null);
        } catch (RuntimeException e) {
            catchingBody.throwIfCaught();
            throw e;
        } finally {
            catchingBody.close();
        }
    }

    @Override
    public void cancel() {
        canceled = true;

        okhttp3.Call call;
        synchronized (this) {
            call = rawCall;
        }
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        if (canceled) {
            return true;
        }
        synchronized (this) {
            return rawCall != null && rawCall.isCanceled();
        }
    }

    public static final class NoContentResponseBody extends ResponseBody {

        private final MediaType contentType;
        private final long contentLength;

        NoContentResponseBody(MediaType contentType, long contentLength) {
            this.contentType = contentType;
            this.contentLength = contentLength;
        }

        @Override
        public MediaType contentType() {
            return contentType;
        }

        @Override
        public long contentLength() {
            return contentLength;
        }

        @Override
        public BufferedSource source() {
            throw new IllegalStateException("Cannot read raw response body of a converted body.");
        }
    }

    static final class ExceptionCatchingRequestBody extends ResponseBody {

        private final ResponseBody delegate;
        IOException thrownException;

        ExceptionCatchingRequestBody(ResponseBody delegate) {
            this.delegate = delegate;
        }

        @Override
        public MediaType contentType() {
            return delegate.contentType();
        }

        @Override
        public long contentLength() {
            return delegate.contentLength();
        }

        @Override
        public BufferedSource source() {
            return Okio.buffer(new ForwardingSource(delegate.source()) {
                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    try {
                        return super.read(sink, byteCount);
                    } catch (IOException e) {
                        thrownException = e;
                        throw e;
                    }
                }
            });
        }

        @Override
        public void close() {
            delegate.close();
        }

        void throwIfCaught() throws IOException {
            if (thrownException != null) {
                throw thrownException;
            }
        }
    }
}
