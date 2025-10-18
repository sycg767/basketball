package com.basketball.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝支付配置
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    /**
     * 支付宝网关地址
     */
    private String gatewayUrl = "https://openapi.alipay.com/gateway.do";

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String privateKey;

    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;

    /**
     * 签名类型
     */
    private String signType = "RSA2";

    /**
     * 字符编码
     */
    private String charset = "UTF-8";

    /**
     * 返回格式
     */
    private String format = "json";

    /**
     * 同步回调地址
     */
    private String returnUrl;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 是否启用 (默认false,避免未配置时启动报错)
     */
    private Boolean enabled = false;

    /**
     * 创建支付宝客户端
     * 注意: 只有当enabled=true时才创建Bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "alipay", name = "enabled", havingValue = "true")
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
                gatewayUrl,
                appId,
                privateKey,
                format,
                charset,
                alipayPublicKey,
                signType
        );
    }
}
