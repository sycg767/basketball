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
 * 用户第三方登录绑定实体类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_oauth")
@Schema(description = "第三方登录绑定实体")
public class UserOauth extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "绑定ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "第三方类型：wechat-微信，qq-QQ，weibo-微博，alipay-支付宝")
    private String oauthType;

    @Schema(description = "第三方OpenID")
    private String openId;

    @Schema(description = "第三方UnionID（微信）")
    private String unionId;

    @Schema(description = "第三方昵称")
    private String nickname;

    @Schema(description = "第三方头像")
    private String avatar;

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "令牌过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tokenExpireTime;

    @Schema(description = "绑定时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bindTime;

    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(description = "状态：0-解绑，1-已绑定")
    private Integer status;
}
