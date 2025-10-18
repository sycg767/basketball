package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.exception.ErrorCode;
import com.basketball.dto.request.PaymentCreateDTO;
import com.basketball.dto.response.PaymentResultVO;
import com.basketball.entity.PaymentConfig;
import com.basketball.entity.PaymentNotifyLog;
import com.basketball.entity.PaymentOrder;
import com.basketball.mapper.PaymentConfigMapper;
import com.basketball.mapper.PaymentNotifyLogMapper;
import com.basketball.mapper.PaymentOrderMapper;
import com.basketball.service.IPaymentService;
import com.basketball.service.INotificationService;
import com.basketball.dto.request.NotificationSendDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 统一支付服务实现
 * 注意: 这是模拟实现,生产环境需要集成真实的支付SDK
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Service
public class PaymentServiceImpl implements IPaymentService {

    @Resource
    private PaymentOrderMapper paymentOrderMapper;

    @Resource
    private PaymentConfigMapper paymentConfigMapper;

    @Resource
    private PaymentNotifyLogMapper paymentNotifyLogMapper;

    @Resource
    private INotificationService notificationService;

    /**
     * 创建支付订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO createPayment(Long userId, PaymentCreateDTO createDTO) {
        log.info("创建支付订单: userId={}, businessNo={}, paymentType={}, amount={}",
                userId, createDTO.getBusinessNo(), createDTO.getPaymentType(), createDTO.getAmount());

        // 1. 查询支付配置
        LambdaQueryWrapper<PaymentConfig> configQuery = new LambdaQueryWrapper<>();
        configQuery.eq(PaymentConfig::getPayMethod, createDTO.getPaymentType())
                .eq(PaymentConfig::getStatus, 1);
        PaymentConfig config = paymentConfigMapper.selectOne(configQuery);

        if (config == null) {
            // 测试环境：如果找不到启用的支付配置，仍然允许创建支付订单（模拟模式）
            log.warn("未找到启用的支付配置: paymentType={}, 当前为测试模式，继续创建支付订单", createDTO.getPaymentType());
            // throw new BusinessException("不支持的支付方式");
        }

        // 2. 检查是否已有待支付订单
        LambdaQueryWrapper<PaymentOrder> existQuery = new LambdaQueryWrapper<>();
        existQuery.eq(PaymentOrder::getBusinessNo, createDTO.getBusinessNo())
                .in(PaymentOrder::getStatus, 0, 1); // 待支付或支付中
        PaymentOrder existOrder = paymentOrderMapper.selectOne(existQuery);

        if (existOrder != null) {
            log.info("业务订单已存在待支付订单: paymentNo={}", existOrder.getPaymentNo());
            return convertToVO(existOrder);
        }

        // 3. 创建支付订单
        PaymentOrder order = new PaymentOrder();
        order.setPaymentNo(generatePaymentNo());
        order.setBusinessNo(createDTO.getBusinessNo());
        order.setBusinessType(createDTO.getBusinessType());
        order.setUserId(userId);
        order.setPaymentType(createDTO.getPaymentType());
        order.setAmount(createDTO.getAmount());

        // 计算手续费（当前无手续费配置，设为0）
        BigDecimal feeAmount = BigDecimal.ZERO;
        order.setFeeAmount(feeAmount);
        order.setActualAmount(createDTO.getAmount());

        order.setStatus(0); // 待支付
        order.setClientIp(createDTO.getClientIp());
        order.setExpireTime(LocalDateTime.now().plusMinutes(30)); // 30分钟过期
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        // 4. 模拟调用支付接口
        if (createDTO.getPaymentType().startsWith("wechat_native")) {
            // 微信扫码支付 - 生成模拟二维码URL
            order.setQrCodeUrl("https://api.mch.weixin.qq.com/pay/qrcode?data=mock_" + order.getPaymentNo());
            order.setThirdPartyOrderNo("MOCK_WX_" + System.currentTimeMillis());
            log.info("生成微信扫码支付二维码: qrCodeUrl={}", order.getQrCodeUrl());

        } else if (createDTO.getPaymentType().startsWith("wechat_h5")) {
            // 微信H5支付 - 生成模拟跳转URL
            order.setPaymentUrl("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=mock_" + order.getPaymentNo());
            order.setThirdPartyOrderNo("MOCK_WX_H5_" + System.currentTimeMillis());
            log.info("生成微信H5支付链接: paymentUrl={}", order.getPaymentUrl());

        } else if (createDTO.getPaymentType().startsWith("alipay_pc")) {
            // 支付宝PC支付 - 生成模拟跳转URL
            order.setPaymentUrl("https://openapi.alipay.com/gateway.do?trade_no=mock_" + order.getPaymentNo());
            order.setThirdPartyOrderNo("MOCK_ALIPAY_" + System.currentTimeMillis());
            log.info("生成支付宝PC支付链接: paymentUrl={}", order.getPaymentUrl());

        } else if (createDTO.getPaymentType().startsWith("alipay_wap")) {
            // 支付宝WAP支付 - 生成模拟跳转URL
            order.setPaymentUrl("https://openapi.alipay.com/gateway.do?trade_no=mock_wap_" + order.getPaymentNo());
            order.setThirdPartyOrderNo("MOCK_ALIPAY_WAP_" + System.currentTimeMillis());
            log.info("生成支付宝WAP支付链接: paymentUrl={}", order.getPaymentUrl());

        } else {
            throw new BusinessException("不支持的支付方式");
        }

        // 5. 保存支付订单
        paymentOrderMapper.insert(order);

        log.info("支付订单创建成功: paymentNo={}, thirdPartyOrderNo={}",
                order.getPaymentNo(), order.getThirdPartyOrderNo());

        return convertToVO(order);
    }

    /**
     * 查询支付订单
     */
    @Override
    public PaymentResultVO queryPayment(String paymentNo) {
        LambdaQueryWrapper<PaymentOrder> query = new LambdaQueryWrapper<>();
        query.eq(PaymentOrder::getPaymentNo, paymentNo);
        PaymentOrder order = paymentOrderMapper.selectOne(query);

        if (order == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        // TODO: 生产环境需要调用第三方支付接口查询真实支付状态

        return convertToVO(order);
    }

    /**
     * 处理支付回调
     * 注意: 这是模拟实现,真实环境需要验签和解密
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleCallback(String paymentType, Map<String, String> callbackData) {
        log.info("接收支付回调: paymentType={}, data={}", paymentType, callbackData);

        try {
            // 1. 记录回调日志
            PaymentNotifyLog notifyLog = new PaymentNotifyLog();
            notifyLog.setPaymentNo(callbackData.getOrDefault("out_trade_no", ""));
            notifyLog.setNotifyType("callback");
            notifyLog.setNotifyData(callbackData.toString());
            notifyLog.setStatus(0); // 待处理
            notifyLog.setRetryCount(0);
            notifyLog.setCreateTime(LocalDateTime.now());
            paymentNotifyLogMapper.insert(notifyLog);

            // 2. 模拟验签 (生产环境需要真实验签)
            String paymentNo = callbackData.get("out_trade_no");
            String tradeNo = callbackData.get("trade_no");
            String tradeStatus = callbackData.getOrDefault("trade_status", "TRADE_SUCCESS");

            if (paymentNo == null) {
                notifyLog.setStatus(2);
                notifyLog.setErrorMsg("缺少订单号");
                paymentNotifyLogMapper.updateById(notifyLog);
                return "fail";
            }

            // 3. 查询订单
            LambdaQueryWrapper<PaymentOrder> query = new LambdaQueryWrapper<>();
            query.eq(PaymentOrder::getPaymentNo, paymentNo);
            PaymentOrder order = paymentOrderMapper.selectOne(query);

            if (order == null) {
                notifyLog.setStatus(2);
                notifyLog.setErrorMsg("订单不存在");
                paymentNotifyLogMapper.updateById(notifyLog);
                return "fail";
            }

            // 4. 检查订单状态
            if (order.getStatus() == 2) {
                log.info("订单已支付,忽略回调: paymentNo={}", paymentNo);
                notifyLog.setStatus(1);
                notifyLog.setResult("订单已支付");
                paymentNotifyLogMapper.updateById(notifyLog);
                return "success";
            }

            // 5. 更新订单状态
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                order.setStatus(2); // 支付成功
                order.setThirdPartyTradeNo(tradeNo);
                order.setPaidTime(LocalDateTime.now());
                order.setUpdateTime(LocalDateTime.now());
                paymentOrderMapper.updateById(order);

                // TODO: 更新业务订单状态 (预订订单、会员卡订单等)

                log.info("支付成功: paymentNo={}, tradeNo={}", paymentNo, tradeNo);

                // 发送支付成功通知
                try {
                    NotificationSendDTO notificationDTO = new NotificationSendDTO();
                    notificationDTO.setUserId(order.getUserId());
                    notificationDTO.setTemplateCode("PAYMENT_SUCCESS");
                    notificationDTO.setBizId(order.getBusinessNo());
                    notificationDTO.setBizType(order.getBusinessType());
                    notificationDTO.setNotificationType("system");

                    Map<String, Object> params = new HashMap<>();
                    params.put("paymentNo", paymentNo);
                    params.put("amount", order.getActualAmount().toString());
                    params.put("businessNo", order.getBusinessNo());
                    notificationDTO.setParams(params);

                    notificationService.sendNotification(notificationDTO);
                } catch (Exception e) {
                    log.error("发送支付成功通知失败: paymentNo={}", paymentNo, e);
                }

                notifyLog.setStatus(1);
                notifyLog.setResult("处理成功");
                paymentNotifyLogMapper.updateById(notifyLog);

                return "success";
            } else {
                order.setStatus(3); // 支付失败
                order.setErrorMsg("支付失败: " + tradeStatus);
                order.setUpdateTime(LocalDateTime.now());
                paymentOrderMapper.updateById(order);

                notifyLog.setStatus(1);
                notifyLog.setResult("支付失败");
                paymentNotifyLogMapper.updateById(notifyLog);

                return "fail";
            }

        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            return "fail";
        }
    }

    /**
     * 取消支付订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPayment(String paymentNo) {
        LambdaQueryWrapper<PaymentOrder> query = new LambdaQueryWrapper<>();
        query.eq(PaymentOrder::getPaymentNo, paymentNo);
        PaymentOrder order = paymentOrderMapper.selectOne(query);

        if (order == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        if (order.getStatus() == 2) {
            throw new BusinessException("订单已支付,无法取消");
        }

        if (order.getStatus() == 4) {
            throw new BusinessException("订单已取消");
        }

        order.setStatus(4); // 已取消
        order.setUpdateTime(LocalDateTime.now());
        paymentOrderMapper.updateById(order);

        log.info("取消支付订单: paymentNo={}", paymentNo);
    }

    /**
     * 申请退款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(String paymentNo, String refundReason) {
        LambdaQueryWrapper<PaymentOrder> query = new LambdaQueryWrapper<>();
        query.eq(PaymentOrder::getPaymentNo, paymentNo);
        PaymentOrder order = paymentOrderMapper.selectOne(query);

        if (order == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        if (order.getStatus() != 2) {
            throw new BusinessException("只有已支付订单才能退款");
        }

        if (order.getStatus() == 5) {
            throw new BusinessException("订单已退款");
        }

        // TODO: 调用第三方支付接口申请退款

        order.setStatus(5); // 已退款
        order.setRefundAmount(order.getActualAmount());
        order.setRefundTime(LocalDateTime.now());
        order.setRefundReason(refundReason);
        order.setUpdateTime(LocalDateTime.now());
        paymentOrderMapper.updateById(order);

        log.info("退款成功: paymentNo={}, amount={}", paymentNo, order.getRefundAmount());

        // 发送退款成功通知
        try {
            NotificationSendDTO notificationDTO = new NotificationSendDTO();
            notificationDTO.setUserId(order.getUserId());
            notificationDTO.setTemplateCode("REFUND_SUCCESS");
            notificationDTO.setBizId(order.getBusinessNo());
            notificationDTO.setBizType(order.getBusinessType());
            notificationDTO.setNotificationType("system");

            Map<String, Object> params = new HashMap<>();
            params.put("paymentNo", paymentNo);
            params.put("refundAmount", order.getRefundAmount().toString());
            params.put("businessNo", order.getBusinessNo());
            params.put("refundReason", refundReason != null ? refundReason : "用户申请退款");
            notificationDTO.setParams(params);

            notificationService.sendNotification(notificationDTO);
        } catch (Exception e) {
            log.error("发送退款成功通知失败: paymentNo={}", paymentNo, e);
        }
    }

    /**
     * 生成支付订单号
     */
    private String generatePaymentNo() {
        // 格式: PAY + yyyyMMddHHmmss + 6位随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "PAY" + timestamp + random;
    }

    /**
     * 转换为VO
     */
    private PaymentResultVO convertToVO(PaymentOrder order) {
        PaymentResultVO vo = new PaymentResultVO();
        BeanUtils.copyProperties(order, vo);
        return vo;
    }
}
