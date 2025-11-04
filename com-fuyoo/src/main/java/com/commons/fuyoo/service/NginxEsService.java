package com.commons.fuyoo.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface NginxEsService {

    List<Map<String, String>> searchParamsLatest(String host, String uri, String method) throws IOException;
}
