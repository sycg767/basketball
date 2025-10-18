package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信绑定请求
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@Schema(description = "微信绑定请求")
public class WechatBindDTO {

    @Schema(description = "授权码", required = true)
    @NotBlank(message = "授权码不能为空")
    private String code;

    @Schema(description = "状态码", required = true)
    @NotBlank(message = "状态码不能为空")
    private String state;
}
