package com.basketball.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员卡视图对象
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "会员卡视图对象")
public class MemberCardVO {

    @Schema(description = "会员卡ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "卡类型ID")
    private Long cardTypeId;

    @Schema(description = "卡号")
    private String cardNo;

    @Schema(description = "卡名称")
    private String cardName;

    @Schema(description = "卡类型: 1-时间卡, 2-次卡, 3-储值卡")
    private Integer cardType;

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;
}
