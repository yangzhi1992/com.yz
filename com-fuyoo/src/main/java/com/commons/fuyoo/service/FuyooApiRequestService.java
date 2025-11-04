package com.commons.fuyoo.service;

import com.commons.fuyoo.dto.HttpRequestEntity;
import com.commons.fuyoo.dto.HttpRequestResponse;
import io.netty.channel.Channel;
import java.util.List;

public interface FuyooApiRequestService extends HttpRequestService {

    void scheduleTask(HttpRequestEntity requestEntity, Channel channel, String requestType, int delay);

    void cancelTask(String path, String ipAndPort, String method);

    String statistis(List<HttpRequestResponse> successList, List<?> requestResultList);

}
