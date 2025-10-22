package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 课程支付请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-22
 */
@Data
@Schema(description = "课程支付请求")
public class CoursePaymentDTO {

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式: 1-在线支付, 2-余额支付, 3-会员卡支付")
    private Integer paymentMethod;

    @Schema(description = "支付类型: wechat-微信, alipay-支付宝")
    private String paymentType;
}
