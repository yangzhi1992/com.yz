package com.commons.monitor.service;

import com.commons.monitor.dto.HttpRequestEntity;
import com.commons.monitor.dto.HttpRequestResponse;
import io.netty.channel.Channel;
import java.util.List;

public interface FuyooApiRequestService extends HttpRequestService {

    void scheduleTask(HttpRequestEntity requestEntity, Channel channel, String requestType, int delay);

    void cancelTask(String path, String ipAndPort, String method);

    String statistis(List<HttpRequestResponse> successList, List<?> requestResultList);

}
