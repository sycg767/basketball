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
 * 课程热度分析实体类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_popularity_analysis")
@Schema(description = "课程热度分析")
public class CoursePopularityAnalysis extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "分析ID")
    private Long id;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "分析日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate analysisDate;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "浏览人数(去重)")
    private Integer viewUserCount;

    @Schema(description = "报名次数")
    private Integer enrollCount;

    @Schema(description = "报名人数(去重)")
    private Integer enrollUserCount;

    @Schema(description = "完成人数")
    private Integer completionCount;

    @Schema(description = "平均评分(0-5分)")
    private BigDecimal avgRating;

    @Schema(description = "评价数量")
    private Integer ratingCount;

    @Schema(description = "收入金额")
    private BigDecimal revenue;

    @Schema(description = "热度评分(0-100)")
    private Integer popularityScore;

    @Schema(description = "排名")
    private Integer ranking;

    @Schema(description = "趋势:up-上升, down-下降, stable-稳定")
    private String trend;

    @Schema(description = "趋势变化值")
    private Integer trendChange;

    @Schema(description = "转化率(%)")
    private BigDecimal conversionRate;

    @Schema(description = "完成率(%)")
    private BigDecimal completionRate;
}
