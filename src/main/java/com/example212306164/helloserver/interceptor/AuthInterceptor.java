package com.example212306164.helloserver.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String method = request.getMethod();
        String uri = request.getRequestURI();

        System.out.println("拦截器执行 - 方法: " + method + ", URI: " + uri);

        // 只放行注册接口（POST /api/users），登录接口已在 WebConfig 中排除
        // 注意：不能在 WebConfig 中用 excludePathPatterns("/api/users") 排除，
        // 因为那样会连 GET/DELETE /api/users/** 也一并跳过拦截，存在安全漏洞。
        // 这里通过 HTTP 方法+路径的细粒度判断，只放行 POST 注册请求。
        boolean isRegister = "POST".equalsIgnoreCase(method) && "/api/users".equals(uri);
        if (isRegister) {
            return true;
        }

        // 对其他所有接口（含 GET /api/users/{id}、GET /api/users/page、DELETE 等）执行 Token 校验
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            String errorJson = "{\"code\": 401, \"msg\": \"登录凭证已缺失或过期，请重新登录\", \"data\": null}";
            response.getWriter().write(errorJson);
            return false;
        }

        return true;
    }
}