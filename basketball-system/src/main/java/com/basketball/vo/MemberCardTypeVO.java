package com.basketball.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员卡类型视图对象
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "会员卡类型视图对象")
public class MemberCardTypeVO {

    @Schema(description = "卡类型ID")
    private Long id;

    @Schema(description = "卡名称")
    private String cardName;

    @Schema(description = "卡代码")
    private String cardCode;

    @Schema(description = "卡类型: 1-时间卡, 2-次卡, 3-储值卡")
    private Integer cardType;

    @Schema(description = "有效期(天)")
    private Integer duration;

    @Schema(description = "可用次数")
    private Integer times;

    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "折扣率")
    private BigDecimal discount;

    @Schema(description = "对应会员等级")
    private Integer memberLevel;

    @Schema(description = "权益说明列表")
    private List<String> benefitsList;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态: 0-下架, 1-上架")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
