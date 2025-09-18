package com.commons.cache.springcache.java.caffenine;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private Long id;
    private String name;
}
