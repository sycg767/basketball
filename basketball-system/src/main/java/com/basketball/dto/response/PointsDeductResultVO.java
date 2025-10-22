package com.basketball.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 积分抵扣结果VO
 *
 * @author Basketball Team
 * @date 2025-10-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分抵扣结果")
public class PointsDeductResultVO {

    @Schema(description = "原始金额", example = "100.00")
    private BigDecimal originalAmount;

    @Schema(description = "使用积分", example = "1000")
    private Integer pointsUsed;

    @Schema(description = "抵扣金额", example = "10.00")
    private BigDecimal deductAmount;

    @Schema(description = "实付金额", example = "90.00")
    private BigDecimal finalAmount;

    @Schema(description = "剩余积分", example = "5000")
    private Integer remainingPoints;

    @Schema(description = "是否可用", example = "true")
    private Boolean available;

    @Schema(description = "提示信息", example = "成功使用1000积分抵扣10元")
    private String message;
}
