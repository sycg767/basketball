package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知订阅实体类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@TableName("notification_subscribe")
@Schema(description = "通知订阅实体")
public class NotificationSubscribe implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "订阅ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "通知场景")
    private String scene;

    @Schema(description = "通知类型: sms/wechat/system/email")
    private String notificationType;

    @Schema(description = "是否启用: 0-否, 1-是")
    private Integer isEnabled;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
