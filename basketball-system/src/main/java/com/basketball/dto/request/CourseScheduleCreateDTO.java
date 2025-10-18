package com.basketball.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 创建课程排期请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "创建课程排期请求")
public class CourseScheduleCreateDTO {

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long courseId;

    @NotNull(message = "场地ID不能为空")
    @Schema(description = "场地ID")
    private Long venueId;

    @NotNull(message = "上课日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "上课日期")
    private LocalDate scheduleDate;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "开始时间")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "结束时间")
    private LocalTime endTime;

    @NotNull(message = "最大人数不能为空")
    @Min(value = 1, message = "最大人数至少为1")
    @Schema(description = "最大人数")
    private Integer maxStudents;

    @Schema(description = "备注")
    private String remark;
}
