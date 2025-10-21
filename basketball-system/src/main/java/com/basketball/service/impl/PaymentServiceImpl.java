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
import com.basketball.mapper.BookingMapper;
import com.basketball.entity.Booking;
import com.basketball.mapper.UserMapper;
import com.basketball.entity.User;
import com.basketball.mapper.PointsRecordMapper;
import com.basketball.entity.PointsRecord;
import com.basketball.service.IPaymentService;
import com.basketball.service.INotificationService;
import com.basketball.dto.request.NotificationSendDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
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

    @Resource
    private BookingMapper bookingMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private PointsRecordMapper pointsRecordMapper;

    @Resource
    private com.basketball.mapper.FinancialRecordMapper financialRecordMapper;

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

        // 2. 检查是否已有待支付订单，自动取消所有旧订单
        LambdaQueryWrapper<PaymentOrder> existQuery = new LambdaQueryWrapper<>();
        existQuery.eq(PaymentOrder::getBusinessNo, createDTO.getBusinessNo())
                .in(PaymentOrder::getStatus, 0, 1); // 待支付或支付中
        List<PaymentOrder> existOrders = paymentOrderMapper.selectList(existQuery);

        if (!existOrders.isEmpty()) {
            // 自动取消所有未支付的旧订单
            for (PaymentOrder existOrder : existOrders) {
                existOrder.setStatus(4); // 已取消
                existOrder.setErrorMsg("创建新订单，旧订单自动取消");
                existOrder.setUpdateTime(LocalDateTime.now());
                paymentOrderMapper.updateById(existOrder);
                log.info("自动取消旧订单: paymentNo={}", existOrder.getPaymentNo());
            }
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
            order.setQrCodeUrl("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=mock_wechat_" + order.getPaymentNo());
            order.setThirdPartyOrderNo("MOCK_WX_" + System.currentTimeMillis());
            log.info("生成微信扫码支付二维码: qrCodeUrl={}", order.getQrCodeUrl());

        } else if (createDTO.getPaymentType().startsWith("wechat_h5")) {
            // 微信H5支付 - 生成模拟跳转URL
            order.setPaymentUrl("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=mock_" + order.getPaymentNo());
            order.setThirdPartyOrderNo("MOCK_WX_H5_" + System.currentTimeMillis());
            log.info("生成微信H5支付链接: paymentUrl={}", order.getPaymentUrl());

        } else if (createDTO.getPaymentType().startsWith("alipay_page") || createDTO.getPaymentType().startsWith("alipay_pc")) {
            // 支付宝PC/页面支付 - 生成模拟二维码URL（像微信一样扫码支付）
            order.setQrCodeUrl("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=mock_alipay_" + order.getPaymentNo());
            order.setThirdPartyOrderNo("MOCK_ALIPAY_" + System.currentTimeMillis());
            log.info("生成支付宝扫码支付二维码: qrCodeUrl={}", order.getQrCodeUrl());

        } else if (createDTO.getPaymentType().startsWith("alipay_wap")) {
            // 支付宝WAP支付 - 生成模拟跳转URL
            order.setPaymentUrl("https://openapi.alipay.com/gateway.do?trade_no=mock_wap_" + order.getPaymentNo());
            order.setThirdPartyOrderNo("MOCK_ALIPAY_WAP_" + System.currentTimeMillis());
            log.info("生成支付宝WAP支付链接: paymentUrl={}", order.getPaymentUrl());

        } else {
            throw new BusinessException("不支持的支付方式: " + createDTO.getPaymentType());
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
            notifyLog.setOrderNo(callbackData.getOrDefault("out_trade_no", ""));
            notifyLog.setNotifyType("callback");
            notifyLog.setNotifyData(callbackData.toString());
            notifyLog.setProcessStatus(0); // 待处理
            notifyLog.setRetryCount(0);
            notifyLog.setCreateTime(LocalDateTime.now());
            paymentNotifyLogMapper.insert(notifyLog);

            // 2. 模拟验签 (生产环境需要真实验签)
            String paymentNo = callbackData.get("out_trade_no");
            String tradeNo = callbackData.get("trade_no");
            // 兼容微信(trade_state)和支付宝(trade_status)两种格式
            String tradeStatus = callbackData.get("trade_status");
            String tradeState = callbackData.get("trade_state");

            if (paymentNo == null) {
                notifyLog.setProcessStatus(2);
                notifyLog.setErrorMsg("缺少订单号");
                paymentNotifyLogMapper.updateById(notifyLog);
                return "fail";
            }

            // 3. 查询订单
            LambdaQueryWrapper<PaymentOrder> query = new LambdaQueryWrapper<>();
            query.eq(PaymentOrder::getPaymentNo, paymentNo);
            PaymentOrder order = paymentOrderMapper.selectOne(query);

            if (order == null) {
                notifyLog.setProcessStatus(2);
                notifyLog.setErrorMsg("订单不存在");
                paymentNotifyLogMapper.updateById(notifyLog);
                return "fail";
            }

            // 4. 检查订单状态
            if (order.getStatus() == 2) {
                log.info("订单已支付,忽略回调: paymentNo={}", paymentNo);
                notifyLog.setProcessStatus(1);
                notifyLog.setProcessResult("订单已支付");
                paymentNotifyLogMapper.updateById(notifyLog);
                return "success";
            }

            // 5. 更新订单状态
            if ("TRADE_SUCCESS".equals(tradeStatus) || "SUCCESS".equals(tradeState)) {
                order.setStatus(2); // 支付成功
                order.setThirdPartyTradeNo(tradeNo);
                order.setPaidTime(LocalDateTime.now());
                order.setUpdateTime(LocalDateTime.now());
                paymentOrderMapper.updateById(order);

                // 更新业务订单状态
                if ("booking".equals(order.getBusinessType())) {
                    // 预订订单
                    LambdaQueryWrapper<Booking> bookingQuery = new LambdaQueryWrapper<>();
                    bookingQuery.eq(Booking::getBookingNo, order.getBusinessNo());
                    Booking booking = bookingMapper.selectOne(bookingQuery);

                    if (booking != null) {
                        booking.setStatus(1);
                        booking.setPayTime(LocalDateTime.now());
                        booking.setUpdateTime(LocalDateTime.now());
                        bookingMapper.updateById(booking);

                        log.info("预订订单状态更新成功: bookingId={}, bookingNo={}, status=1",
                                booking.getId(), booking.getBookingNo());

                        try {
                            addPointsForPayment(booking.getUserId(), booking.getActualPrice(), booking.getBookingNo());
                        } catch (Exception e) {
                            log.error("增加积分失败: userId={}, bookingNo={}", booking.getUserId(), booking.getBookingNo(), e);
                        }
                    } else {
                        log.warn("未找到对应的预订订单: businessNo={}", order.getBusinessNo());
                    }
                } else if ("balance_recharge".equals(order.getBusinessType())) {
                    // 余额充值
                    handleBalanceRecharge(order);
                }

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
                    params.put("amount", order.getActualAmount().toString());
                    params.put("orderNo", order.getBusinessNo());
                    notificationDTO.setParams(params);

                    notificationService.sendNotification(notificationDTO);
                } catch (Exception e) {
                    log.error("发送支付成功通知失败: paymentNo={}", paymentNo, e);
                }

                notifyLog.setProcessStatus(1);
                notifyLog.setProcessResult("处理成功");
                paymentNotifyLogMapper.updateById(notifyLog);

                return "success";
            } else {
                order.setStatus(3); // 支付失败
                order.setErrorMsg("支付失败: " + tradeStatus);
                order.setUpdateTime(LocalDateTime.now());
                paymentOrderMapper.updateById(order);

                notifyLog.setProcessStatus(1);
                notifyLog.setProcessResult("支付失败");
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
     * 处理余额充值
     */
    private void handleBalanceRecharge(PaymentOrder order) {
        User user = userMapper.selectById(order.getUserId());
        if (user == null) {
            log.error("用户不存在，余额充值失败: userId={}", order.getUserId());
            return;
        }

        BigDecimal balanceBefore = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal balanceAfter = balanceBefore.add(order.getActualAmount());

        // 更新用户余额
        user.setBalance(balanceAfter);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 创建财务流水记录
        com.basketball.entity.FinancialRecord record = new com.basketball.entity.FinancialRecord();
        record.setRecordNo(generateRecordNo());
        record.setRecordType(1); // 1-收入
        record.setBusinessType(5); // 5-余额充值
        record.setOrderNo(order.getPaymentNo());
        record.setUserId(order.getUserId());
        record.setAmount(order.getActualAmount());
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setPaymentMethod(getPaymentMethodCode(order.getPaymentType()));
        record.setDescription("账户余额充值");
        record.setCreateTime(LocalDateTime.now());
        financialRecordMapper.insert(record);

        log.info("余额充值成功: userId={}, amount={}, balanceBefore={}, balanceAfter={}, recordNo={}",
                user.getId(), order.getActualAmount(), balanceBefore, balanceAfter, record.getRecordNo());
    }

    /**
     * 生成财务流水号
     */
    private String generateRecordNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "REC" + timestamp + random;
    }

    /**
     * 获取支付方式代码
     */
    private Integer getPaymentMethodCode(String paymentType) {
        if (paymentType == null) return 0;
        if (paymentType.startsWith("wechat")) return 1; // 微信支付
        if (paymentType.startsWith("alipay")) return 2; // 支付宝
        return 0; // 其他
    }

    /**
     * 增加消费积分
     * 规则：消费金额的1%转换为积分（1元=1积分）
     */
    private void addPointsForPayment(Long userId, BigDecimal amount, String orderNo) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("用户不存在，无法增加积分: userId={}", userId);
            return;
        }

        // 计算积分：消费金额的1%（向下取整）
        int pointsToAdd = amount.intValue(); // 1元=1积分
        if (pointsToAdd <= 0) {
            log.info("消费金额太小，不增加积分: amount={}", amount);
            return;
        }

        // 记录变动前积分
        int pointsBefore = user.getPoints() != null ? user.getPoints() : 0;
        int pointsAfter = pointsBefore + pointsToAdd;

        // 更新用户积分
        user.setPoints(pointsAfter);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 创建积分记录
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(pointsToAdd);
        record.setType(1); // 1-消费赠送
        record.setRelatedOrder(orderNo);
        record.setPointsBefore(pointsBefore);
        record.setPointsAfter(pointsAfter);
        record.setExpireDate(LocalDate.now().plusYears(1)); // 积分1年后过期
        record.setRemark("消费赠送积分：订单" + orderNo + "，消费" + amount + "元");
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        log.info("增加消费积分成功: userId={}, points={}, orderNo={}, amount={}",
                userId, pointsToAdd, orderNo, amount);
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
