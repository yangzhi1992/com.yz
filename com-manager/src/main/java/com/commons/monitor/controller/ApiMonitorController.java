package com.commons.monitor.controller;

import com.commons.common.support.ReturnValue;
import com.commons.common.utils.BooleanPair;
import com.commons.common.utils.StringTool;
import com.commons.monitor.dto.ApiRequestResultVO;
import com.commons.monitor.dto.HttpRequestEntity;
import com.commons.monitor.dto.HttpRequestResponse;
import com.commons.monitor.service.FuyooApiRequestService;
import com.commons.monitor.service.FuyooManageRequestService;
import com.commons.monitor.service.NginxEsService;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/apimonitor")
public class ApiMonitorController  {

    @Autowired
    @Qualifier("httpApiRequestService")
    private FuyooApiRequestService httpApiRequestService;

    @Autowired
    @Qualifier("httpManageInfoRequestService")
    private FuyooManageRequestService httpManageInfoRequestService;

    @Autowired
    @Qualifier("httpManageMappingRequestService")
    private FuyooManageRequestService httpManageMappingRequestService;

    @Autowired
    private NginxEsService nginxEsService;

    @RequestMapping("/page")
    public String monitorPage() {
        return "web/api/monitor";
    }

    @RequestMapping("/searchHost")
    @ResponseBody
    public ReturnValue searchHosts(String requestType, String host, String qaeApp)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (StringTool.isBlank(requestType) || StringTool.isBlank(host)) {
            return ReturnValue.renderFailure("参数不正确");
        }

        if (requestType.equals("qae") && StringTool.isBlank(qaeApp)) {
            return ReturnValue.renderFailure("QAE应用未选择");
        }

        List<String> hostIpList = httpManageInfoRequestService.getHostIps(requestType, host, qaeApp);
        if (CollectionUtils.isEmpty(hostIpList)) {
            return ReturnValue.renderFailure("应用或容器不存在");
        }

        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                                                           .requestType(requestType).host(host).qaeApp(qaeApp).ips(hostIpList).build();

        List<?> requestResultList = httpManageInfoRequestService.batchRequest(requestEntity);
        List<HttpRequestResponse> hostServiceInfoList = requestResultList.stream()
                                                                         .map(result -> (HttpRequestResponse) result)
                                                                         .collect(Collectors.toList());

        BooleanPair<List>
                listBooleanPair = httpManageInfoRequestService.postProcessResponse(hostServiceInfoList, hostIpList);
        return ReturnValue.renderSuccess(listBooleanPair.getResult());
    }

    @RequestMapping("/apiBatchRequest")
    @ResponseBody
    public ReturnValue<ApiRequestResultVO> apiBatchRequest(String host, String path, String method, String param, String ips)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (StringTool.isBlank(host) || StringTool.isBlank(path) || StringTool.isBlank(ips) || StringTool.isBlank(method)) {
            return ReturnValue.renderFailure("域名、请求方法、接口url、节点IP等参数不正确！");
        }

        List<String> ipList = Arrays.stream(ips.trim().split(","))
                .map(String::trim).filter(StringTool::isNotBlank).collect(Collectors.toList());

        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                .host(host).method(method).path(path).paramsStr(param).ips(ipList)
                .build();

        List<?> requestResultList = httpApiRequestService.batchRequest(requestEntity);
        List<HttpRequestResponse> successList = requestResultList.stream()
                .filter(result -> ((HttpRequestResponse) result).isSuccess())
                .map(result -> (HttpRequestResponse) result)
                .collect(Collectors.toList());

        BooleanPair<List> comparePair = httpApiRequestService.postProcessResponse(successList, null);
        String statistis = httpApiRequestService.statistis(successList, requestResultList);

        ApiRequestResultVO resultVO = ApiRequestResultVO.builder()
                                                        .checkRequestResponseList(requestResultList)
                                                        .statistis(statistis)
                                                        .allsuccess(successList.size() == requestResultList.size())
                                                        .contentSame(comparePair.isSuccess())
                                                        .normalHosts(comparePair.getResult())
                                                        .build();

        return ReturnValue.renderSuccess(resultVO);
    }

    @RequestMapping("/suggestParams")
    @ResponseBody
    public ReturnValue suggestParams(String host, String path, String method) throws IOException {
        List<Map<String, String>>  data = nginxEsService.searchParamsLatest(host, path, method);
        return ReturnValue.renderSuccess(data);
    }

    @RequestMapping("/suggestUriV2")
    @ResponseBody
    public ReturnValue suggestUriV2() throws InterruptedException, ExecutionException, TimeoutException {
        List<String> hostIpList = httpManageMappingRequestService.getHostIps("qce", null, null);
        if (CollectionUtils.isEmpty(hostIpList)) {
            return ReturnValue.renderFailure("应用或容器不存在");
        }

        HttpRequestEntity requestEntity = HttpRequestEntity.builder().ips(hostIpList).build();

        List<?> requestResultList = httpManageMappingRequestService.batchRequest(requestEntity);
        List<HttpRequestResponse> successList = requestResultList.stream()
                .filter(result -> ((HttpRequestResponse) result).isSuccess())
                .map(result -> (HttpRequestResponse) result)
                .collect(Collectors.toList());
        BooleanPair<List> listBooleanPair = httpManageMappingRequestService.postProcessResponse(successList, hostIpList);
        return ReturnValue.renderSuccess(listBooleanPair.getResult());
    }

    @RequestMapping("/submitScheduleTask")
    @ResponseBody
    public ReturnValue scheduleTask(String ip, String method, String path, String param, String host, int delay) {
        if (StringTool.isBlank(ip) || StringTool.isBlank(method) || StringTool.isBlank(path)) {
            return ReturnValue.renderFailure("请求参数不能为空");
        }

        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                .ips(Lists.newArrayList(ip)).method(method).path(path).paramsStr(param)
                .headers(Collections.singletonMap("Host", host))
                .build();
        httpApiRequestService.scheduleTask(requestEntity, null, "api", delay);
        return ReturnValue.renderSuccess(null, "任务提交成功！");
    }

    @RequestMapping("/cancelScheduleTask")
    @ResponseBody
    public ReturnValue cancelTask(String ip, String method, String path) {
        if (StringTool.isBlank(ip) || StringTool.isBlank(method) || StringTool.isBlank(path)) {
            return ReturnValue.renderFailure("请求参数不能为空");
        }
        httpApiRequestService.cancelTask(path, ip, method);
        return ReturnValue.renderSuccess(null, "任务已取消！");
    }
}
