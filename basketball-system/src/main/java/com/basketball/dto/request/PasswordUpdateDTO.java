package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 密码更新DTO
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@Data
@Schema(description = "密码更新请求")
public class PasswordUpdateDTO {

    @NotBlank(message = "原密码不能为空")
    @Schema(description = "原密码", required = true)
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^.{6,20}$", message = "密码长度为6-20位")
    @Schema(description = "新密码", required = true)
    private String newPassword;
}