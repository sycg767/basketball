package com.basketball.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 场地价格VO
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Data
@Schema(description = "场地价格视图对象")
public class VenuePriceVO {

    @Schema(description = "价格ID")
    private Long id;

    @Schema(description = "场地ID")
    private Long venueId;

    @Schema(description = "时段类型: 1-工作日, 2-周末, 3-节假日")
    private Integer timeType;

    @Schema(description = "时段名称")
    private String timePeriod;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "会员价格")
    private BigDecimal memberPrice;

    @Schema(description = "状态")
    private Integer status;
}
