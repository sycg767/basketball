package com.basketball.interceptor;

import com.basketball.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 短信验证码限流拦截器
 * 基于内存的简单限流实现 (生产环境建议使用Redis)
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Component
public class SmsRateLimitInterceptor implements HandlerInterceptor {

    /**
     * IP限流: IP -> 计数器
     * Key: IP地址, Value: 1小时内的请求次数
     */
    private final Map<String, RateLimitCounter> ipLimitMap = new ConcurrentHashMap<>();

    /**
     * 限流配置
     */
    private static final int IP_HOUR_LIMIT = 10; // IP每小时限制10次
    private static final long HOUR_MILLIS = 60 * 60 * 1000; // 1小时

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 只拦截发送短信接口
        String requestURI = request.getRequestURI();
        if (!requestURI.contains("/api/sms/send")) {
            return true;
        }

        // 获取客户端IP
        String clientIp = getClientIp(request);

        // 检查IP限流
        if (!checkIpRateLimit(clientIp)) {
            log.warn("IP限流触发: IP={}, 超过每小时{}次限制", clientIp, IP_HOUR_LIMIT);
            throw new BusinessException("发送太频繁，请稍后再试");
        }

        // 记录本次请求
        recordRequest(clientIp);

        return true;
    }

    /**
     * 检查IP限流
     */
    private boolean checkIpRateLimit(String ip) {
        RateLimitCounter counter = ipLimitMap.get(ip);

        // 首次请求
        if (counter == null) {
            return true;
        }

        // 检查是否过期
        long now = System.currentTimeMillis();
        if (now - counter.startTime > HOUR_MILLIS) {
            // 过期,清除旧数据
            ipLimitMap.remove(ip);
            return true;
        }

        // 检查是否超过限制
        return counter.count.get() < IP_HOUR_LIMIT;
    }

    /**
     * 记录请求
     */
    private void recordRequest(String ip) {
        long now = System.currentTimeMillis();

        ipLimitMap.compute(ip, (key, counter) -> {
            if (counter == null) {
                // 首次请求
                return new RateLimitCounter(now);
            } else if (now - counter.startTime > HOUR_MILLIS) {
                // 过期,重新计数
                return new RateLimitCounter(now);
            } else {
                // 累加计数
                counter.count.incrementAndGet();
                return counter;
            }
        });
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 限流计数器
     */
    private static class RateLimitCounter {
        final long startTime;
        final AtomicInteger count;

        RateLimitCounter(long startTime) {
            this.startTime = startTime;
            this.count = new AtomicInteger(1);
        }
    }
}
