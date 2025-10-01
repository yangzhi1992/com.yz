package com.commons.common.utils;

import java.io.Serializable;

/**
 *
 */
public class BooleanPair<T> implements Serializable {
    private static final long serialVersionUID = -6508733820418231251L;

    public final static BooleanPair FAIL = new BooleanPair();


    private boolean success;

    private String message;

    private T result;

    public BooleanPair(boolean success, T result) {
        this(success, result, null);
    }


    public BooleanPair(boolean success, T result, String message) {
        this.success = success;
        this.result = result;
        this.message = message;
    }

    public BooleanPair(T result) {
        this(true, result);
    }

    public BooleanPair() {
        this(false, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
