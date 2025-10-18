package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 发送短信验证码DTO
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@Schema(description = "发送短信验证码请求")
public class SmsSendDTO {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;

    @NotBlank(message = "验证码类型不能为空")
    @Schema(description = "验证码类型: login-登录, register-注册, bind-绑定, reset-重置密码", required = true, example = "login")
    private String type;

    @Schema(description = "使用场景描述", example = "用户登录")
    private String scene;
}
