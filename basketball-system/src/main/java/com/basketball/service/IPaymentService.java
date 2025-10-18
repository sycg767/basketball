package com.basketball.service;

import com.basketball.dto.request.PaymentCreateDTO;
import com.basketball.dto.response.PaymentResultVO;

import java.util.Map;

/**
 * 统一支付服务接口
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
public interface IPaymentService {

    /**
     * 创建支付订单
     *
     * @param userId     用户ID
     * @param createDTO  创建支付订单请求
     * @return 支付结果 (包含二维码或跳转URL)
     */
    PaymentResultVO createPayment(Long userId, PaymentCreateDTO createDTO);

    /**
     * 查询支付订单状态
     *
     * @param paymentNo 支付订单号
     * @return 支付结果
     */
    PaymentResultVO queryPayment(String paymentNo);

    /**
     * 处理支付回调
     *
     * @param paymentType  支付方式
     * @param callbackData 回调数据
     * @return 处理结果
     */
    String handleCallback(String paymentType, Map<String, String> callbackData);

    /**
     * 取消支付订单
     *
     * @param paymentNo 支付订单号
     */
    void cancelPayment(String paymentNo);

    /**
     * 申请退款
     *
     * @param paymentNo   支付订单号
     * @param refundReason 退款原因
     */
    void refund(String paymentNo, String refundReason);
}
