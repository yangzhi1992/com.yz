package com.commons.common.utils;

import com.commons.common.support.FrameworkConstants;
import java.util.Objects;

/**
 *
 */
public class ObjectTool {

    public static <T> T defaultIfNull(T obj, T defaultValue) {
        if (Objects.isNull(obj)) {
            return defaultValue;
        }
        return obj;
    }

    public static <T> Object defaultIfNull(T obj) {
        if (Objects.isNull(obj)) {
            return FrameworkConstants.EMPTY;
        }
        return obj;
    }
}
