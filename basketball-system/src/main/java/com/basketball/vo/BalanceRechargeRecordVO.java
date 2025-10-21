package com.basketball.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 余额充值记录VO
 *
 * @author Basketball Team
 * @date 2025-10-21
 */
@Data
@Schema(description = "余额充值记录VO")
public class BalanceRechargeRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "支付订单号")
    private String paymentNo;

    @Schema(description = "业务订单号")
    private String orderNo;

    @Schema(description = "充值金额")
    private BigDecimal amount;

    @Schema(description = "支付方式")
    private String payMethodName;

    @Schema(description = "支付状态: 0-待支付, 1-支付中, 2-支付成功, 3-支付失败, 4-已取消")
    private Integer status;

    @Schema(description = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
