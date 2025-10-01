package com.commons.es.simple.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

/**
 * ES 单条数据
 */
public interface EsDoc {
    @JsonIgnore
    Serializable getPk();
}
