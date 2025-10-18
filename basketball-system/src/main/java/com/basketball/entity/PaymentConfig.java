package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 支付配置实体类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_config")
@Schema(description = "支付配置实体")
public class PaymentConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "支付方式: wechat_native, wechat_jsapi, alipay_page, alipay_wap, balance, member_card")
    private String payMethod;

    @Schema(description = "支付方式名称")
    private String payName;

    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "商户ID")
    private String merchantId;

    @Schema(description = "API密钥")
    private String apiKey;

    @Schema(description = "应用密钥")
    private String appSecret;

    @Schema(description = "私钥")
    private String privateKey;

    @Schema(description = "公钥")
    private String publicKey;

    @Schema(description = "异步通知URL")
    private String notifyUrl;

    @Schema(description = "同步返回URL")
    private String returnUrl;

    @Schema(description = "是否沙箱环境: 0-否, 1-是")
    private Integer isSandbox;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;
}
