package com.commons.common.exception;

/**
 *
 */
public class JsonException extends RuntimeException {

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(String message) {
        super(message);
    }
}
