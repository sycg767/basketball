package com.basketball.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 短信工具类
 * 目前使用模拟发送,生产环境需要集成阿里云短信或腾讯云短信
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Component
public class SmsUtil {

    /**
     * 发送验证码短信
     * 模拟发送,实际生产环境需要调用真实短信API
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否发送成功
     */
    public boolean sendVerificationCode(String phone, String code) {
        try {
            // TODO: 生产环境需要集成真实短信服务
            // 示例: 阿里云短信
            // DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            // IAcsClient client = new DefaultAcsClient(profile);
            // SendSmsRequest request = new SendSmsRequest();
            // request.setPhoneNumbers(phone);
            // request.setSignName(signName);
            // request.setTemplateCode(templateCode);
            // request.setTemplateParam("{\"code\":\"" + code + "\"}");
            // SendSmsResponse response = client.getAcsResponse(request);
            // return "OK".equals(response.getCode());

            // 模拟发送
            log.info("【模拟短信】发送验证码到手机号: {}, 验证码: {}", phone, code);
            log.info("【短信内容】您的验证码是: {}，5分钟内有效，请勿泄露给他人。【篮球馆管理系统】", code);

            // 模拟发送成功
            return true;

        } catch (Exception e) {
            log.error("发送短信验证码失败: phone={}, error={}", phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 生成6位随机数字验证码
     *
     * @return 验证码
     */
    public static String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 生成指定位数的随机数字验证码
     *
     * @param length 验证码长度
     * @return 验证码
     */
    public static String generateCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送通知短信
     *
     * @param phone   手机号
     * @param content 短信内容
     * @return 是否发送成功
     */
    public boolean sendNotification(String phone, String content) {
        try {
            // TODO: 生产环境需要集成真实短信服务

            // 模拟发送
            log.info("【模拟短信】发送通知到手机号: {}", phone);
            log.info("【短信内容】{}", content);

            return true;

        } catch (Exception e) {
            log.error("发送通知短信失败: phone={}, error={}", phone, e.getMessage(), e);
            return false;
        }
    }
}
