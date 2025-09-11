package com.commons.api.retrofit.converter.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.commons.api.retrofit.Converter;
import com.commons.api.retrofit.Retrofit;
import com.commons.common.support.ReturnValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public final class JacksonConverterFactory extends Converter.Factory {

    public static JacksonConverterFactory create() {
        return create(new ObjectMapper());
    }

    public static JacksonConverterFactory create(ObjectMapper mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper == null");
        }
        return new JacksonConverterFactory(mapper);
    }

    private final ObjectMapper mapper;

    private JacksonConverterFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
            Retrofit retrofit) {
        ObjectReader reader = mapper.readerFor(getJacksonType(type));
        return new JacksonResponseBodyConverter<>(reader);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
            Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        ObjectWriter writer = mapper.writerFor(getJacksonType(type));
        return new JacksonRequestBodyConverter<>(writer);
    }

    private JavaType getJacksonType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            boolean isSingleArg = (typeArguments != null && typeArguments.length == 1);
            if ((rawType == ReturnValue.class) && isSingleArg
                    && (typeArguments[0] instanceof ParameterizedType)) {
                ParameterizedType innerParameterizedType = (ParameterizedType) typeArguments[0];
                Type innerRawType = innerParameterizedType.getRawType();
                Class innerClass = (Class) innerRawType;
                Type[] innerTypeArguments = innerParameterizedType.getActualTypeArguments();
                if (innerTypeArguments != null && innerTypeArguments.length == 1
                        && (innerTypeArguments[0] instanceof Class)) {
                    Class valueType = (Class) innerTypeArguments[0];
                    if (List.class.isAssignableFrom(innerClass)) {
                        JavaType listType =
                                mapper.getTypeFactory()
                                        .constructParametricType(ArrayList.class, valueType);

                        return
                                mapper
                                        .getTypeFactory()
                                        .constructParametricType(ReturnValue.class, listType);
                    } else if (Set.class.isAssignableFrom(innerClass)) {
                        JavaType setType = mapper.getTypeFactory()
                                .constructParametricType(HashSet.class, valueType);

                        return
                                mapper
                                        .getTypeFactory()
                                        .constructParametricType(ReturnValue.class, setType);
                    }
                }
            }
        }

        return mapper.getTypeFactory().constructType(type);
    }
}
