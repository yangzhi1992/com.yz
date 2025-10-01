package com.commons.security;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(prefix = "components.security", name = "enabled", matchIfMissing = false)
public class SecurityConfig {

    @Autowired
    private SecurityInfoProperties securityInfoProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //对 RESTAPI 需要禁用 CSRF 防护,POST请求必须
                .csrf()
                .disable()
                //设置请求权限
                .authorizeHttpRequests()
                .antMatchers(
                        securityInfoProperties.getPermitAllUrls()
                                              .toArray(new String[0])
                )
                .permitAll()
                .antMatchers(securityInfoProperties.getAdminUrls()
                                                   .toArray(new String[0]))
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll() // 登录页配置
                .and()
                .logout()
                .permitAll() //登出配置
                .and()
                .httpBasic();

        return http.build();                    // 返回 SecurityFilterChain
    }

    /**
     * 添加用户和角色
     */
    @Bean
    public UserDetailsService userDetailsService() {
        //{noop}" + user.getPassword() {noop}后面添加明文密码
        List<UserDetails> users = securityInfoProperties.getUsers()
                                                        .stream()
                                                        .map(user -> User.builder()
                                                                         .username(user.getUsername())
                                                                         .password("{noop}" + user.getPassword()) //明文
                                                                         .password(passwordEncoder().encode(
                                                                                 user.getPassword())) //密文
                                                                         .roles(user.getRoles())
                                                                         .build())
                                                        .collect(Collectors.toList());

        return new InMemoryUserDetailsManager(users);
    }

    /**
     * Spring Security 加密
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 推荐使用 BCrypt
    }

}