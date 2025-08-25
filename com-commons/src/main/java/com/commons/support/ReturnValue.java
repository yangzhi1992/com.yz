package com.commons.support;

import com.commons.utils.StatusCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.commons.utils.JsonTool;
import java.io.Serializable;

/**
 *
 */
@JsonInclude
public class ReturnValue<T> implements Serializable {

    private static final long serialVersionUID = 7456545854079110387L;

    private String code;

    private T data;

    private String msg;

    public ReturnValue() {
    }

    public ReturnValue(T data, String code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public static ReturnValue renderSuccess() {
        return renderSuccess(null);
    }

    public static <T> ReturnValue<T> renderSuccess(T data) {
        return renderSuccess(data, StatusCode.SUCCESS.getMessage());
    }

    public static <T> ReturnValue<T> renderSuccess(T data, String msg) {
        return new ReturnValue<>(data, StatusCode.SUCCESS.getCode(), msg);
    }

    public static ReturnValue renderFailure() {
        return renderFailure(StatusCode.INTERNAL_ERROR.getMessage());
    }

    public static ReturnValue renderFailure(String msg) {
        return renderFailure(StatusCode.INTERNAL_ERROR.getCode(), msg);
    }

    public static ReturnValue renderFailure(StatusCode statusCode) {
        return renderFailure(statusCode.getCode(), statusCode.getMessage());
    }

    public static ReturnValue renderFailure(String customizeCode, String msg) {
        return new ReturnValue<>(null, customizeCode, msg);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @JsonIgnore
    public boolean isSuccessful() {
        return StatusCode.SUCCESS.getCode()
                                 .equals(this.code);
    }

    @Override
    public String toString() {
        return JsonTool.toJSONString(this);
    }

}
