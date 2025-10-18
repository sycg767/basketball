package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role")
@Schema(description = "角色实体")
public class Role extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色代码")
    private String roleCode;

    @Schema(description = "角色级别")
    private Integer roleLevel;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}