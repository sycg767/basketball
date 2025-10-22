package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 课程价格计算DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "课程价格计算DTO")
public class CoursePriceDTO {

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long courseId;

    @NotNull(message = "排期ID不能为空")
    @Schema(description = "排期ID")
    private Long scheduleId;
}
