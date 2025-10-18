package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 取消预订请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "取消预订请求")
public class BookingCancelDTO {

    @NotBlank(message = "取消原因不能为空")
    @Schema(description = "取消原因", required = true)
    private String cancelReason;
}
