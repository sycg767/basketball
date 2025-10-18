package com.basketball.service.impl;

import com.basketball.common.exception.BusinessException;
import com.basketball.config.WechatPayConfig;
import com.basketball.dto.response.PaymentResultVO;
import com.basketball.service.IWechatPayService;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.h5.model.SceneInfo;
import com.wechat.pay.java.service.payments.h5.model.H5Info;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 微信支付服务实现
 *
 * 注意事项:
 * 1. 需要在application.yml中配置微信支付参数
 * 2. 需要配置商户证书和私钥
 * 3. 金额单位: 接口传入是元,微信支付需要转换为分
 * 4. enabled=false时,所有操作都会抛出异常提示未配置
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Service
public class WechatPayServiceImpl implements IWechatPayService {

    @Resource
    private WechatPayConfig wechatPayConfig;

    @Autowired(required = false)
    private Config wechatPayApiConfig;

    /**
     * Native扫码支付
     */
    @Override
    public PaymentResultVO createNativeOrder(String paymentNo, String description, BigDecimal amount, String notifyUrl) {
        checkConfig();
        log.info("创建微信Native支付订单: paymentNo={}, amount={}", paymentNo, amount);

        try {
            // 创建Native支付服务
            NativePayService service = new NativePayService.Builder().config(wechatPayApiConfig).build();

            // 构建请求参数
            PrepayRequest request = new PrepayRequest();

            // 基本参数
            request.setAppid(wechatPayConfig.getAppId());
            request.setMchid(wechatPayConfig.getMerchantId());
            request.setDescription(description);
            request.setOutTradeNo(paymentNo);
            request.setNotifyUrl(notifyUrl);

            // 金额信息 (分)
            Amount amountInfo = new Amount();
            amountInfo.setTotal(yuanToFen(amount));
            request.setAmount(amountInfo);

            // 调用下单方法,得到应答
            PrepayResponse response = service.prepay(request);

            // 封装返回结果
            PaymentResultVO result = new PaymentResultVO();
            result.setPaymentNo(paymentNo);
            result.setAmount(amount);
            result.setStatus(0); // 待支付
            result.setQrCodeUrl(response.getCodeUrl()); // 二维码URL

            log.info("微信Native支付订单创建成功: codeUrl={}", response.getCodeUrl());
            return result;

        } catch (ServiceException e) {
            log.error("微信Native支付下单失败", e);
            throw new BusinessException("微信支付下单失败: " + e.getErrorMessage());
        }
    }

    /**
     * JSAPI支付 (公众号/小程序)
     */
    @Override
    public PaymentResultVO createJsapiOrder(String paymentNo, String description, BigDecimal amount, String openId, String notifyUrl) {
        checkConfig();
        log.info("创建微信JSAPI支付订单: paymentNo={}, openId={}, amount={}", paymentNo, openId, amount);

        try {
            // 创建JSAPI支付服务
            JsapiService service = new JsapiService.Builder().config(wechatPayApiConfig).build();

            // 构建请求参数
            com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest request =
                    new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();

            request.setAppid(wechatPayConfig.getAppId());
            request.setMchid(wechatPayConfig.getMerchantId());
            request.setDescription(description);
            request.setOutTradeNo(paymentNo);
            request.setNotifyUrl(notifyUrl);

            // 用户标识
            Payer payer = new Payer();
            payer.setOpenid(openId);
            request.setPayer(payer);

            // 金额信息 (分)
            com.wechat.pay.java.service.payments.jsapi.model.Amount amountInfo =
                    new com.wechat.pay.java.service.payments.jsapi.model.Amount();
            amountInfo.setTotal(yuanToFen(amount));
            request.setAmount(amountInfo);

            // 调用下单方法
            com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse response = service.prepay(request);

            // 封装返回结果
            PaymentResultVO result = new PaymentResultVO();
            result.setPaymentNo(paymentNo);
            result.setAmount(amount);
            result.setStatus(0);
            result.setThirdPartyOrderNo(response.getPrepayId()); // prepay_id用于前端调起支付

            log.info("微信JSAPI支付订单创建成功: prepayId={}", response.getPrepayId());
            return result;

        } catch (ServiceException e) {
            log.error("微信JSAPI支付下单失败", e);
            throw new BusinessException("微信支付下单失败: " + e.getErrorMessage());
        }
    }

    /**
     * H5支付 (手机网页)
     */
    @Override
    public PaymentResultVO createH5Order(String paymentNo, String description, BigDecimal amount, String clientIp, String notifyUrl) {
        checkConfig();
        log.info("创建微信H5支付订单: paymentNo={}, amount={}", paymentNo, amount);

        try {
            // 创建H5支付服务
            H5Service service = new H5Service.Builder().config(wechatPayApiConfig).build();

            // 构建请求参数
            com.wechat.pay.java.service.payments.h5.model.PrepayRequest request =
                    new com.wechat.pay.java.service.payments.h5.model.PrepayRequest();

            request.setAppid(wechatPayConfig.getAppId());
            request.setMchid(wechatPayConfig.getMerchantId());
            request.setDescription(description);
            request.setOutTradeNo(paymentNo);
            request.setNotifyUrl(notifyUrl);

            // 场景信息
            SceneInfo sceneInfo = new SceneInfo();
            sceneInfo.setPayerClientIp(clientIp);
            H5Info h5Info = new H5Info();
            h5Info.setType("Wap");
            sceneInfo.setH5Info(h5Info);
            request.setSceneInfo(sceneInfo);

            // 金额信息 (分)
            com.wechat.pay.java.service.payments.h5.model.Amount amountInfo =
                    new com.wechat.pay.java.service.payments.h5.model.Amount();
            amountInfo.setTotal(yuanToFen(amount));
            request.setAmount(amountInfo);

            // 调用下单方法
            com.wechat.pay.java.service.payments.h5.model.PrepayResponse response = service.prepay(request);

            // 封装返回结果
            PaymentResultVO result = new PaymentResultVO();
            result.setPaymentNo(paymentNo);
            result.setAmount(amount);
            result.setStatus(0);
            result.setPaymentUrl(response.getH5Url()); // H5支付URL

            log.info("微信H5支付订单创建成功: h5Url={}", response.getH5Url());
            return result;

        } catch (ServiceException e) {
            log.error("微信H5支付下单失败", e);
            throw new BusinessException("微信支付下单失败: " + e.getErrorMessage());
        }
    }

