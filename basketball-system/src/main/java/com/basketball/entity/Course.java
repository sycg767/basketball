package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课程实体类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
@Schema(description = "课程实体")
public class Course extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "课程ID")
    private Long id;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程编号")
    private String courseCode;

    @Schema(description = "课程类型: 1-基础班, 2-提高班, 3-专业班, 4-私教课")
    private Integer courseType;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "最大学员数")
    private Integer maxStudents;

    @Schema(description = "最小开班人数")
    private Integer minStudents;

    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "会员价格")
    private BigDecimal memberPrice;

    @Schema(description = "课时(分钟)")
    private Integer duration;

    @Schema(description = "总课时数")
    private Integer totalSessions;

    @Schema(description = "难度等级: 1-入门, 2-初级, 3-中级, 4-高级")
    private Integer difficultyLevel;

    @Schema(description = "适合年龄")
    private String ageRange;

    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "课程大纲(JSON格式)")
    private String syllabus;

    @Schema(description = "封面图片")
    private String coverImage;

    @Schema(description = "宣传视频")
    private String videoUrl;

    @Schema(description = "状态: 0-下架, 1-上架, 2-已满")
    private Integer status;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "报名人数")
    private Integer enrollCount;

    @Schema(description = "评分")
    private BigDecimal rating;
}
