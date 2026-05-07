package com.example212306164.helloserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * 由于本项目使用自定义 AuthInterceptor + JWT 实现认证，
 * 这里将 Spring Security 的默认拦截全部放行，避免与拦截器冲突。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（前后端分离项目通常不需要）
                .csrf(AbstractHttpConfigurer::disable)
                // 放行所有请求，认证逻辑由自定义 AuthInterceptor 处理
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // 禁用默认的 HTTP Basic 弹窗认证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 禁用默认的表单登录页
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
