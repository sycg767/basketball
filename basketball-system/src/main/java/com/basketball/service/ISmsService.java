package com.basketball.service;

/**
 * 短信服务接口
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
public interface ISmsService {

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phone, String type);

    /**
     * 验证验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @param type  验证码类型
     * @return 是否验证通过
     */
    boolean verifyCode(String phone, String code, String type);

    /**
     * 清除验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     */
    void clearCode(String phone, String type);

    /**
     * 检查发送频率限制
     *
     * @param phone 手机号
     * @return 是否允许发送
     */
    boolean checkSendFrequency(String phone);
}
