package com.commons.security;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "components.security")
public class SecurityInfoProperties {
    private boolean enabled = true;
    private List<UserInfo> users;
    private List<String> permitAllUrls;
    private List<String> adminUrls;

    @Getter
    @Setter
    public static class UserInfo {
        private String username;
        private String password;
        private String roles;
    }
}