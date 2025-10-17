package com.commons.monitor.service.impl;


import com.commons.common.utils.BooleanPair;
import com.commons.common.utils.StringTool;
import com.commons.monitor.dto.HttpRequestEntity;
import com.commons.monitor.dto.HttpRequestResponse;
import com.commons.monitor.service.BatchHttpRequestService;
import com.commons.monitor.service.FuyooApiRequestService;
import com.commons.monitor.service.HttpInvoker;
import com.commons.monitor.service.SchedulerRequestTaskService2;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("httpApiRequestService")
public class FuyooApiRequestServiceImpl implements FuyooApiRequestService {

    @Autowired
    private BatchHttpRequestService batchHttpRequestService;

    @Autowired
    private SchedulerRequestTaskService2<HttpRequestResponse> schedulerRequestTaskService;

    @Override
    public List<?> batchRequest(HttpRequestEntity requestEntity)
            throws InterruptedException, ExecutionException, TimeoutException {
        requestEntity.setHeaders(Collections.singletonMap("Host", requestEntity.getHost()));
        List<HttpInvoker> httpInvokers = buildInvokers(requestEntity, "api");
        return batchHttpRequestService.invokeHttpRequest(httpInvokers);
    }

    @Override
    public BooleanPair<List> postProcessResponse(List<HttpRequestResponse> successList, List<String> hostIpList) {
        Map<String, List<String>> hashRes = new HashMap<>();

        BooleanPair<List> booleanPair = new BooleanPair<>();
        booleanPair.setSuccess(true);

        successList.forEach(res -> {
            String md5s = "";
            try {
                md5s = Arrays.toString(MessageDigest.getInstance("MD5").digest(res.getResponse().getBytes()));
            } catch (NoSuchAlgorithmException ignored) {
            }

            List<String> hostList = hashRes.getOrDefault(md5s, new ArrayList<>());
            hostList.add(res.getHost());
            hashRes.put(md5s, hostList);
        });

        if (hashRes.size() > 1) {
            List<String> normalHostList = patchResponse(successList, hashRes);

            booleanPair.setSuccess(false);
            booleanPair.setResult(normalHostList);

        } else {
            booleanPair.setResult(hashRes.values().iterator().next());
        }

        return booleanPair;
    }

    private List<String> patchResponse(List<HttpRequestResponse> successList, Map<String, List<String>> hashRes) {
        TreeMap<Integer, List<String>> countHost = new TreeMap<>();
        hashRes.values().forEach(hosts -> countHost.put(hosts.size(), hosts));

        Map.Entry<Integer, List<String>> normalResponseEntry = countHost.lastEntry();
        List<String> normalHostList = normalResponseEntry.getValue();

        List<HttpRequestResponse> normalResponseList = successList.stream()
                .filter(response -> normalHostList.contains(response.getHost())).collect(Collectors.toList());

        HttpRequestResponse normalResponse = normalResponseList.get(0);

        List<String> normalPatches = new ArrayList<>();

        for (HttpRequestResponse response : successList) {
            if (!normalHostList.contains(response.getHost())) {
                Patch<String> responsePatch = DiffUtils.diffInline(normalResponse.getResponse(), response.getResponse());
                for (AbstractDelta<String> delta : responsePatch.getDeltas()) {
                    int sourcePos = delta.getSource().getPosition();
                    int targetPos = delta.getTarget().getPosition();

                    int sourceStartFieldPos = response.getResponse().lastIndexOf(",", sourcePos);
                    int sourceEndFieldPos = response.getResponse().indexOf(",", sourcePos);

                    sourceStartFieldPos = (response.getResponse().charAt(sourcePos) == ',') ?
                            sourceStartFieldPos : sourceStartFieldPos + 1;
                    sourceEndFieldPos = sourceEndFieldPos == -1 ? response.getResponse().length() : sourceEndFieldPos;

                    String sourceField = response.getResponse().substring(sourceStartFieldPos, sourceEndFieldPos);
                    List<String> patches = response.getPatch();
                    if (StringTool.isNotBlank(sourceField)) {
                        if (patches == null) {
                            response.setPatch(Lists.newArrayList(sourceField));
                        } else {
                            if (!patches.contains(sourceField)) {
                                patches.add(sourceField);
                            }
                        }
                    }

                    int targetStartFieldPos = normalResponse.getResponse().lastIndexOf(",", targetPos);
                    int targetEndFieldPos = normalResponse.getResponse().indexOf(",", targetPos);

                    targetStartFieldPos = (response.getResponse().charAt(targetPos) == ',') ?
                            targetStartFieldPos : targetStartFieldPos + 1;
                    targetEndFieldPos = targetEndFieldPos == -1 ? normalResponse.getResponse().length() : targetEndFieldPos;

                    String targetField = normalResponse.getResponse().substring(targetStartFieldPos, targetEndFieldPos);

                    if (StringTool.isNotBlank(targetField) && !normalPatches.contains(targetField)) {
                        normalPatches.add(targetField);
                    }
                }
            }
        }
        normalResponseList.forEach(response -> response.setPatch(normalPatches));
        return normalHostList;
    }


    @Override
    public void scheduleTask(HttpRequestEntity requestEntity, Channel channel, String serviceType, int delay) {
        String ipAndPort = requestEntity.getIps().get(0);
        HttpInvoker httpInvoker = buildInvoker(
                ipAndPort,
                requestEntity.getPath(),
                requestEntity.getMethod(),
                requestEntity.getParamsStr(),
                requestEntity.getHeaders(),
                serviceType);

        String taskName = String.format("%s_%s_%s", requestEntity.getPath(), ipAndPort, requestEntity.getMethod());
        schedulerRequestTaskService.registerTask(httpInvoker::invoke, channel, taskName, delay);
    }

    @Override
    public void cancelTask(String path, String ipAndPort, String method) {
        String taskName = String.format("%s_%s_%s", path, ipAndPort, method);
        schedulerRequestTaskService.unregisterTask(taskName);
    }

    @Override
    public String statistis(List<HttpRequestResponse> successList, List<?> requestResultList) {
        String msg = String.format("%s/%s/%s/%s/%s", requestResultList.size(), requestResultList.size(), 0, 0, 0);
        if (successList.size() > 0) {
            long max = successList.stream().mapToLong(HttpRequestResponse::getDuration).max().getAsLong();
            long min = successList.stream().mapToLong(HttpRequestResponse::getDuration).min().getAsLong();
            double avg = successList.stream().mapToLong(HttpRequestResponse::getDuration).average().getAsDouble();

            msg = String.format("%s/%s/%s/%s/%.1f", successList.size(), successList.size(), max, min, avg);
        }
        return msg;
    }
}
