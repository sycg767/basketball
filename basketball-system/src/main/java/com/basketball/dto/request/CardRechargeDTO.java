package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 会员卡充值请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "会员卡充值请求")
public class CardRechargeDTO {

    @NotNull(message = "会员卡ID不能为空")
    @Schema(description = "会员卡ID")
    private Long cardId;

    @NotNull(message = "充值金额不能为空")
    @Positive(message = "充值金额必须大于0")
    @Schema(description = "充值金额")
    private BigDecimal amount;

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式: 1-在线支付, 2-余额支付")
    private Integer payMethod;
}