    /**
     * 查询订单
     *
     * 注意: 微信支付V3 SDK查询订单的API较为复杂,这里简化实现
     * 生产环境建议使用统一支付服务(PaymentServiceImpl)查询数据库订单状态
     */
    @Override
    public PaymentResultVO queryOrder(String paymentNo) {
        checkConfig();
        log.info("查询微信支付订单: paymentNo={}", paymentNo);

        // TODO: 调用微信支付查询接口
        // 由于SDK的Transaction类路径较为复杂,建议：
        // 1. 开发阶段: 使用PaymentServiceImpl.queryPayment()查询数据库
        // 2. 生产环境: 根据实际SDK版本调整Transaction类的导入路径

        throw new BusinessException("请使用统一支付服务查询订单状态 (PaymentController.query)");
    }

    /**
     * 处理支付回调通知
     *
     * 注意: 这里简化处理,实际应该:
     * 1. 验证签名
     * 2. 解密报文
     * 3. 更新订单状态
     * 4. 返回成功响应
     */
    @Override
    public String handleNotify(String requestBody) {
        checkConfig();
        log.info("接收微信支付回调通知");

        try {
            // TODO: 实现完整的回调处理逻辑
            // 1. 使用Config验证签名
            // 2. 解密报文获取支付结果
            // 3. 更新订单状态
            // 4. 返回成功响应

            // 简化处理: 直接返回成功
            return "{\"code\": \"SUCCESS\", \"message\": \"成功\"}";

        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            return "{\"code\": \"FAIL\", \"message\": \"失败\"}";
        }
    }

    /**
     * 申请退款
     */
    @Override
    public void refund(String paymentNo, String refundNo, BigDecimal totalAmount, BigDecimal refundAmount, String reason) {
        checkConfig();
        log.info("申请微信支付退款: paymentNo={}, refundNo={}, refundAmount={}", paymentNo, refundNo, refundAmount);

        try {
            // 创建退款服务
            RefundService service = new RefundService.Builder().config(wechatPayApiConfig).build();

            // 构建请求参数
            CreateRequest request = new CreateRequest();
            request.setOutTradeNo(paymentNo);
            request.setOutRefundNo(refundNo);
            request.setReason(reason);
            request.setNotifyUrl(wechatPayConfig.getNotifyUrl()); // 退款回调地址

            // 金额信息 (分)
            AmountReq amountReq = new AmountReq();
            amountReq.setRefund(Long.valueOf(yuanToFen(refundAmount))); // 退款金额
            amountReq.setTotal(Long.valueOf(yuanToFen(totalAmount))); // 原订单金额
            amountReq.setCurrency("CNY");
            request.setAmount(amountReq);

            // 调用退款方法
            Refund refund = service.create(request);

            log.info("微信支付退款申请成功: refundId={}, status={}", refund.getRefundId(), refund.getStatus());

        } catch (ServiceException e) {
            log.error("微信支付退款失败", e);
            throw new BusinessException("退款失败: " + e.getErrorMessage());
        }
    }

    /**
     * 关闭订单
     */
    @Override
    public void closeOrder(String paymentNo) {
        checkConfig();
        log.info("关闭微信支付订单: paymentNo={}", paymentNo);

        try {
            // 使用Native服务关闭即可 (通用)
            NativePayService service = new NativePayService.Builder().config(wechatPayApiConfig).build();

            // 构建请求参数
            CloseOrderRequest request = new CloseOrderRequest();
            request.setMchid(wechatPayConfig.getMerchantId());
            request.setOutTradeNo(paymentNo);

            // 调用关闭方法
            service.closeOrder(request);

            log.info("微信支付订单关闭成功");

        } catch (ServiceException e) {
            log.error("微信支付订单关闭失败", e);
            throw new BusinessException("关闭订单失败: " + e.getErrorMessage());
        }
    }

    /**
     * 检查微信支付配置
     */
    private void checkConfig() {
        if (!wechatPayConfig.getEnabled() || wechatPayApiConfig == null) {
            throw new BusinessException("微信支付未启用或未配置,请在application.yml中配置相关参数");
        }
    }

    /**
     * 元转分
     */
    private Integer yuanToFen(BigDecimal amount) {
        return amount.multiply(new BigDecimal("100")).intValue();
    }

    /**
     * 分转元
     */
    private BigDecimal fenToYuan(Integer amount) {
        return new BigDecimal(amount).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
