package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付通知日志实体类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_notify_log")
@Schema(description = "支付通知日志实体")
public class PaymentNotifyLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "支付订单号")
    private String paymentNo;

    @Schema(description = "通知类型: callback-支付回调, refund-退款回调")
    private String notifyType;

    @Schema(description = "通知内容")
    private String notifyData;

    @Schema(description = "处理状态: 0-待处理, 1-处理成功, 2-处理失败")
    private Integer status;

    @Schema(description = "处理结果")
    private String result;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "重试次数")
    private Integer retryCount;
}
