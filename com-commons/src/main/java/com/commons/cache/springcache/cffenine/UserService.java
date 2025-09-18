package com.commons.cache.springcache.cffenine;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "users") // 默认缓存名称
public class UserService {

    private static List<UserInfo> userInfos = Arrays.asList(
            UserInfo.builder()
                    .id(1L)
                    .name("name")
                    .build(),
            UserInfo.builder()
                    .id(2L)
                    .name("name2")
                    .build());

    // 根据ID获取用户
    @Cacheable(key = "#id", unless = "#result == null")
    public UserInfo getUserById(Long id) {
        return userInfos.stream()
                        .filter(v -> Objects.equals(id, v.getId()))
                        .findFirst()
                        .orElse(null);
    }

    // 根据用户名获取用户
    @Cacheable(value = "users", key = "#username", unless = "#result == null")
    public UserInfo getUserByUsername(String username) {
        return userInfos.stream()
                        .filter(v -> Objects.equals(username, v.getName()))
                        .findFirst()
                        .orElse(null);
    }

    // 更新用户信息
    @Caching(
            put = {
                    @CachePut(key = "#user.id"),
                    @CachePut(value = "users", key = "#user.name")
            }
    )
    public UserInfo updateUser(UserInfo user) {
        UserInfo info = userInfos.stream()
                                 .filter(v -> Objects.equals(user.getId(), v.getId()))
                                 .findFirst()
                                 .orElse(null);
        info.setName(user.getName());
        return info;
    }

    // 删除用户
    @Caching(
            evict = {
                    @CacheEvict(key = "#id"),
                    @CacheEvict(value = "users", key = "#name")
            }
    )
    public void deleteUser(Long id, String username) {
        UserInfo userInfo = UserInfo.builder()
                                    .id(id)
                                    .name(username)
                                    .build();
        //userInfos.removeIf(user -> Objects.equals(user.getId(),id));
    }

    // 获取所有用户（不缓存，因为数据量大且变化频繁）
    public List<UserInfo> getAllUsers() {
        return userInfos;
    }

    // 清除整个用户缓存
    @CacheEvict(allEntries = true)
    public void clearAllUserCache() {
        // 方法体可以为空，注解会处理缓存清除
    }
}