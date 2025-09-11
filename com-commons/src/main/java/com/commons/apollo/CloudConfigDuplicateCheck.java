package com.commons.apollo;

import com.commons.utils.JsonTool;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

/**
 * 查看应用下/env中同一个key在不同namespace中重复的记录
 */
public class CloudConfigDuplicateCheck {

    public static void main(String[] args) throws Exception {
        // 线上 ip:管理端口
        String host = "10.132.4.85:9080";

        String response = new OkHttpClient().newCall(new Request.Builder()
                .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:DPCpV7".getBytes(StandardCharsets.UTF_8)))
                .url("http://" + host + "/env")
                .get()//默认就是GET请求，可以不写
                .build()).execute().body().string();
        // 输出所有重复的 key 存在的namespace
        duplicateKeyNamespaces(response, true);
    }

    private static void duplicateKeyNamespaces(String response, boolean localFilter) {
        Map<String, Map> map = JsonTool.parseMap(response);
        Map<String, List<String>> keyNP = new HashMap<>(10000);
        try {
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue() instanceof LinkedHashMap) {
                    String ns = (String) entry.getKey();
                    Map<String, Object> m = (Map<String, Object>) entry.getValue();
                    m.keySet().forEach(k -> {
                        List<String> namespace = keyNP.computeIfAbsent(k, k1 -> new ArrayList<>(10));
                        namespace.add(ns);
                    });
                } else {
                    System.out.println(entry.getValue().getClass().getSimpleName() + "\t" + JsonTool.toJSONString(entry.getValue()));
                }
            }
            keyNP.forEach((k, v) -> {
                if (v.size() > 1) {
                    if (!localFilter || v.stream().anyMatch(s -> s.contains(".yml"))) {
                        System.out.println("[" + v.size() + "]" + k + "\t:\t" + StringUtils.join(v, "\t"));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
