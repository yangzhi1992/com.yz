
package com.commons.api.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;


public interface CallAdapter<R, T> {

    T adapt(Call<R> call);


    Type responseType();

    abstract class Factory {

        public abstract
        @Nullable
        CallAdapter<?, ?> get(Type returnType, Annotation[] annotations,
                Retrofit retrofit);


        protected static Type getParameterUpperBound(int index, ParameterizedType type) {
            return Utils.getParameterUpperBound(index, type);
        }


        protected static Class<?> getRawType(Type type) {
            return Utils.getRawType(type);
        }
    }
}
