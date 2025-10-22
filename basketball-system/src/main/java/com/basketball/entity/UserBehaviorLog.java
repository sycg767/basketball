package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户行为日志实体类
 * 注意：此表不继承BaseEntity，因为数据库表中没有update_time字段
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Data
@TableName("user_behavior_log")
@Schema(description = "用户行为日志")
public class UserBehaviorLog implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "浏览器UA")
    private String userAgent;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "行为时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime behaviorTime;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
