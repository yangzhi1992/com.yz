package com.commons.es.simple.props;

import com.commons.es.simple.dto.ElasticConfigItem;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by allan on 17/5/11.
 */
@ConfigurationProperties
public class ElasticSearchProperties {

    private Map<String, ElasticConfigItem> elastic = new HashMap<>();

    public Map<String, ElasticConfigItem> getElastic() {
        return elastic;
    }

    public void setElastic(Map<String, ElasticConfigItem> elastic) {
        this.elastic = elastic;
    }

}
