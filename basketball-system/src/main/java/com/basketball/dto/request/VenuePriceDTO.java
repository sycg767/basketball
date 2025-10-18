package com.basketball.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 场地价格DTO
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Data
@Schema(description = "场地价格请求")
public class VenuePriceDTO {

    @NotNull(message = "场地ID不能为空")
    @Schema(description = "场地ID", required = true)
    private Long venueId;

    @NotBlank(message = "时段类型不能为空")
    @Schema(description = "时段类型", required = true)
    private String timeType;

    @NotBlank(message = "时段名称不能为空")
    @Schema(description = "时段名称", required = true)
    private String timePeriod;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", required = true)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", required = true)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "价格不能为空")
    @Schema(description = "价格", required = true)
    private BigDecimal price;

    @NotNull(message = "会员价格不能为空")
    @Schema(description = "会员价格", required = true)
    private BigDecimal memberPrice;
}
