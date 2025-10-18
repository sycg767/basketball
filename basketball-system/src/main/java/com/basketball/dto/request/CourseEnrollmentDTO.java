package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 课程报名请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "课程报名请求")
public class CourseEnrollmentDTO {

    @NotNull(message = "排期ID不能为空")
    @Schema(description = "排期ID")
    private Long scheduleId;

    @Schema(description = "订单编号(支付时使用)")
    private String orderNo;
}
