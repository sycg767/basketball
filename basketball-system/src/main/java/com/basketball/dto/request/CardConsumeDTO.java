package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 会员卡消费请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "会员卡消费请求")
public class CardConsumeDTO {

    @NotNull(message = "会员卡ID不能为空")
    @Schema(description = "会员卡ID")
    private Long cardId;

    @Schema(description = "消费金额(储值卡)")
    @Positive(message = "消费金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "消费次数(次卡)")
    @Positive(message = "消费次数必须大于0")
    private Integer times;

    @Schema(description = "关联订单号")
    private String orderNo;

    @Schema(description = "描述")
    private String description;
}
