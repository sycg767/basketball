package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 课程报名请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "课程报名请求")
public class CourseEnrollmentDTO {

    @NotNull(message = "排期ID不能为空")
    @Schema(description = "排期ID")
    private Long scheduleId;

    @Schema(description = "订单编号(支付时使用)")
    private String orderNo;

    @Schema(description = "是否使用积分")
    private Boolean usePoints;

    @Schema(description = "使用的积分数量")
    private Integer pointsToUse;

    @Schema(description = "支付方式: 1-在线支付, 2-余额支付, 3-会员卡支付")
    private Integer paymentMethod;

    @Schema(description = "支付类型: wechat-微信, alipay-支付宝")
    private String paymentType;
}
