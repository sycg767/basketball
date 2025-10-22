package com.basketball.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知响应
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@Schema(description = "通知信息")
public class NotificationVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "通知类型")
    private String notificationType;

    @Schema(description = "通知渠道")
    private String channel;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "业务关联ID")
    private String businessId;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "是否已读: 0-未读, 1-已读")
    private Integer isRead;

    @Schema(description = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    @Schema(description = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    @Schema(description = "跳转链接")
    private String jumpUrl;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
