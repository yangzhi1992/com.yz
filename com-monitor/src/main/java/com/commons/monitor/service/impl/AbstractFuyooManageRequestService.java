package com.commons.monitor.service.impl;

import com.commons.monitor.dto.HttpRequestEntity;
import com.commons.monitor.service.FuyooManageRequestService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

abstract class AbstractFuyooManageRequestService implements FuyooManageRequestService {
    @Value("${healthCheck.qce}")
    private String qceList;

    @Override
    public List<String> getHostIps(String requestType, String host, String qaeApp) {
        List<String> ipList = new ArrayList<>();
        if ("qce".equals(requestType)) {
            ipList = Arrays.stream(qceList.split(","))
                           .map(ip -> ip.trim() + ":8080,9080")
                           .collect(Collectors.toList());
        }
        return ipList;
    }

    protected void setAuthorization(HttpRequestEntity requestEntity) {
        requestEntity.setHeaders(Collections.singletonMap("Authorization", "Basic YWRtaW46MzRhdmQ4IzExMjIy"));
    }
}
