package com.basketball.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 场地VO
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Data
@Schema(description = "场地视图对象")
public class VenueVO {

    @Schema(description = "场地ID")
    private Long id;

    @Schema(description = "场地名称")
    private String venueName;

    @Schema(description = "场地编码")
    private String venueCode;

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

    @Schema(description = "状态：0-维护中，1-可用，2-已预订")
    private Integer status;

    @Schema(description = "主图URL")
    private String imageUrl;

    @Schema(description = "场地图片列表")
    private List<String> imageList;

    @Schema(description = "场地描述")
    private String description;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "基础价格(元/小时)")
    private BigDecimal basePrice;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
