package com.basketball.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayConfig {

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户API私钥路径
     */
    private String privateKeyPath;

    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;

    /**
     * 商户APIV3密钥
     */
    private String apiV3Key;

    /**
     * AppID (公众号/小程序/APP)
     */
    private String appId;

    /**
     * 通知回调URL
     */
    private String notifyUrl;

    /**
     * 是否启用 (默认false,避免未配置时启动报错)
     */
    private Boolean enabled = false;

    /**
     * 创建微信支付Config
     * 注意: 只有当enabled=true时才创建Bean,避免未配置时启动报错
     */
    @Bean
    @ConditionalOnProperty(prefix = "wechat.pay", name = "enabled", havingValue = "true")
    public Config wechatPayApiConfig() {
        try {
            // 使用自动更新证书的配置构造器
            return new RSAAutoCertificateConfig.Builder()
                    .merchantId(merchantId)
                    .privateKeyFromPath(privateKeyPath)
                    .merchantSerialNumber(merchantSerialNumber)
                    .apiV3Key(apiV3Key)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("微信支付配置初始化失败: " + e.getMessage(), e);
        }
    }
}
