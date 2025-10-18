package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 短信验证码实体类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sms_verification_code")
@Schema(description = "短信验证码实体")
public class SmsVerificationCode extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "验证码ID")
    private Long id;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "验证码")
    private String code;

    @Schema(description = "类型：login-登录，register-注册，bind-绑定，reset-重置密码")
    private String type;

    @Schema(description = "使用场景描述")
    private String scene;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "是否已使用：0-未使用，1-已使用")
    private Integer used;

    @Schema(description = "使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime usedTime;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "发送次数")
    private Integer sendCount;

    @Schema(description = "状态：0-失败，1-成功")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMsg;
}
