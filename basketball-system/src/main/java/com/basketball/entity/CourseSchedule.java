package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 课程排期实体类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_schedule")
@Schema(description = "课程排期实体")
public class CourseSchedule extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "排期ID")
    private Long id;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "场地ID")
    private Long venueId;

    @Schema(description = "排期编号")
    private String scheduleNo;

    @Schema(description = "上课日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @Schema(description = "最大人数")
    private Integer maxStudents;

    @Schema(description = "已报名人数")
    private Integer enrolledCount;

    @Schema(description = "已签到人数")
    private Integer checkedInCount;

    @Schema(description = "状态: 0-未开始, 1-报名中, 2-已满员, 3-进行中, 4-已结束, 5-已取消")
    private Integer status;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "备注")
    private String remark;
}
