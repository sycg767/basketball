package com.basketball.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 课程排期VO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "课程排期视图对象")
public class CourseScheduleVO {

    @Schema(description = "排期ID")
    private Long id;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "场地ID")
    private Long venueId;

    @Schema(description = "场地名称")
    private String venueName;

    @Schema(description = "场地位置")
    private String venueLocation;

    @Schema(description = "排期编号")
    private String scheduleNo;

    @Schema(description = "上课日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "时间段")
    private String timeSlot;

    @Schema(description = "最大人数")
    private Integer maxStudents;

    @Schema(description = "已报名人数")
    private Integer enrolledCount;

    @Schema(description = "剩余名额")
    private Integer remainingSeats;

    @Schema(description = "已签到人数")
    private Integer checkedInCount;

    @Schema(description = "状态: 0-未开始, 1-报名中, 2-已满员, 3-进行中, 4-已结束, 5-已取消")
    private Integer status;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
