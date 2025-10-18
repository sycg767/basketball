package com.basketball.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课程查询请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "课程查询请求")
public class CourseQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "关键词(课程名称)")
    private String keyword;

    @Schema(description = "课程类型: 1-基础班, 2-提高班, 3-专业班, 4-私教课")
    private Integer courseType;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "难度等级: 1-入门, 2-初级, 3-中级, 4-高级")
    private Integer difficultyLevel;

    @Schema(description = "状态: 0-下架, 1-上架, 2-已满")
    private Integer status;
}
