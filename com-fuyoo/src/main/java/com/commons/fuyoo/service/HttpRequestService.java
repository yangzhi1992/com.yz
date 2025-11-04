package com.commons.fuyoo.service;

import com.commons.common.utils.BooleanPair;
import com.commons.fuyoo.dto.HttpRequestEntity;
import com.commons.fuyoo.dto.HttpRequestResponse;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public interface HttpRequestService {

    long DEFAULT_TIMEOUT = 5000L;

    List<?> batchRequest(HttpRequestEntity httpRequestEntity)
            throws InterruptedException, ExecutionException, TimeoutException;

    default List<HttpInvoker> buildInvokers(HttpRequestEntity requestEntity, String serviceType) {
        if (requestEntity.getIps() == null) {
            return new ArrayList<>();
        }

        return requestEntity.getIps().stream().filter(ipAndPort -> !StringUtils.isBlank(ipAndPort))
                .map(ipAndPort -> buildInvoker(
                        ipAndPort,
                        requestEntity.getPath(),
                        requestEntity.getMethod(),
                        requestEntity.getParamsStr(),
                        requestEntity.getHeaders(),
                        serviceType)
                ).collect(Collectors.toList());
    }

    default HttpInvoker buildInvoker(String ipAndPort, String path, String method, String paramStr,
                                     Map<String, String> headers, String serviceType) {
        String[] ipAndPortArr = ipAndPort.split(",");
        String[] ipArr = ipAndPortArr[0].split(":");

        String host = "";
        String port = "";

        host = ipArr[0].trim();
        if (serviceType.equals("api")) {
            if (ipArr.length == 1) {
                port = "80";
            } else {
                port = ipArr[1].trim();
            }
        } else {
            if (ipAndPortArr.length > 1) {
                port = ipAndPortArr[1].trim();
            }
        }
        return HttpInvoker.builder()
                .url(String.format("http://%s:%s%s", host, port, path.trim()))
                .method(method)
                .params(paramStr)
                .timeout(DEFAULT_TIMEOUT)
                .headers(headers)
                .build();
    }

    BooleanPair<List> postProcessResponse(List<HttpRequestResponse> successList, List<String> hostIpList);
}