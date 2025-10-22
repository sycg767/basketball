package com.basketball.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 课程价格VO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程价格VO")
public class CoursePriceVO {

    @Schema(description = "课程原价")
    private BigDecimal basePrice;

    @Schema(description = "会员价(如果有会员卡)")
    private BigDecimal memberPrice;

    @Schema(description = "实际应付价格")
    private BigDecimal actualPrice;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "是否有会员卡")
    private Boolean hasCard;

    @Schema(description = "会员卡名称")
    private String cardName;

    @Schema(description = "折扣率(0.9表示9折)")
    private BigDecimal discount;

    @Schema(description = "是否可用积分")
    private Boolean canUsePoints;

    @Schema(description = "最大积分抵扣金额")
    private BigDecimal maxPointsDeduct;
}
