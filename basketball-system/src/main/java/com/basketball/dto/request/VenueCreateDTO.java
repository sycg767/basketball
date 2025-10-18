package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 场地创建DTO
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Data
@Schema(description = "场地创建请求")
public class VenueCreateDTO {

    @NotBlank(message = "场地名称不能为空")
    @Schema(description = "场地名称", required = true)
    private String venueName;

    @NotBlank(message = "场地编码不能为空")
    @Schema(description = "场地编码", required = true)
    private String venueCode;

    @NotNull(message = "场地类型不能为空")
    @Schema(description = "场地类型: 1-室内全场, 2-室内半场, 3-室外全场, 4-室外半场", required = true)
    private Integer venueType;

    @NotNull(message = "场地面积不能为空")
    @Schema(description = "场地面积", required = true)
    private BigDecimal area;

    @NotNull(message = "容纳人数不能为空")
    @Schema(description = "容纳人数", required = true)
    private Integer capacity;

    @Schema(description = "地板类型")
    private String floorType;

    @Schema(description = "设施")
    private String facilities;

    @Schema(description = "主图URL")
    private String imageUrl;

    @Schema(description = "场地图片（多张，逗号分隔）")
    private String images;

    @Schema(description = "场地描述")
    private String description;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "排序")
    private Integer sortOrder;
}
