package com.commons.support;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class FrameworkConstants {

    /**
     * 最大的异常消息长度
     */
    public final static int MAX_LOG_MSG_LENGTH = 5000;

    /**
     * 记录异常日志时,判断请求参数是否过大的标准
     */
    public final static int MAX_REQUEST_PARA_LENGTH = 500;

    /**
     * 异常堆栈的最大深度
     */
    public final static int MAX_STACK_DEPTH = 20;

    /**
     * 信息自定义分割符,用于日志分割
     */
    public final static String ALL_DELIM = "QQXXQQ";

    /**
     * 内容自定义换行分割符
     */
    public final static String NEXT_DELIM = "QQRRQQ";

    /**
     * 内容自定义换行分割符
     */
    public final static String TICK_DELIM = "QQTTQQ";

    /**
     * 初始化的StringBuilder容量,减少System.arraycopy
     */
    public final static int STR_INIT_SIZE = 60;


    public final static Map<String, String> EMPTY = new HashMap<>();

    /**
     * redis成功返回值
     */
    public final static String OK = "OK";

    /**
     * lazy配置前缀
     */
    public final static String LAZY_PREFIX = "lazy";

    /**
     * 当服务器要求密码认证,而客户端未提供时,redis的错误消息
     */
    public final static String REDIS_AUTH_ERROR_MSG = "NOAUTH Authentication required";
}
