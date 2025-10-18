package com.basketball.interceptor;

import com.basketball.security.JwtTokenProvider;
import com.basketball.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证拦截器
 * 从请求头中提取 JWT token，解析用户ID并存储到 ThreadLocal
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取 token
        String token = getTokenFromRequest(request);
        
        if (token != null) {
            try {
                // 验证 token 并解析用户ID
                if (jwtTokenProvider.validateToken(token)) {
                    Long userId = jwtTokenProvider.getUserIdFromToken(token);
                    // 将用户ID存储到 ThreadLocal
                    UserContext.setUserId(userId);
                    log.debug("用户 {} 通过JWT认证", userId);
                }
            } catch (Exception e) {
                log.warn("JWT token 解析失败: {}", e.getMessage());
            }
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清除 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }

    /**
     * 从请求中提取 token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 优先从 Authorization header 获取
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 兼容直接从 token header 获取
        String token = request.getHeader("token");
        if (token != null) {
            return token;
        }
        
        // 最后尝试从请求参数获取
        return request.getParameter("token");
    }
}

