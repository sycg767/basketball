package com.basketball.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 创建预订请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "创建预订请求")
public class BookingCreateDTO {

    @NotNull(message = "场地ID不能为空")
    @Schema(description = "场地ID", required = true)
    private Long venueId;

    @NotNull(message = "预订日期不能为空")
    @Schema(description = "预订日期", required = true, example = "2025-01-15")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", required = true, example = "18:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", required = true, example = "20:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "预订类型：1-按时段，2-包场", example = "1")
    private Integer bookingType;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "人数")
    private Integer peopleCount;

    @Schema(description = "备注")
    private String remark;
}
