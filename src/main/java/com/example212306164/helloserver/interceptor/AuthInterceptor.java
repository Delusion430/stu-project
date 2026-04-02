package com.example212306164.helloserver.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 获取本次请求的HTTP动词和具体路径
        String method = request.getMethod();
        String uri = request.getRequestURI();

        System.out.println("拦截器执行 - 方法: " + method + ", URI: " + uri);  // 添加这行

        // 2. 手写细粒度放行规则
        boolean isCreateUser = "POST".equalsIgnoreCase(method) && "/api/users".equals(uri);
        boolean isGetUser = "GET".equalsIgnoreCase(method) && uri.startsWith("/api/users/");

        if (isCreateUser || isGetUser) {
            return true;
        }

        // 3. 执行严格的Token校验
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            // ✅ 修正：添加 data: null 字段
            String errorJson = "{\"code\": 401, \"msg\": \"登录凭证已缺失或过期，请重新登录\", \"data\": null}";
            response.getWriter().write(errorJson);
            return false;
        }

        return true;

    }
}