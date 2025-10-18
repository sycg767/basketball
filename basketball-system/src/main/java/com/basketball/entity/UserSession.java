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
 * 用户Session管理实体类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_session")
@Schema(description = "用户Session实体")
public class UserSession extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "Session ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "登录类型：1-账号密码，2-手机验证码，3-微信，4-其他")
    private Integer loginType;

    @Schema(description = "设备类型：web/ios/android")
    private String deviceType;

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "登录地点")
    private String location;

    @Schema(description = "User-Agent")
    private String userAgent;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "最后活跃时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActiveTime;

    @Schema(description = "状态：0-已失效，1-有效")
    private Integer status;
}
