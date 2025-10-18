package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_order")
@Schema(description = "支付订单实体")
public class PaymentOrder extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "支付订单ID")
    private Long id;

    @Schema(description = "支付订单号(唯一)")
    private String paymentNo;

    @Schema(description = "业务订单号(预订订单号或会员卡订单号)")
    private String businessNo;

    @Schema(description = "业务类型: booking-预订, member_card-会员卡, course-课程")
    private String businessType;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "支付方式: wechat_native, alipay_pc等")
    private String paymentType;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "实际支付金额")
    private BigDecimal actualAmount;

    @Schema(description = "支付状态: 0-待支付, 1-支付中, 2-支付成功, 3-支付失败, 4-已取消, 5-已退款")
    private Integer status;

    @Schema(description = "第三方支付订单号")
    private String thirdPartyOrderNo;

    @Schema(description = "第三方支付流水号")
    private String thirdPartyTradeNo;

    @Schema(description = "支付二维码URL")
    private String qrCodeUrl;

    @Schema(description = "支付跳转URL")
    private String paymentUrl;

    @Schema(description = "用户IP地址")
    private String clientIp;

    @Schema(description = "支付完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paidTime;

    @Schema(description = "订单过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "退款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    @Schema(description = "退款原因")
    private String refundReason;

    @Schema(description = "支付失败原因")
    private String errorMsg;

    @Schema(description = "备注")
    private String remark;
}
