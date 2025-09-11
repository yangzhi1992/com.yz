package com.commons.common.exception;

/**
 *
 */
public class BusinessException extends RuntimeException {

    private String code;

    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
