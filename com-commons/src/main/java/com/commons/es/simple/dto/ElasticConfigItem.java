package com.commons.es.simple.dto;

import com.commons.es.simple.props.ElasticNode;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ElasticConfigItem implements Serializable {
    private static final long serialVersionUID = 3980547326148342600L;

    private String clusterName;

    private String username;

    private String password;

    private List<ElasticNode> nodes;

    private Map<String, String> properties = new HashMap<>();

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<ElasticNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ElasticNode> nodes) {
        this.nodes = nodes;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
