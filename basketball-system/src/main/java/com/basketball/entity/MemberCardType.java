package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 会员卡类型实体类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_card_type")
@Schema(description = "会员卡类型实体")
public class MemberCardType extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "卡类型ID")
    private Long id;

    @Schema(description = "卡名称")
    private String cardName;

    @Schema(description = "卡代码")
    private String cardCode;

    @Schema(description = "卡类型: 1-时间卡(月/季/年), 2-次卡, 3-储值卡")
    private Integer cardType;

    @Schema(description = "有效期(天)")
    private Integer duration;

    @Schema(description = "可用次数")
    private Integer times;

    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "折扣率: 0.90表示9折")
    private BigDecimal discount;

    @Schema(description = "对应会员等级")
    private Integer memberLevel;

    @Schema(description = "权益说明(JSON格式)")
    private String benefits;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态: 0-下架, 1-上架")
    private Integer status;
}
