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
 * 会员活跃度分析实体类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_activity_analysis")
@Schema(description = "会员活跃度分析")
public class MemberActivityAnalysis extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "分析ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "统计日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate analysisDate;

    @Schema(description = "登录次数")
    private Integer loginCount;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "预订次数")
    private Integer bookingCount;

    @Schema(description = "支付次数")
    private Integer paymentCount;

    @Schema(description = "消费金额")
    private BigDecimal paymentAmount;

    @Schema(description = "取消次数")
    private Integer cancelCount;

    @Schema(description = "活跃度得分 (0-100)")
    private Integer activityScore;

    @Schema(description = "最近活跃时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActiveTime;

    @Schema(description = "连续活跃天数")
    private Integer continuousDays;

    @Schema(description = "R值 (Recency 最近购买): 最后消费距今天数")
    private Integer rfmR;

    @Schema(description = "F值 (Frequency 消费频次): 统计期内消费次数")
    private Integer rfmF;

    @Schema(description = "M值 (Monetary 消费金额): 统计期内消费总额")
    private BigDecimal rfmM;

    @Schema(description = "RFM综合得分")
    private Integer rfmScore;

    @Schema(description = "流失风险等级: 0-无风险, 1-低风险, 2-中风险, 3-高风险")
    private Integer churnRisk;

    @Schema(description = "备注")
    private String remark;
}
