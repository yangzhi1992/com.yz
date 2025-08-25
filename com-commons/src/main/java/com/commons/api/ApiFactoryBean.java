package com.commons.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.commons.api.retrofit.RequestInterceptor;
import com.commons.api.retrofit.ResponseInterceptor;
import com.commons.api.retrofit.Retrofit;
import com.commons.api.retrofit.converter.jackson.JacksonConverterFactory;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 *
 */
public class ApiFactoryBean<T> implements FactoryBean<T>, EnvironmentAware {

    private final Class<T> interfaceClass;

    private final String baseUrl;

    private ObjectMapper objectMapper;

    private OkHttpClient originalClient;

    private RequestInterceptor requestInterceptor;

    private ResponseInterceptor responseInterceptor;

    private Environment environment;

    public ApiFactoryBean(Class<T> clz, String baseUrl) {
        this.interfaceClass = clz;
        this.baseUrl = baseUrl;
    }

    @Override
    public T getObject() throws Exception {
        OkHttpClient okHttpClient = this.originalClient.newBuilder()
                .addInterceptor(new SimpleInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .responseErrorHandler(responseInterceptor)
                .environment(environment)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        return retrofit.create(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return this.interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    final class SimpleInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            return chain.proceed(requestInterceptor.preHandle(request));
        }
    }

    public void setRequestInterceptor(RequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
    }

    public void setResponseInterceptor(ResponseInterceptor responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setOriginalClient(OkHttpClient originalClient) {
        this.originalClient = originalClient;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
