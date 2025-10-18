package com.basketball.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 创建课程请求DTO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "创建课程请求")
public class CourseCreateDTO {

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100")
    @Schema(description = "课程名称")
    private String courseName;

    @NotNull(message = "课程类型不能为空")
    @Min(value = 1, message = "课程类型无效")
    @Max(value = 4, message = "课程类型无效")
    @Schema(description = "课程类型: 1-基础班, 2-提高班, 3-专业班, 4-私教课")
    private Integer courseType;

    @NotNull(message = "教练ID不能为空")
    @Schema(description = "教练ID")
    private Long coachId;

    @Min(value = 1, message = "最大学员数至少为1")
    @Schema(description = "最大学员数")
    private Integer maxStudents;

    @Min(value = 1, message = "最小开班人数至少为1")
    @Schema(description = "最小开班人数")
    private Integer minStudents;

    @NotNull(message = "课程价格不能为空")
    @DecimalMin(value = "0.01", message = "课程价格必须大于0")
    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "会员价格")
    private BigDecimal memberPrice;

    @NotNull(message = "课时不能为空")
    @Min(value = 1, message = "课时至少为1分钟")
    @Schema(description = "课时(分钟)")
    private Integer duration;

    @Min(value = 1, message = "总课时数至少为1")
    @Schema(description = "总课时数")
    private Integer totalSessions;

    @Min(value = 1, message = "难度等级无效")
    @Max(value = 4, message = "难度等级无效")
    @Schema(description = "难度等级: 1-入门, 2-初级, 3-中级, 4-高级")
    private Integer difficultyLevel;

    @Schema(description = "适合年龄")
    private String ageRange;

    @Size(max = 2000, message = "课程描述长度不能超过2000")
    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "课程大纲(JSON格式)")
    private String syllabus;

    @Schema(description = "封面图片")
    private String coverImage;

    @Schema(description = "宣传视频")
    private String videoUrl;
}
