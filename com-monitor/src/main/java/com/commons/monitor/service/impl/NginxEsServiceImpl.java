package com.commons.monitor.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.commons.common.utils.StringTool;
import com.commons.common.utils.http.OkhttpClientTool;
import com.commons.monitor.service.NginxEsService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NginxEsServiceImpl implements NginxEsService {
    @Value("${spring.elasticsearch.host}")
    private String host2;

    @Value("${spring.elasticsearch.port}")
    private int port;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${elastic.nginx-log.index-name}")
    public String indexName;

    @Override
    public List<Map<String, String>> searchParamsLatest(String host, String uri, String method) throws IOException {
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                                                           .must(QueryBuilders.rangeQuery("timestamp")
                                                                              .from(System.currentTimeMillis()
                                                                                      - 24 * 60 * 60 * 1000)
                                                                              .to(System.currentTimeMillis()));

        if (!StringTool.isBlank(uri)) {
            queryBuilder.must(QueryBuilders.matchQuery("http_uri", uri));
        }
        if (!StringTool.isBlank(host)) {
            queryBuilder.must(QueryBuilders.matchQuery("http_host", host));
        }
        if (!StringTool.isBlank(method)) {
            queryBuilder.must(QueryBuilders.matchQuery("http_method", method));
        }

        String[] esIdx = new String[] {indexName};
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(0)
                .size(30)
                .query(queryBuilder)
                .sort("timestamp", SortOrder.DESC);

        SearchRequest request = new SearchRequest()
                .indices(esIdx)
                .indicesOptions(IndicesOptions.lenientExpandOpen())
                .source(sourceBuilder);

        String url = "http://" + host2 + ":" +
                port + "/" + indexName + "/_search";
        String username = "admin";
        String password = "yICe#9UD";
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder()
                                   .encode(auth.getBytes());
        String authHeaderValue = "Basic " + new String(encodedAuth);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authHeaderValue);
        String result = OkhttpClientTool.performPostJsonRequest(url, null, headers, JSON.parseObject(request.source()
                                                                                                            .toString()),
                null, false);
        // 1. 将整个响应解析为JSON对象
        JSONObject responseJson = JSON.parseObject(result);

        // 2. 使用JSONPath提取所有hits.hits[*]._source.query_params
        Object result2 = JSONPath.eval(responseJson, "$.hits.hits[*]._source.query_params");
        List<Map<String, String>> res = new ArrayList<>();
        // 3. 处理提取结果
        if (result2 instanceof com.alibaba.fastjson2.JSONArray) {
            com.alibaba.fastjson2.JSONArray array = (com.alibaba.fastjson2.JSONArray)result2;
            for (Object item : array) {
                if (item != null) {
                    Map<String, String> map = new HashMap<>();
                    map.put("value", item.toString());
                    res.add(map);
                }
            }
        }

        return res;
    }

}
