package com.commons.fuyoo.service;

import java.util.List;

public interface FuyooManageRequestService extends HttpRequestService {

    List<String> getHostIps(String requestType, String host, String qaeApp);
}
