package com.commons.cache.springcache.cffenine;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private Long id;
    private String name;
}
