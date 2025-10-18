package com.basketball.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.basketball.common.exception.BusinessException;
import com.basketball.config.AlipayConfig;
import com.basketball.dto.response.PaymentResultVO;
import com.basketball.service.IAlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝支付服务实现
 *
 * 注意事项:
 * 1. 需要在application.yml中配置支付宝参数
 * 2. 需要配置应用私钥和支付宝公钥
 * 3. enabled=false时,所有操作都会抛出异常提示未配置
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Service
public class AlipayServiceImpl implements IAlipayService {

    @Resource
    private AlipayConfig alipayConfig;

    @Autowired(required = false)
    private AlipayClient alipayClient;

    /**
     * 电脑网站支付 (PC支付)
     */
    @Override
    public PaymentResultVO createPageOrder(String paymentNo, String subject, BigDecimal amount, String returnUrl, String notifyUrl) {
        checkConfig();
        log.info("创建支付宝PC支付订单: paymentNo={}, amount={}", paymentNo, amount);

        try {
            // 构建支付请求
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setReturnUrl(returnUrl);
            request.setNotifyUrl(notifyUrl);

            // 业务参数
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(paymentNo);
            model.setTotalAmount(amount.toString());
            model.setSubject(subject);
            model.setProductCode("FAST_INSTANT_TRADE_PAY"); // 固定值
            request.setBizModel(model);

            // 调用SDK生成表单
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);

            if (!response.isSuccess()) {
                log.error("支付宝PC支付下单失败: {}", response.getBody());
                throw new BusinessException("支付宝下单失败");
            }

            // 封装返回结果
            PaymentResultVO result = new PaymentResultVO();
            result.setPaymentNo(paymentNo);
            result.setAmount(amount);
            result.setStatus(0);
            result.setPaymentUrl(response.getBody()); // 返回HTML表单,前端直接渲染即可

            log.info("支付宝PC支付订单创建成功");
            return result;

        } catch (AlipayApiException e) {
            log.error("支付宝PC支付下单失败", e);
            throw new BusinessException("支付宝下单失败: " + e.getMessage());
        }
    }

    /**
     * 手机网站支付 (WAP支付)
     */
    @Override
    public PaymentResultVO createWapOrder(String paymentNo, String subject, BigDecimal amount, String returnUrl, String notifyUrl) {
        checkConfig();
        log.info("创建支付宝WAP支付订单: paymentNo={}, amount={}", paymentNo, amount);

        try {
            // 构建支付请求
            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            request.setReturnUrl(returnUrl);
            request.setNotifyUrl(notifyUrl);

            // 业务参数
            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
            model.setOutTradeNo(paymentNo);
            model.setTotalAmount(amount.toString());
            model.setSubject(subject);
            model.setProductCode("QUICK_WAP_WAY"); // 固定值
            request.setBizModel(model);

            // 调用SDK生成表单
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);

            if (!response.isSuccess()) {
                log.error("支付宝WAP支付下单失败: {}", response.getBody());
                throw new BusinessException("支付宝下单失败");
            }

            // 封装返回结果
            PaymentResultVO result = new PaymentResultVO();
            result.setPaymentNo(paymentNo);
            result.setAmount(amount);
            result.setStatus(0);
            result.setPaymentUrl(response.getBody()); // 返回HTML表单

            log.info("支付宝WAP支付订单创建成功");
            return result;

        } catch (AlipayApiException e) {
            log.error("支付宝WAP支付下单失败", e);
            throw new BusinessException("支付宝下单失败: " + e.getMessage());
        }
    }

    /**
     * 查询订单
     */
    @Override
    public PaymentResultVO queryOrder(String paymentNo) {
        checkConfig();
        log.info("查询支付宝支付订单: paymentNo={}", paymentNo);

        try {
            // 构建查询请求
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

            // 业务参数
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(paymentNo);
            request.setBizModel(model);

            // 调用SDK查询
            AlipayTradeQueryResponse response = alipayClient.execute(request);

            if (!response.isSuccess()) {
                log.error("支付宝订单查询失败: {}", response.getMsg());
                throw new BusinessException("查询订单失败");
            }

            // 封装返回结果
            PaymentResultVO result = new PaymentResultVO();
            result.setPaymentNo(paymentNo);
            result.setThirdPartyTradeNo(response.getTradeNo());
            result.setAmount(new BigDecimal(response.getTotalAmount()));

            // 转换支付状态
            // WAIT_BUYER_PAY: 交易创建,等待买家付款
            // TRADE_CLOSED: 未付款交易超时关闭,或支付完成后全额退款
            // TRADE_SUCCESS: 交易支付成功
            // TRADE_FINISHED: 交易结束,不可退款
            String tradeStatus = response.getTradeStatus();
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                result.setStatus(2); // 支付成功
            } else if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
                result.setStatus(1); // 支付中
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                result.setStatus(4); // 已关闭
            }

            log.info("支付宝订单查询成功: tradeStatus={}", tradeStatus);
            return result;

        } catch (AlipayApiException e) {
            log.error("支付宝订单查询失败", e);
            throw new BusinessException("查询订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付回调通知
     */
    @Override
    public String handleNotify(Map<String, String> params) {
        checkConfig();
        log.info("接收支付宝支付回调通知");

        try {
            // 1. 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSignType()
            );

            if (!signVerified) {
                log.error("支付宝回调签名验证失败");
                return "fail";
            }

            // 2. 获取关键参数
            String outTradeNo = params.get("out_trade_no"); // 商户订单号
            String tradeNo = params.get("trade_no"); // 支付宝交易号
            String tradeStatus = params.get("trade_status"); // 交易状态

            log.info("支付宝回调: outTradeNo={}, tradeNo={}, tradeStatus={}",
                    outTradeNo, tradeNo, tradeStatus);

            // 3. 根据交易状态处理业务逻辑
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                // TODO: 更新订单状态为支付成功
                log.info("支付宝支付成功: outTradeNo={}", outTradeNo);
            }

            return "success";

        } catch (Exception e) {
            log.error("处理支付宝回调失败", e);
            return "fail";
        }
    }

    /**
     * 申请退款
     */
    @Override
    public void refund(String paymentNo, String refundNo, BigDecimal refundAmount, String reason) {
        checkConfig();
        log.info("申请支付宝退款: paymentNo={}, refundNo={}, refundAmount={}", paymentNo, refundNo, refundAmount);

        try {
            // 构建退款请求
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

            // 业务参数
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(paymentNo);
            model.setRefundAmount(refundAmount.toString());
            model.setRefundReason(reason);
            model.setOutRequestNo(refundNo); // 退款请求号
            request.setBizModel(model);

            // 调用SDK申请退款
            AlipayTradeRefundResponse response = alipayClient.execute(request);

            if (!response.isSuccess()) {
                log.error("支付宝退款失败: {}", response.getMsg());
                throw new BusinessException("退款失败: " + response.getMsg());
            }

            log.info("支付宝退款申请成功");

        } catch (AlipayApiException e) {
            log.error("支付宝退款失败", e);
            throw new BusinessException("退款失败: " + e.getMessage());
        }
    }

    /**
     * 关闭订单
     */
    @Override
    public void closeOrder(String paymentNo) {
        checkConfig();
        log.info("关闭支付宝订单: paymentNo={}", paymentNo);

        try {
            // 构建关单请求
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();

            // 业务参数
            AlipayTradeCloseModel model = new AlipayTradeCloseModel();
            model.setOutTradeNo(paymentNo);
            request.setBizModel(model);

            // 调用SDK关闭订单
            AlipayTradeCloseResponse response = alipayClient.execute(request);

            if (!response.isSuccess()) {
                log.error("支付宝关单失败: {}", response.getMsg());
                throw new BusinessException("关闭订单失败: " + response.getMsg());
            }

            log.info("支付宝订单关闭成功");

        } catch (AlipayApiException e) {
            log.error("支付宝关单失败", e);
            throw new BusinessException("关闭订单失败: " + e.getMessage());
        }
    }

    /**
     * 检查支付宝配置
     */
    private void checkConfig() {
        if (!alipayConfig.getEnabled() || alipayClient == null) {
            throw new BusinessException("支付宝支付未启用或未配置,请在application.yml中配置相关参数");
        }
    }
}
