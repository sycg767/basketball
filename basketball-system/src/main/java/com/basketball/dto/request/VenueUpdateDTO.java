package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 场地更新DTO
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Data
@Schema(description = "场地更新请求")
public class VenueUpdateDTO {

    @Schema(description = "场地名称")
    private String venueName;

    @Schema(description = "场地类型: 1-室内全场, 2-室内半场, 3-室外全场, 4-室外半场")
    private Integer venueType;

    @Schema(description = "场地面积")
    private BigDecimal area;

    @Schema(description = "容纳人数")
    private Integer capacity;

    @Schema(description = "地板类型")
    private String floorType;

    @Schema(description = "设施")
    private String facilities;

    @Schema(description = "状态：0-维护中，1-可用")
    private Integer status;

    @Schema(description = "主图URL")
    private String imageUrl;

    @Schema(description = "场地图片")
    private String images;

    @Schema(description = "场地描述")
    private String description;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "排序")
    private Integer sortOrder;
}
