package com.basketball.enums;

/**
 * 用户角色枚举
 *
 * @author Basketball Team
 * @date 2025-10-21
 */
public enum UserRoleEnum {

    /**
     * 普通用户
     */
    USER(0, "普通用户"),

    /**
     * 管理员
     */
    ADMIN(1, "管理员");

    private final Integer code;
    private final String description;

    UserRoleEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     */
    public static UserRoleEnum getByCode(Integer code) {
        if (code == null) {
            return USER;
        }
        for (UserRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return USER;
    }

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
}