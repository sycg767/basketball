package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员卡实体类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_card")
@Schema(description = "会员卡实体")
public class MemberCard extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "会员卡ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "卡类型ID")
    private Long cardTypeId;

    @Schema(description = "卡号")
    private String cardNo;

    @Schema(description = "余额(储值卡)")
    private BigDecimal balance;

    @Schema(description = "剩余次数(次卡)")
    private Integer remainingTimes;

    @Schema(description = "生效日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "到期日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @Schema(description = "状态: 0-未激活, 1-有效, 2-已过期, 3-已冻结, 4-已注销")
    private Integer status;

    @Schema(description = "激活时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activateTime;

    @Schema(description = "冻结原因")
    private String freezeReason;

    @Schema(description = "备注")
    private String remark;
}
