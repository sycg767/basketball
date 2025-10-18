package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 定时提醒实体类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scheduled_reminder")
@Schema(description = "定时提醒实体")
public class ScheduledReminder extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "提醒ID")
    private Long id;

    @Schema(description = "任务类型: member_expire-会员到期, course_start-课程开始, booking_remind-预订提醒")
    private String taskType;

    @Schema(description = "目标用户ID")
    private Long targetUserId;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务ID")
    private String bizId;

    @Schema(description = "提醒时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime remindTime;

    @Schema(description = "提醒标题")
    private String title;

    @Schema(description = "提醒内容")
    private String content;

    @Schema(description = "状态: 0-待执行, 1-已执行, 2-已取消, 3-失败")
    private Integer status;

    @Schema(description = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executeTime;

    @Schema(description = "执行结果")
    private String result;

    @Schema(description = "重试次数")
    private Integer retryCount;
}
