package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 购买会员卡请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "购买会员卡请求")
public class CardPurchaseDTO {

    @NotNull(message = "卡类型ID不能为空")
    @Schema(description = "卡类型ID")
    private Long cardTypeId;

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式: 1-在线支付, 2-余额支付")
    private Integer payMethod;

    @Schema(description = "实际支付金额")
    private BigDecimal actualAmount;
}
