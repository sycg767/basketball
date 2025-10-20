package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String permissionName;

    private String permissionCode;

    private String permissionDesc;

    private String permissionType;

    private String url;

    private String method;

    private Long parentId;

    private Integer level;

    private Integer sort;

    private Integer status;

    private String icon;

    private String component;

    private String isMenu;

    private String isCache;

    private String isExternal;

    private String menuType;

    private String hidden;

    private String createTime;

    private String updateTime;
}