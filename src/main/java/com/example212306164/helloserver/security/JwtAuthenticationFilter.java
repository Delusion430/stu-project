package com.example212306164.helloserver.security;

import com.example212306164.helloserver.entity.User;
import com.example212306164.helloserver.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * JWT 认证过滤器
 * 处理登录成功后后续请求的身份识别
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    // 放行路径列表（与 SecurityConfig 保持一致）
    private static final List<String> PERMIT_ALL_PATHS = Arrays.asList(
            "/api/users/login",
            "/api/users"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        // 检查是否为放行路径
        boolean isPermitAll = isPermitAllPath(requestUri, requestMethod);

        // 1. 读取请求头中的 Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. 如果没有 Authorization，或者不是 Bearer 开头，直接放行给后续过滤器
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 对于放行路径，即使有Bearer token也直接放行（避免旧token影响登录）
        if (isPermitAll) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 截取真正的 JWT 字符串（去掉 "Bearer " 前缀）
        String jwt = authHeader.substring(7);

        String username = null;

        try {
            // 4. 从 JWT 中解析用户名
            System.out.println("[DEBUG] 尝试解析 JWT: " + jwt.substring(0, Math.min(50, jwt.length())) + "...");
            username = jwtUtil.extractUsername(jwt);
            System.out.println("[DEBUG] 解析成功，用户名: " + username);
        } catch (Exception e) {
            // Token 解析失败，返回 401 未授权
            System.out.println("[ERROR] JWT 解析失败: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            String errorJson = "{\"code\": 401, \"msg\": \"无效的 Token，请重新登录: " + e.getMessage() + "\", \"data\": null}";
            response.getWriter().write(errorJson);
            return;
        }

        // 5. 如果解析到了用户名，并且当前还没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 根据用户名查询用户
            User user = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username));

            if (user != null) {
                // 创建认证对象
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证信息放入 SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                // 用户不存在，返回 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                String errorJson = "{\"code\": 401, \"msg\": \"用户不存在，请重新登录\", \"data\": null}";
                response.getWriter().write(errorJson);
                return;
            }
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否为放行路径
     */
    private boolean isPermitAllPath(String requestUri, String requestMethod) {
        for (String path : PERMIT_ALL_PATHS) {
            // 登录接口：POST /api/users/login
            if ("/api/users/login".equals(path) && "POST".equalsIgnoreCase(requestMethod) && "/api/users/login".equals(requestUri)) {
                return true;
            }
            // 注册接口：POST /api/users
            if ("/api/users".equals(path) && "POST".equalsIgnoreCase(requestMethod) && "/api/users".equals(requestUri)) {
                return true;
            }
        }
        return false;
    }
}