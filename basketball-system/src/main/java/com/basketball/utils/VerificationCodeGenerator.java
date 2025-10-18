package com.basketball.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * 验证码生成器
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
public class VerificationCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String NUMERIC_CHARS = "0123456789";
    private static final String ALPHA_NUMERIC_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 生成6位数字验证码
     *
     * @return 验证码
     */
    public static String generateNumericCode() {
        return generateNumericCode(6);
    }

    /**
     * 生成指定位数的数字验证码
     *
     * @param length 验证码长度
     * @return 验证码
     */
    public static String generateNumericCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(NUMERIC_CHARS.charAt(RANDOM.nextInt(NUMERIC_CHARS.length())));
        }
        return code.toString();
    }

    /**
     * 生成字母数字混合验证码
     *
     * @param length 验证码长度
     * @return 验证码
     */
    public static String generateAlphaNumericCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(ALPHA_NUMERIC_CHARS.charAt(RANDOM.nextInt(ALPHA_NUMERIC_CHARS.length())));
        }
        return code.toString();
    }

    /**
     * 获取验证码过期时间（默认5分钟后）
     *
     * @return 过期时间
     */
    public static LocalDateTime getExpireTime() {
        return getExpireTime(5);
    }

    /**
     * 获取验证码过期时间
     *
     * @param minutes 有效分钟数
     * @return 过期时间
     */
    public static LocalDateTime getExpireTime(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * 检查验证码是否过期
     *
     * @param expireTime 过期时间
     * @return true-已过期，false-未过期
     */
    public static boolean isExpired(LocalDateTime expireTime) {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
