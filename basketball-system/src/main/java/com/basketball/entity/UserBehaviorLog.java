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
 * 用户行为日志实体类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_behavior_log")
@Schema(description = "用户行为日志")
public class UserBehaviorLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "行为类型: login-登录, view-浏览, booking-预订, payment-支付, cancel-取消")
    private String behaviorType;

    @Schema(description = "业务模块: user-用户, venue-场地, course-课程, booking-预订, payment-支付")
    private String module;

    @Schema(description = "关联业务ID")
    private String businessId;

    @Schema(description = "行为描述")
    private String description;

    @Schema(description = "设备类型: web, ios, android, mini-program")
    private String deviceType;

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "浏览器UA")
    private String userAgent;

    @Schema(description = "扩展参数(JSON)")
    private String extraParams;

    @Schema(description = "行为时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime behaviorTime;
}
