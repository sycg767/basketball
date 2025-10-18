package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员卡使用记录实体类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@TableName("member_card_record")
@Schema(description = "会员卡使用记录实体")
public class MemberCardRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "会员卡ID")
    private Long cardId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "记录类型: 1-充值, 2-消费, 3-退款, 4-赠送")
    private Integer recordType;

    @Schema(description = "金额变动")
    private BigDecimal changeAmount;

    @Schema(description = "次数变动")
    private Integer changeTimes;

    @Schema(description = "变动前余额")
    private BigDecimal balanceBefore;

    @Schema(description = "变动后余额")
    private BigDecimal balanceAfter;

    @Schema(description = "变动前次数")
    private Integer timesBefore;

    @Schema(description = "变动后次数")
    private Integer timesAfter;

    @Schema(description = "关联订单号")
    private String relatedOrderNo;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
