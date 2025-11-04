package com.commons.fuyoo.service;

import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_POST;

import com.commons.common.utils.RandomGenerator;
import com.commons.fuyoo.dto.HttpRequestResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Data;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Builder
@Data
public class HttpInvoker implements Invoker {

    public static final String TRACE_ID_NAME = "X-B3-TraceId";
    public static final String SPAN_ID_NAME = "X-B3-SpanId";
    public static final String PARENT_ID_NAME = "X-B3-ParentSpanId";
    public static final String SAMPLED_NAME = "X-B3-Sampled";

    /**
     * 连接超时（毫秒）
     */
    private static final long CONNECT_TIMEOUT = 2500L;

    /**
     * 默认读超时（毫秒）
     */
    private static final long DEFAULT_TIMEOUT = 2500L;

    private static final OkHttpClient CLIENT;

    static {
        CLIENT = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    private String method;

    private String url;

    private String params;

    private Map<String, String> headers;

    private long timeout;


    @Override
    public HttpRequestResponse invoke() {
        HttpRequestResponse result = new HttpRequestResponse();

        String decodeParams = params;
        if (params != null) {
            decodeParams = new String(Base64.getDecoder().decode(params));
        }

        String uriParams = String.format("%s?%s", url, (decodeParams == null? "" : decodeParams));
        HttpUrl httpUrl = HttpUrl.parse(uriParams);
        if (httpUrl == null) {
            result.setHost(url);
            result.setSuccess(false);
            result.setException("请求参数有误，无法解析:" + uriParams);
            return result;
        }

        String host = httpUrl.host() + ":" + httpUrl.port();
        result.setHost(host);

        Request.Builder reqBuilder;

        if (METHOD_POST.equals(method)) {
            FormBody formBody = null;
            FormBody.Builder builder = new FormBody.Builder();
            Optional.ofNullable(httpUrl.queryParameterNames()).ifPresent(
                    params -> params.forEach(name -> builder.add(name, Objects.requireNonNull(httpUrl.queryParameter(name)))));
            formBody = builder.build();

            reqBuilder = new Request.Builder().url(url).method(method, formBody);
        } else {
            reqBuilder = new Request.Builder().url(httpUrl).method(method, null);
        }

        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                reqBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        String traceId = addTraceHeader(reqBuilder);

        Request request = reqBuilder.build();
        OkHttpClient okHttpClient = CLIENT.newBuilder().readTimeout(timeout, TimeUnit.MILLISECONDS).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            long sendTime = response.sentRequestAtMillis();
            long receiveTime = response.receivedResponseAtMillis();

            Map<String, String> headers = new HashMap<>();
            String headerText = this.headerBody(response, headers);

            result.setStatus(response.code());
            result.setHeaders(headers);
            result.setHeaderText(headerText);
            result.setSuccess(true);
            result.setReceiveTime(receiveTime);
            result.setDuration(receiveTime - sendTime);
            result.setTraceId(traceId);
            result.setResponse(response.body() == null ? "" : response.body().string());
        } catch (IOException e) {
            result.setStatus(-1);
            result.setSuccess(false);
            result.setException(e.getMessage());
        }
        return result;
    }

    private String addTraceHeader(Request.Builder reqBuilder) {
        String traceId = createTraceId();
        reqBuilder.addHeader(TRACE_ID_NAME, traceId);
        reqBuilder.addHeader(PARENT_ID_NAME, createSpanId());
        reqBuilder.addHeader(SAMPLED_NAME, "1");
        return traceId;
    }

    private String createTraceId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private String createSpanId() {
        return RandomGenerator.next();
    }

    private String headerBody(Response response, Map<String, String> headers) {
        StringBuilder headerText = new StringBuilder();

        response.headers().names().forEach(name -> {
            headers.put(name, response.header(name, ""));
            headerText.append(String.format("%s: %s\n", name, response.header(name, "")));
        });
        return headerText.toString();
    }
}
