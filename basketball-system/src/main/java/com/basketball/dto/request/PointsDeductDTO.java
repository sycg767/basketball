package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 积分抵扣请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-22
 */
@Data
@Schema(description = "积分抵扣请求")
public class PointsDeductDTO {

    @NotNull(message = "使用积分不能为空")
    @Min(value = 100, message = "最少使用100积分")
    @Schema(description = "使用积分数量", example = "1000")
    private Integer pointsToUse;

    @NotNull(message = "订单金额不能为空")
    @Schema(description = "订单原始金额", example = "100.00")
    private java.math.BigDecimal orderAmount;

    @Schema(description = "订单号", example = "BK1234567890")
    private String orderNo;
}
