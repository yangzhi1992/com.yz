package com.commons.utils;

import java.io.Serializable;
import lombok.Getter;

/**
 *
 */
@Getter
public class StatusCode implements Serializable {

    private static final long serialVersionUID = -6444527753333016714L;

    public final static StatusCode SUCCESS = new StatusCode("A00000", "正常", true);
    public final static StatusCode EMPTY = new StatusCode("A00002", "没有数据", true);
    public final static StatusCode AE_ERR = new StatusCode("A00401", "请登录后操作", true);

    public final static StatusCode INTERNAL_ERROR = new StatusCode("E00000", "系统内部错误");
    public final static StatusCode PARAM_ERROR = new StatusCode("E00001", "参数错误");
    public final static StatusCode AUTHORIZE_ERROR = new StatusCode("E00002", "授权失败");
    public final static StatusCode LIMIT_ERROR = new StatusCode("E00003", "访问限速");
    public final static StatusCode URI_ERR = new StatusCode("E10002", "接口不存在");
    public final static StatusCode SERVER_ERR = new StatusCode("E10003", "操作失败");
    public final static StatusCode HTTP_FAIL = new StatusCode("S00001", "接口访问失败");
    public final static StatusCode CIRCUIT_BREAK_ERROR = new StatusCode("S00002", "熔断");
    private final String code;

    private final String message;

    private final boolean success;

    private StatusCode(String code, String message) {
        this.code = code;
        this.message = message;
        this.success = false;
    }

    private StatusCode(String code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }


}
