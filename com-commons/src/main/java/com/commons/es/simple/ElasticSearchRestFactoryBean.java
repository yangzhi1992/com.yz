package com.commons.es.simple;

import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.FactoryBean;

/**
 * ES highLevel client
 */
public class ElasticSearchRestFactoryBean implements FactoryBean<RestHighLevelClient> {
    private final HttpHost[] addresses;
    private final String username;
    private final String password;
    private final Map<String, String> properties;
    private RestHighLevelClient client;
    private volatile boolean initialized = false;

    public ElasticSearchRestFactoryBean(HttpHost[] addresses, String username, String password,
            Map<String, String> properties) {
        this.addresses = addresses;
        this.username = username;
        this.password = password;
        if (properties == null) {
            properties = Collections.emptyMap();
        }
        this.properties = properties;
    }

    @Override
    public RestHighLevelClient getObject() throws Exception {
        init();
        return client;
    }

    public void init() {
        if (initialized) {
            return;
        }
        // 指定默认超时时间
        int connectTimeout = Integer.parseInt(properties.getOrDefault("connectTimeout", "5000"));
        int requestTimeout = Integer.parseInt(properties.getOrDefault("requestTimeout", "2000"));
        int socketTimeout = Integer.parseInt(properties.getOrDefault("socketTimeout", "30000"));
        RestClientBuilder builder = RestClient.builder(addresses)
                                              .setRequestConfigCallback(
                                                      config -> config.setConnectTimeout(connectTimeout)
                                                                      .setConnectionRequestTimeout(requestTimeout)
                                                                      .setSocketTimeout(socketTimeout))
                                              .setHttpClientConfigCallback(
                                                      httpClientBuilder -> {
                                                          final CredentialsProvider credentialsProvider =
                                                                  new BasicCredentialsProvider();
                                                          if (StringUtils.isNotBlank(username)
                                                                  && StringUtils.isNotBlank(password)) {
                                                              credentialsProvider.setCredentials(AuthScope.ANY,
                                                                      new UsernamePasswordCredentials(username,
                                                                              password));
                                                              httpClientBuilder.setDefaultCredentialsProvider(
                                                                      credentialsProvider);
                                                          }
                                                          return httpClientBuilder;
                                                      }
                                              );
        client = new RestHighLevelClient(builder);
        try {
            // 校验远端集群是否可用
            if (!client.ping(RequestOptions.DEFAULT)) {
                StringBuilder addressBuilder = new StringBuilder(60);
                for (HttpHost address : addresses) {
                    addressBuilder.append(address.getHostName())
                                  .append(":")
                                  .append(address.getPort());
                }
            }
        } catch (Exception e) {

        }
        initialized = true;
    }

    @Override
    public Class<?> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
            }
        }
    }
}
