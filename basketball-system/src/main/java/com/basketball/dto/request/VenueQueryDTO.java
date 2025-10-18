package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 场地查询DTO
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Data
@Schema(description = "场地查询请求")
public class VenueQueryDTO {

    @Schema(description = "关键字（场地名称或编码）")
    private String keyword;

    @Schema(description = "场地类型")
    private String venueType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "页码", required = true)
    private Integer page = 1;

    @Schema(description = "每页数量", required = true)
    private Integer size = 10;
}
