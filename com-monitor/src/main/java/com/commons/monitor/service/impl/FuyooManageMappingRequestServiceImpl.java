package com.commons.monitor.service.impl;

import com.commons.common.utils.BooleanPair;
import com.commons.common.utils.BooleanPairUtil;
import com.commons.common.utils.JsonTool;
import com.commons.monitor.dto.HttpRequestEntity;
import com.commons.monitor.dto.HttpRequestResponse;
import com.commons.monitor.service.BatchHttpRequestService;
import com.commons.monitor.service.HttpInvoker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("httpManageMappingRequestService")
public class FuyooManageMappingRequestServiceImpl extends AbstractFuyooManageRequestService {

    @Autowired
    private BatchHttpRequestService batchHttpRequestService;

    @Override
    public List<?> batchRequest(HttpRequestEntity requestEntity)
            throws InterruptedException, ExecutionException, TimeoutException {
        super.setAuthorization(requestEntity);

        requestEntity.setMethod("GET");
        requestEntity.setPath("/mappings");

        List<HttpInvoker> httpInvokers = buildInvokers(requestEntity, "manage");
        return batchHttpRequestService.invokeHttpRequest(httpInvokers);
    }

    @Override
    public BooleanPair<List> postProcessResponse(List<HttpRequestResponse> successList, List<String> hostIpList) {
        HashSet<String> urlList = new HashSet<>();

        for (HttpRequestResponse mappingResponse: successList) {
            String mappingStr = mappingResponse.getResponse();
            Map map = JsonTool.parseObject(mappingStr, Map.class);
            Object[] urlInfos = map.keySet().toArray(new String[0]);

            for (int i = 0; i < urlInfos.length; i++) {
                String urlInfo = (String) urlInfos[i];
                if (!urlInfo.startsWith("{")) {
                    continue;
                }

                String urlMethod = urlInfo.substring(1, urlInfo.length() - 1);
                String[] urlMethodArr = urlMethod.split(",");
                String url = urlMethodArr[0].substring(1, urlMethodArr[0].length() - 1);
                urlList.add(url);
            }
        }
        return BooleanPairUtil.success(new ArrayList(urlList));
    }
}
