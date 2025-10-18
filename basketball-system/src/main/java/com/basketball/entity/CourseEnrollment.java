package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程报名实体类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_enrollment")
@Schema(description = "课程报名实体")
public class CourseEnrollment extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "报名ID")
    private Long id;

    @Schema(description = "报名编号")
    private String enrollmentNo;

    @Schema(description = "排期ID")
    private Long scheduleId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "学员ID")
    private Long userId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "报名价格")
    private BigDecimal price;

    @Schema(description = "支付状态: 0-待支付, 1-已支付, 2-已退款")
    private Integer payStatus;

    @Schema(description = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @Schema(description = "出勤状态: 0-未签到, 1-已签到, 2-缺席, 3-请假")
    private Integer attendanceStatus;

    @Schema(description = "签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;

    @Schema(description = "评分: 1-5星")
    private Integer rating;

    @Schema(description = "评价内容")
    private String comment;

    @Schema(description = "评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentTime;

    @Schema(description = "状态: 0-已取消, 1-正常")
    private Integer status;

    @Schema(description = "报名时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime enrollTime;
}
