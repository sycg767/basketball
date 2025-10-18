package com.basketball.common.exception;

import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@Getter
public enum ErrorCode {

    // 系统错误
    SYSTEM_ERROR(500, "系统错误"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),

    // 认证授权错误
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "没有权限访问"),
    TOKEN_INVALID(401, "Token无效或已过期"),

    // 用户相关错误
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(1003, "邮箱已被注册"),
    PHONE_ALREADY_EXISTS(1004, "手机号已被注册"),
    PASSWORD_ERROR(1005, "密码错误"),
    USER_DISABLED(1006, "用户已被禁用"),
    OLD_PASSWORD_ERROR(1007, "原密码错误"),
    VERIFICATION_CODE_ERROR(1008, "验证码错误或已过期"),

    // 场地相关错误
    VENUE_NOT_FOUND(2001, "场地不存在"),
    VENUE_NOT_AVAILABLE(2002, "场地不可用"),
    VENUE_HAS_BOOKING(2003, "场地有未完成的预订，无法删除"),

    // 预订相关错误
    BOOKING_NOT_FOUND(3001, "预订不存在"),
    BOOKING_TIME_CONFLICT(3002, "预订时间冲突"),
    BOOKING_CANNOT_CANCEL(3003, "预订无法取消"),
    BOOKING_EXPIRED(3004, "预订已过期"),

    // 支付相关错误
    PAYMENT_NOT_FOUND(4001, "支付记录不存在"),
    PAYMENT_FAILED(4002, "支付失败"),

    // 课程相关错误
    COURSE_NOT_FOUND(5001, "课程不存在"),
    COURSE_FULL(5002, "课程已满员"),
    ALREADY_ENROLLED(5003, "已报名该课程"),

    // 会员相关错误
    CARD_NOT_FOUND(6001, "会员卡不存在"),
    CARD_EXPIRED(6002, "会员卡已过期"),
    INSUFFICIENT_BALANCE(6003, "余额不足"),
    INSUFFICIENT_POINTS(6004, "积分不足");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}