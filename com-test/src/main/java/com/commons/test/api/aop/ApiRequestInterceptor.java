package com.commons.test.api.aop;

import com.google.common.collect.Maps;
import com.commons.api.retrofit.RequestInterceptor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component("apiRequestInterceptor")
public class ApiRequestInterceptor implements RequestInterceptor {

    @Override
    public Request preHandle(Request request) {
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.equals("GET", request.method())) {
            HttpUrl url = request.url();
            Set<String> names = url.queryParameterNames();
            names.forEach(name -> params.put(name, url.queryParameter(name)));
            HttpUrl.Builder builder = new HttpUrl.Builder().scheme(url.scheme()).host(url.host())
                    .port(url.port()).fragment(url.fragment()).username(url.username())
                    .password(url.password());
            List<String> pathSegments = url.pathSegments();
            pathSegments.forEach(builder::addPathSegment);
            params.forEach((k, v) -> builder.addQueryParameter(k, v.toString()));
            return request.newBuilder().url(builder.build()).build();
        }
        if (request.body() instanceof FormBody) {
            FormBody formBody = (FormBody)request.body();
            for (int i = 0; i < formBody.size(); i++) {
                params.put(formBody.name(i), formBody.value(i));
            }
        }
        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> builder.add(k, v.toString()));
        return request.newBuilder().post(builder.build()).build();
    }
}
