package com.basketball.common.constant;

/**
 * Redis Key常量
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
public class RedisKeyConstant {

    /**
     * 用户Token前缀
     */
    public static final String USER_TOKEN_PREFIX = "user:token:";

    /**
     * 用户信息缓存
     */
    public static final String USER_INFO = "user:info:";

    /**
     * 预订锁
     */
    public static final String BOOKING_LOCK = "booking:lock:";

    /**
     * 验证码
     */
    public static final String SMS_CODE = "sms:code:";
}