package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 创建支付订单请求
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@Schema(description = "创建支付订单请求")
public class PaymentCreateDTO {

    @Schema(description = "业务订单号", required = true)
    @NotBlank(message = "业务订单号不能为空")
    private String businessNo;

    @Schema(description = "业务类型: booking-预订, member_card-会员卡, course-课程", required = true)
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    @Schema(description = "支付方式: wechat_native-微信扫码, wechat_h5-微信H5, alipay_pc-支付宝PC, alipay_wap-支付宝WAP", required = true)
    @NotBlank(message = "支付方式不能为空")
    private String paymentType;

    @Schema(description = "支付金额", required = true)
    @NotNull(message = "支付金额不能为空")
    @Positive(message = "支付金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "订单描述")
    private String description;

    @Schema(description = "客户端IP")
    private String clientIp;
}
