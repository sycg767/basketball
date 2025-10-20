package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

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
    private String orderNo;

    @Schema(description = "支付渠道")
    private String payChannel;

    @Schema(description = "通知类型: callback-支付回调, refund-退款回调")
    private String notifyType;

    @Schema(description = "通知内容")
    private String notifyData;

    @Schema(description = "验签状态: 0-失败, 1-成功")
    private Integer verifyStatus;

    @Schema(description = "处理状态: 0-待处理, 1-处理成功, 2-处理失败")
    private Integer processStatus;

    @Schema(description = "处理结果")
    private String processResult;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "重试次数")
    private Integer retryCount;

  @Schema(description = "通知来源IP")
    private String ipAddress;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
