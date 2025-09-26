package com.commons.gateway;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BackendServiceDTO {
    private String id;
    private String host;
    private int port;
    private boolean healthy = true;
    private long lastChecked;
    private String key;
    private String predicates;

    @JsonIgnore
    public String getUrl() {
        return "http://" + host + ":" + port;
    }
}