package com.basketball.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户VO
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@Data
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "会员等级：0-普通，1-银卡，2-金卡，3-钻石")
    private Integer memberLevel;

    @Schema(description = "积分")
    private Integer points;

    @Schema(description = "账户余额")
    private BigDecimal balance;

    @Schema(description = "用户角色：0-普通用户，1-管理员")
    private Integer role;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}