package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 场地价格实体类
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("venue_price")
@Schema(description = "场地价格实体")
public class VenuePrice extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "价格ID")
    private Long id;

    @Schema(description = "场地ID")
    private Long venueId;

    @Schema(description = "时间类型: 1-工作日, 2-周末, 3-节假日")
    private Integer timeType;

    @Schema(description = "时段名称")
    private String timePeriod;

    @Schema(description = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "标准价格(元/小时)")
    private BigDecimal price;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;
}
