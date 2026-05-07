package com.example212306164.helloserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * 接管接口保护，实现统一的安全入口管理
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 开启全局 CORS 配置
                .cors(cors -> cors.configure(http))
                // 关闭 CSRF 防护（前后端分离项目）
                .csrf(AbstractHttpConfigurer::disable)
                // 配置 Session 管理策略，设置无状态
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 配置接口访问规则
                .authorizeHttpRequests(auth -> auth
                        // 放行注册接口
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // 放行登录接口
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        // 其他所有请求都必须先认证
                        .anyRequest().authenticated()
                )
                // 关闭默认的表单登录页
                .formLogin(AbstractHttpConfigurer::disable)
                // 关闭 HTTP Basic 弹窗认证
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}