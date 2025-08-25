
package com.commons.api.retrofit.converter.jackson;

import com.fasterxml.jackson.databind.ObjectReader;
import com.commons.api.retrofit.Converter;
import java.io.IOException;
import okhttp3.ResponseBody;

final class JacksonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final ObjectReader adapter;

    JacksonResponseBodyConverter(ObjectReader adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            return adapter.readValue(value.charStream());
        } finally {
            value.close();
        }
    }
}
