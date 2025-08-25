
package com.commons.api.retrofit;

public class HttpException extends RuntimeException {

    private final int code;
    private final String message;

    public HttpException(int code, String message) {
        super("HTTP " + code + " " + message);
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
