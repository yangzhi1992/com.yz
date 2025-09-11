package com.commons.common.utils;

import com.commons.common.support.FrameworkConstants;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 */
public final class RequestTool {

    private final static String DEFAULT_IP = "127.0.0.1";

    private RequestTool() {

    }

    @Deprecated
    public static String getRemoteHost() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? DEFAULT_IP : ip;
    }

    /**
     * 将HTTP请求转换为字符,不忽略过长参数
     */
    public static String getRequestParameters(HttpServletRequest request) {
        return getRequestParameters(request, FrameworkConstants.NEXT_DELIM, false);
    }

    /**
     * 将HTTP请求转换为字符
     *
     * @param ignoreTooLong 是否忽略过长的请求参数
     */
    public static String getRequestParameters(HttpServletRequest request, String spliter,
            boolean ignoreTooLong) {
        StringBuilder builder = new StringBuilder(FrameworkConstants.STR_INIT_SIZE);

        final Map<String, String[]> parameterMap = request.getParameterMap();
        if (CollectionTool.isBlank(parameterMap)) {//基本上不会发生,防御性判断
            return builder.toString();
        }
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (builder.length() > FrameworkConstants.MAX_LOG_MSG_LENGTH) {
                break;
            }
            builder.append(entry.getKey());
            builder.append(':');
            String value = Arrays.toString(entry.getValue());
            if (!ignoreTooLong || value.length() < FrameworkConstants.MAX_REQUEST_PARA_LENGTH) {
                builder.append(value);
            } else {
                builder.append("ignoreTooLong");
            }
            builder.append(spliter);
        }

        return builder.toString();
    }

    public static Map<String, String> getParameterAsMap(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();

        final Map<String, String[]> parameterMap = request.getParameterMap();
        if (CollectionTool.isBlank(parameterMap)) {//基本上不会发生,防御性判断
            return result;
        }
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            result.put(entry.getKey(), StringTool.join(entry.getValue()));
        }

        return result;
    }

    public static String getRequestHeaders(HttpServletRequest request, String spliter) {
        StringBuilder messageBuilder = new StringBuilder(50);
        Enumeration<String> headerEnumeration = request.getHeaderNames();
        while (headerEnumeration.hasMoreElements()) {
            String header = headerEnumeration.nextElement();
            String value = request.getHeader(header);
            messageBuilder.append(header).append(':').append(value).append(spliter);
        }
        return messageBuilder.toString();
    }

    public static Map<String, String> getHeaderAsMap(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>(6);
        Enumeration<String> headerEnumeration = request.getHeaderNames();
        while (headerEnumeration.hasMoreElements()) {
            String header = headerEnumeration.nextElement();
            String value = request.getHeader(header);
            result.put(header, value);
        }
        return result;
    }

    public static String getHeader(HttpServletRequest request,
            String name) {
        return request.getHeader(name);
    }
}
