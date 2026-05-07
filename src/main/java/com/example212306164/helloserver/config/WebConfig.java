package com.example212306164.helloserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 旧的 AuthInterceptor 鉴权逻辑已迁移至 Spring Security
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 旧的拦截器注册已移除，鉴权逻辑由 SecurityConfig 接管
}