package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.SmsVerificationCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信验证码Mapper
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Mapper
public interface SmsVerificationCodeMapper extends BaseMapper<SmsVerificationCode> {
}
