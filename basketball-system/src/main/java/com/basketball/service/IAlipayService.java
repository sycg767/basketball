package com.basketball.service;

import com.basketball.dto.response.PaymentResultVO;

import java.math.BigDecimal;

/**
 * 支付宝支付服务接口
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
public interface IAlipayService {

    /**
     * 电脑网站支付 (PC支付)
     *
     * @param paymentNo   支付订单号
     * @param subject     商品标题
     * @param amount      支付金额(元)
     * @param returnUrl   同步回调地址
     * @param notifyUrl   异步通知地址
     * @return 支付结果(包含支付表单)
     */
    PaymentResultVO createPageOrder(String paymentNo, String subject, BigDecimal amount, String returnUrl, String notifyUrl);

    /**
     * 手机网站支付 (WAP支付)
     *
     * @param paymentNo   支付订单号
     * @param subject     商品标题
     * @param amount      支付金额(元)
     * @param returnUrl   同步回调地址
     * @param notifyUrl   异步通知地址
     * @return 支付结果(包含支付表单)
     */
    PaymentResultVO createWapOrder(String paymentNo, String subject, BigDecimal amount, String returnUrl, String notifyUrl);

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
     * @param params 回调参数
     * @return 处理结果
     */
    String handleNotify(java.util.Map<String, String> params);

    /**
     * 申请退款
     *
     * @param paymentNo    支付订单号
     * @param refundNo     退款订单号
     * @param refundAmount 退款金额(元)
     * @param reason       退款原因
     */
    void refund(String paymentNo, String refundNo, BigDecimal refundAmount, String reason);

    /**
     * 关闭订单
     *
     * @param paymentNo 支付订单号
     */
    void closeOrder(String paymentNo);
}
