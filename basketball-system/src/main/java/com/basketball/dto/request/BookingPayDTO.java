package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 支付预订请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "支付预订请求")
public class BookingPayDTO {

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式：1-在线支付，2-余额，3-会员卡，4-现场支付", required = true)
    private Integer paymentMethod;

    @Schema(description = "在线支付类型：wechat_native-微信扫码, alipay_page-支付宝扫码（当paymentMethod=1时必填）")
    private String paymentType;

    @Schema(description = "会员卡ID（当paymentMethod=3时必填）")
    private Long cardId;

    @Schema(description = "使用积分数量（可选，0表示不使用积分）")
    private Integer pointsToUse;
}
