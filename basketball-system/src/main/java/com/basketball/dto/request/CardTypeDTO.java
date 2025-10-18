package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

/**
 * 会员卡类型请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "会员卡类型请求")
public class CardTypeDTO {

    @NotBlank(message = "卡名称不能为空")
    @Schema(description = "卡名称")
    private String cardName;

    @NotBlank(message = "卡编号不能为空")
    @Schema(description = "卡编号")
    private String cardCode;

    @NotNull(message = "卡类型不能为空")
    @Schema(description = "卡类型: 1-时间卡, 2-次卡, 3-储值卡")
    private Integer cardType;

    @Schema(description = "有效时长(天)")
    private Integer duration;

    @Schema(description = "次数")
    private Integer times;

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "折扣")
    private BigDecimal discount;

    @NotNull(message = "会员等级不能为空")
    @Schema(description = "会员等级")
    private Integer memberLevel;

    @Schema(description = "权益列表")
    private List<String> benefits;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态: 0-下架, 1-上架")
    private Integer status;
}
