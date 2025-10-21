package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 账户余额充值请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-21
 */
@Data
@Schema(description = "账户余额充值请求")
public class BalanceRechargeDTO {

    @NotNull(message = "充值金额不能为空")
    @Positive(message = "充值金额必须大于0")
    @Schema(description = "充值金额")
    private BigDecimal amount;

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式: wechat_native-微信扫码, wechat_h5-微信H5, alipay_page-支付宝PC, alipay_wap-支付宝WAP")
    private String paymentType;

    @Schema(description = "客户端IP")
    private String clientIp;
}
