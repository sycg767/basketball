package com.basketball.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付结果响应
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@Schema(description = "支付结果")
public class PaymentResultVO {

    @Schema(description = "支付订单号")
    private String paymentNo;

    @Schema(description = "业务订单号")
    private String businessNo;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "支付方式")
    private String paymentType;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "实际支付金额")
    private BigDecimal actualAmount;

    @Schema(description = "支付状态: 0-待支付, 1-支付中, 2-支付成功, 3-支付失败, 4-已取消, 5-已退款")
    private Integer status;

    @Schema(description = "第三方订单号 (微信prepay_id/支付宝trade_no)")
    private String thirdPartyOrderNo;

    @Schema(description = "第三方交易流水号")
    private String thirdPartyTradeNo;

    @Schema(description = "支付二维码URL (微信扫码支付)")
    private String qrCodeUrl;

    @Schema(description = "支付跳转URL (H5/PC支付)")
    private String paymentUrl;

    @Schema(description = "订单过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "支付完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paidTime;
}
