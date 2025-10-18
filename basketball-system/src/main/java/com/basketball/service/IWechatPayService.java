package com.basketball.service;

import com.basketball.dto.response.PaymentResultVO;

import java.math.BigDecimal;

/**
 * 微信支付服务接口
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
public interface IWechatPayService {

    /**
     * Native扫码支付 - 生成二维码
     *
     * @param paymentNo   支付订单号
     * @param description 商品描述
     * @param amount      支付金额(元)
     * @param notifyUrl   回调地址
     * @return 支付结果(包含二维码URL)
     */
    PaymentResultVO createNativeOrder(String paymentNo, String description, BigDecimal amount, String notifyUrl);

    /**
     * JSAPI支付 - 公众号/小程序支付
     *
     * @param paymentNo   支付订单号
     * @param description 商品描述
     * @param amount      支付金额(元)
     * @param openId      用户OpenID
     * @param notifyUrl   回调地址
     * @return 支付结果(包含支付参数)
     */
    PaymentResultVO createJsapiOrder(String paymentNo, String description, BigDecimal amount, String openId, String notifyUrl);

    /**
     * H5支付 - 手机网页支付
     *
     * @param paymentNo   支付订单号
     * @param description 商品描述
     * @param amount      支付金额(元)
     * @param clientIp    用户IP
     * @param notifyUrl   回调地址
     * @return 支付结果(包含支付URL)
     */
    PaymentResultVO createH5Order(String paymentNo, String description, BigDecimal amount, String clientIp, String notifyUrl);

    /**
     * 查询订单状态
     *
     * @param paymentNo 支付订单号
     * @return 支付结果
     */
    PaymentResultVO queryOrder(String paymentNo);

    /**
     * 处理支付回调通知
     *
     * @param requestBody 回调请求体
     * @return 处理结果
     */
    String handleNotify(String requestBody);

    /**
     * 申请退款
     *
     * @param paymentNo    支付订单号
     * @param refundNo     退款订单号
     * @param totalAmount  订单总金额(元)
     * @param refundAmount 退款金额(元)
     * @param reason       退款原因
     */
    void refund(String paymentNo, String refundNo, BigDecimal totalAmount, BigDecimal refundAmount, String reason);

    /**
     * 关闭订单
     *
     * @param paymentNo 支付订单号
     */
    void closeOrder(String paymentNo);
}
