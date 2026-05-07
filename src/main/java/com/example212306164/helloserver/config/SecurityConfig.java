package com.example212306164.helloserver.config;

import com.example212306164.helloserver.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * 接管接口保护，实现统一的安全入口管理
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
                // 添加 JWT 认证过滤器，放在用户名密码认证过滤器之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 关闭默认的表单登录页
                .formLogin(AbstractHttpConfigurer::disable)
                // 关闭 HTTP Basic 弹窗认证
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}