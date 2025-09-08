package com.commons.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(prefix = "components.security", name = "enabled", matchIfMissing = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //对 RESTAPI 需要禁用 CSRF 防护,POST请求必须
                .csrf().disable()
                //设置请求权限
                .authorizeRequests()
                .antMatchers(
                        "/api/**","/db/**","/limiter/**"
                )
                .permitAll()
                .anyRequest()
                .authenticated()                    // 其余路径需要认证
                .and()
                .formLogin()
                .loginPage("/login")                // 登录页配置
                .permitAll()
                .and()
                .logout()
                .permitAll();

        return http.build();                    // 返回 SecurityFilterChain
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                               .username("user")
                               .password("password")
                               .roles("USER")
                               .build();

        return new InMemoryUserDetailsManager(user);
    }
}