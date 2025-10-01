package com.commons.common.utils;

/**
 *
 */
public final class BooleanPairUtil {

    private BooleanPairUtil() {

    }

    public static <T> BooleanPair<T> success() {
        return new BooleanPair<>(true, null, "成功");
    }

    public static <T> BooleanPair<T> success(String msg, T data) {
        return new BooleanPair<>(true, data, msg);
    }

    public static <T> BooleanPair<T> success(T data) {
        return new BooleanPair<>(true, data, "成功");
    }

    public static <T> BooleanPair<T> instance(boolean success, String msg, T data) {
        return new BooleanPair<>(success, data, msg);
    }

    public static <T> BooleanPair<T> instance(boolean success, String msg) {
        return new BooleanPair<>(success, null, msg);
    }

    public static <T> BooleanPair<T> error(String msg, T data) {
        return new BooleanPair<>(false, data, msg);
    }

    public static <T> BooleanPair<T> error(String msg) {
        return new BooleanPair<>(false, null, msg);
    }

    public static <T> boolean isSuccess(BooleanPair<T> ret) {
        return ret != null && ret.isSuccess();
    }
}
