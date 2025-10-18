package com.basketball.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信授权URL响应
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@Schema(description = "微信授权URL")
public class WechatAuthUrlVO {

    @Schema(description = "授权URL")
    private String authUrl;

    @Schema(description = "状态码(用于防CSRF)")
    private String state;
}
