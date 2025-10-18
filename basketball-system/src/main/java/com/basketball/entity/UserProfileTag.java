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
 * 用户画像标签实体类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_profile_tag")
@Schema(description = "用户画像标签")
public class UserProfileTag extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "标签ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "标签类型: behavior-行为, preference-偏好, consume-消费, risk-风险")
    private String tagType;

    @Schema(description = "标签Key")
    private String tagKey;

    @Schema(description = "标签Value")
    private String tagValue;

    @Schema(description = "标签权重(0-100)")
    private Integer weight;

    @Schema(description = "标签来源: system-系统生成, manual-人工标注, algorithm-算法推荐")
    private String source;

    @Schema(description = "标签描述")
    private String description;

    @Schema(description = "生效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime effectiveTime;

    @Schema(description = "失效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "状态: 0-失效, 1-有效")
    private Integer status;
}
