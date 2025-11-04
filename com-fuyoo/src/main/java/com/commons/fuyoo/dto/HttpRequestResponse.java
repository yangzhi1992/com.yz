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
public class HttpRequestResponse {

    private boolean success;

    private int status;

    private long duration;

    private long receiveTime;

    private Map<String, String> headers;

    private String headerText;

    private String response;

    private String exception;

    private String host;

    private String traceId;

    private List<String> patch;
}
