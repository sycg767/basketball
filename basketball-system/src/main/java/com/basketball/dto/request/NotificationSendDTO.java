package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 发送通知请求
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@Schema(description = "发送通知请求")
public class NotificationSendDTO {

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "模板编码", required = true)
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "通知类型: sms-短信, email-邮件, system-站内信, wechat-微信")
    private String notificationType;

    @Schema(description = "业务ID")
    private String bizId;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "模板参数(用于替换模板中的占位符)")
    private Map<String, Object> params;

    @Schema(description = "自定义标题(如果不使用模板)")
    private String customTitle;

    @Schema(description = "自定义内容(如果不使用模板)")
    private String customContent;
}
