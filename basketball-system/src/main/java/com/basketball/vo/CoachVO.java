package com.basketball.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教练信息视图对象
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "教练信息视图对象")
public class CoachVO {

    @Schema(description = "教练信息ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "资格证书")
    private String certificate;

    @Schema(description = "专长")
    private String specialty;

    @Schema(description = "从教年限")
    private Integer experienceYears;

    @Schema(description = "个人简介")
    private String introduction;

    @Schema(description = "个人成就")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
