package com.commons.fuyoo.service.impl;

import com.commons.common.utils.BooleanPair;
import com.commons.common.utils.BooleanPairUtil;
import com.commons.common.utils.JsonTool;
import com.commons.fuyoo.dto.HttpRequestEntity;
import com.commons.fuyoo.dto.HttpRequestResponse;
import com.commons.fuyoo.dto.SpringServiceInfoDTO;
import com.commons.fuyoo.service.BatchHttpRequestService;
import com.commons.fuyoo.service.HttpInvoker;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("httpManageInfoRequestService")
public class FuyooManageInfoRequestServiceImpl extends AbstractFuyooManageRequestService {

    @Autowired
    private BatchHttpRequestService batchHttpRequestService;

    private List<String> javaServiceHosts = Lists.newArrayList("qce", "qae");

    @Override
    public List<?> batchRequest(HttpRequestEntity requestEntity)
            throws InterruptedException, ExecutionException, TimeoutException {
        super.setAuthorization(requestEntity);

        if (!javaServiceHosts.contains(requestEntity.getRequestType())) {
            return requestEntity.getIps().stream()
                    .map(ip -> HttpRequestResponse.builder().host(ip).success(false).build())
                    .collect(Collectors.toList());
        } else {
            requestEntity.setMethod("GET");
            requestEntity.setPath("/info");

            List<HttpInvoker> httpInvokers = buildInvokers(requestEntity, "manage");
            return batchHttpRequestService.invokeHttpRequest(httpInvokers);
        }
    }

    @Override
    public BooleanPair<List> postProcessResponse(List<HttpRequestResponse> successList, List<String> hostIpList) {
        Map<String, String> hostMap = new HashMap<>();
        for (String host : hostIpList) {
            String ip = host.split(":")[0];
            hostMap.put(ip, host.split(",")[0]);
        }

        return BooleanPairUtil.success(
                successList.stream().map(response -> {
                    Map<String, Object> map = new HashMap<>();

                    String[] ipInfo = response.getHost().split(":");
                    map.put("ip", hostMap.getOrDefault(ipInfo[0], ipInfo[0]));

                    if (response.isSuccess()) {
                        String responseStr = response.getResponse();
                        SpringServiceInfoDTO
                                springServiceInfoDTO = JsonTool.parseObject(responseStr, SpringServiceInfoDTO.class);
                        map.put("version", springServiceInfoDTO.getBuild().getVersion());
                        map.put("uptime", String.format("%.1f", springServiceInfoDTO.getUpTime() / 60.0 / 60));
                        map.put("success", true);
                        return map;
                    } else {
                        map.put("success", false);
                    }
                    return map;
                }).collect(Collectors.toList()));
    }
}
