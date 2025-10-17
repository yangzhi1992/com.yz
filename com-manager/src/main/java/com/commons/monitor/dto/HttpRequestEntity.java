package com.commons.monitor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequestEntity {
    private String method;
    private String host;
    private String path;
    private String paramsStr;
    private List<String> ips;
    private String requestType;
    private String qaeApp;
    private Map<String, String> headers;
}
