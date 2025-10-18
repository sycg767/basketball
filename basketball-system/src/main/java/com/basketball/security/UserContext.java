package com.basketball.security;

/**
 * 用户上下文
 * 使用ThreadLocal存储当前登录用户信息
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
public class UserContext {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setCurrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    /**
     * 清除当前用户ID
     */
    public static void clear() {
        currentUserId.remove();
    }
}
