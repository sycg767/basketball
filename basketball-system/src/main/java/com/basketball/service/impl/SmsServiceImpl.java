package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.common.exception.BusinessException;
import com.basketball.entity.SmsVerificationCode;
import com.basketball.mapper.SmsVerificationCodeMapper;
import com.basketball.service.ISmsService;
import com.basketball.utils.SmsUtil;
import com.basketball.utils.VerificationCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 短信服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Service
public class SmsServiceImpl implements ISmsService {

    @Resource
    private SmsVerificationCodeMapper smsCodeMapper;

    @Resource
    private SmsUtil smsUtil;

    @Resource
    private HttpServletRequest request;

    // 验证码有效期（分钟）
    private static final int CODE_EXPIRE_MINUTES = 5;

    // 发送频率限制（秒）
    private static final int SEND_INTERVAL_SECONDS = 60;

    /**
     * 发送验证码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendVerificationCode(String phone, String type) {
        // 1. 检查发送频率
        if (!checkSendFrequency(phone)) {
            throw new BusinessException("发送太频繁，请稍后再试");
        }

        // 2. 生成验证码
        String code = VerificationCodeGenerator.generateNumericCode();
        log.info("生成验证码: phone={}, code={}, type={}", phone, code, type);

        // 3. 发送短信
        boolean sent = smsUtil.sendVerificationCode(phone, code);
        if (!sent) {
            throw new BusinessException("短信发送失败，请稍后重试");
        }

        // 4. 保存验证码记录
        SmsVerificationCode smsCode = new SmsVerificationCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setType(type);
        smsCode.setExpireTime(VerificationCodeGenerator.getExpireTime(CODE_EXPIRE_MINUTES));
        smsCode.setUsed(0);
        smsCode.setSendCount(1);
        smsCode.setStatus(1);
        smsCode.setIpAddress(getClientIp());
        smsCode.setCreateTime(LocalDateTime.now());

        smsCodeMapper.insert(smsCode);

        log.info("验证码保存成功: id={}", smsCode.getId());
        return true;
    }

    /**
     * 验证验证码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verifyCode(String phone, String code, String type) {
        // 1. 查询验证码记录
        LambdaQueryWrapper<SmsVerificationCode> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmsVerificationCode::getPhone, phone)
                .eq(SmsVerificationCode::getCode, code)
                .eq(SmsVerificationCode::getType, type)
                .eq(SmsVerificationCode::getUsed, 0)
                .eq(SmsVerificationCode::getStatus, 1)
                .orderByDesc(SmsVerificationCode::getCreateTime)
                .last("LIMIT 1");

        SmsVerificationCode smsCode = smsCodeMapper.selectOne(queryWrapper);

        if (smsCode == null) {
            log.warn("验证码不存在或已使用: phone={}, code={}, type={}", phone, code, type);
            return false;
        }

        // 2. 检查是否过期
        if (VerificationCodeGenerator.isExpired(smsCode.getExpireTime())) {
            log.warn("验证码已过期: phone={}, expireTime={}", phone, smsCode.getExpireTime());
            return false;
        }

        // 3. 标记为已使用
        smsCode.setUsed(1);
        smsCode.setUsedTime(LocalDateTime.now());
        smsCode.setUpdateTime(LocalDateTime.now());
        smsCodeMapper.updateById(smsCode);

        log.info("验证码验证成功: phone={}, code={}", phone, code);
        return true;
    }

    /**
     * 清除验证码
     */
    @Override
    public void clearCode(String phone, String type) {
        LambdaQueryWrapper<SmsVerificationCode> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmsVerificationCode::getPhone, phone)
                .eq(SmsVerificationCode::getType, type)
                .eq(SmsVerificationCode::getUsed, 0);

        SmsVerificationCode smsCode = new SmsVerificationCode();
        smsCode.setUsed(1);
        smsCode.setUpdateTime(LocalDateTime.now());

        smsCodeMapper.update(smsCode, queryWrapper);
        log.info("验证码已清除: phone={}, type={}", phone, type);
    }

    /**
     * 检查发送频率限制
     */
    @Override
    public boolean checkSendFrequency(String phone) {
        // 查询最近的一条发送记录
        LambdaQueryWrapper<SmsVerificationCode> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmsVerificationCode::getPhone, phone)
                .orderByDesc(SmsVerificationCode::getCreateTime)
                .last("LIMIT 1");

        SmsVerificationCode lastCode = smsCodeMapper.selectOne(queryWrapper);

        if (lastCode == null) {
            return true;
        }

        // 检查距离上次发送是否超过限制时间
        LocalDateTime lastSendTime = lastCode.getCreateTime();
        LocalDateTime now = LocalDateTime.now();
        long secondsDiff = java.time.Duration.between(lastSendTime, now).getSeconds();

        if (secondsDiff < SEND_INTERVAL_SECONDS) {
            log.warn("发送频率过快: phone={}, lastSendTime={}, secondsDiff={}",
                    phone, lastSendTime, secondsDiff);
            return false;
        }

        return true;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp() {
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
}
