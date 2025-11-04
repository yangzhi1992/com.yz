package com.commons.fuyoo.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
