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

/**
 * 场地使用分析实体类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("venue_usage_analysis")
@Schema(description = "场地使用分析")
public class VenueUsageAnalysis extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "分析ID")
    private Long id;

    @Schema(description = "场地ID")
    private Long venueId;

    @Schema(description = "分析日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate analysisDate;

    @Schema(description = "总预订次数")
    private Integer totalBookings;

    @Schema(description = "成功预订次数")
    private Integer successBookings;

    @Schema(description = "取消预订次数")
    private Integer cancelledBookings;

    @Schema(description = "预订人数(去重)")
    private Integer uniqueUsers;

    @Schema(description = "预订转化率(%)")
    private BigDecimal bookingConversionRate;

    @Schema(description = "可用时段数")
    private Integer availableSlots;

    @Schema(description = "已预订时段数")
    private Integer bookedSlots;

    @Schema(description = "使用率(%)")
    private BigDecimal usageRate;

    @Schema(description = "收入金额")
    private BigDecimal revenue;

    @Schema(description = "平均客单价")
    private BigDecimal avgOrderAmount;

    @Schema(description = "高峰时段: morning-上午, afternoon-下午, evening-晚上")
    private String peakPeriod;

    @Schema(description = "高峰时段预订数")
    private Integer peakBookings;

    @Schema(description = "场地评分(0-5分)")
    private BigDecimal venueRating;

    @Schema(description = "评分数量")
    private Integer ratingCount;

    @Schema(description = "使用得分(0-100)")
    private Integer usageScore;

    @Schema(description = "趋势:up-上升, down-下降, stable-稳定")
    private String trend;

    @Schema(description = "趋势变化值")
    private Integer trendChange;

    @Schema(description = "运营建议")
    private String suggestion;
}
