package com.example212306164.helloserver.config;

import com.example212306164.helloserver.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/api/**")
                // 排除登录、注册和用户详情相关接口（用于测试 Redis 缓存）
                .excludePathPatterns(
                    "/api/users/login",
                    "/api/users",              // 注册接口 POST
                    "/api/users/{id}/detail",  // 查询用户详情
                    "/api/users/{id}"          // 删除用户
                );
    }
}