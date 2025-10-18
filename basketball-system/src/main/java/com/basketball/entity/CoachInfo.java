package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 教练信息实体类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("coach_info")
@Schema(description = "教练信息实体")
public class CoachInfo extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "教练信息ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "资格证书")
    private String certificate;

    @Schema(description = "专长")
    private String specialty;

    @Schema(description = "从教年限")
    private Integer experienceYears;

    @Schema(description = "个人简介")
    private String introduction;

    @Schema(description = "个人成就(JSON数组)")
    private String achievements;

    @Schema(description = "私教课时费")
    private BigDecimal hourlyRate;

    @Schema(description = "课程分成比例(%)")
    private BigDecimal commissionRate;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "教授学员数")
    private Integer totalStudents;

    @Schema(description = "开设课程数")
    private Integer totalCourses;

    @Schema(description = "状态: 0-离职, 1-在职, 2-休假")
    private Integer status;
}
