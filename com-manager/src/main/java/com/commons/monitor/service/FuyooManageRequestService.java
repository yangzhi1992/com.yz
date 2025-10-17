package com.commons.monitor.service;

import java.util.List;

public interface FuyooManageRequestService extends HttpRequestService {

    List<String> getHostIps(String requestType, String host, String qaeApp);
}
