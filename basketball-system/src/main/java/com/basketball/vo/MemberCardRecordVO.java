package com.basketball.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员卡使用记录视图对象
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "会员卡使用记录视图对象")
public class MemberCardRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "会员卡ID")
    private Long cardId;

    @Schema(description = "卡号")
    private String cardNo;

    @Schema(description = "卡名称")
    private String cardName;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
