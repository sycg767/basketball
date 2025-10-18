package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.dto.request.PaymentCreateDTO;
import com.basketball.dto.response.PaymentResultVO;
import com.basketball.service.IPaymentService;
import com.basketball.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Tag(name = "支付管理", description = "统一支付接口 - 支持微信和支付宝")
@RestController
@RequestMapping("/api/payment")
@Validated
public class PaymentController {

    @Resource
    private IPaymentService paymentService;

    @Resource
    private HttpServletRequest request;

    /**
     * 创建支付订单
     */
    @Operation(summary = "创建支付订单", description = "创建支付订单,返回支付二维码或跳转链接")
    @PostMapping("/create")
    public Result<PaymentResultVO> createPayment(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId,
            @Valid @RequestBody PaymentCreateDTO createDTO) {

        // 设置客户端IP
        if (createDTO.getClientIp() == null) {
            createDTO.setClientIp(getClientIp());
        }

        log.info("创建支付订单请求: userId={}, businessNo={}, paymentType={}",
                userId, createDTO.getBusinessNo(), createDTO.getPaymentType());

        PaymentResultVO result = paymentService.createPayment(userId, createDTO);
        return Result.success(result);
    }

    /**
     * 查询支付订单
     */
    @Operation(summary = "查询支付订单", description = "根据支付订单号查询订单状态")
    @GetMapping("/query/{paymentNo}")
    public Result<PaymentResultVO> queryPayment(
            @Parameter(description = "支付订单号", required = true)
            @PathVariable String paymentNo) {

        log.info("查询支付订单: paymentNo={}", paymentNo);
        PaymentResultVO result = paymentService.queryPayment(paymentNo);
        return Result.success(result);
    }

    /**
     * 微信支付回调
     */
    @Operation(summary = "微信支付回调", description = "接收微信支付回调通知")
    @PostMapping("/callback/wechat")
    public String wechatCallback(@RequestBody String xmlData) {
        log.info("接收微信支付回调: {}", xmlData);

        // TODO: 解析XML数据并转换为Map
        Map<String, String> callbackData = new HashMap<>();
        callbackData.put("out_trade_no", "mock_payment_no");
        callbackData.put("trade_no", "mock_trade_no");
        callbackData.put("trade_status", "TRADE_SUCCESS");

        String result = paymentService.handleCallback("wechat", callbackData);

        // 微信要求返回XML格式
        if ("success".equals(result)) {
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } else {
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>";
        }
    }

    /**
     * 支付宝支付回调
     */
    @Operation(summary = "支付宝支付回调", description = "接收支付宝支付回调通知")
    @PostMapping("/callback/alipay")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        log.info("接收支付宝支付回调: {}", params);

        String result = paymentService.handleCallback("alipay", params);

        // 支付宝要求返回success或fail
        return result;
    }

    /**
     * 取消支付订单
     */
    @Operation(summary = "取消支付订单", description = "取消未支付的订单")
    @PostMapping("/cancel/{paymentNo}")
    public Result<Void> cancelPayment(
            @Parameter(description = "支付订单号", required = true)
            @PathVariable String paymentNo) {

        log.info("取消支付订单: paymentNo={}", paymentNo);
        paymentService.cancelPayment(paymentNo);
        return Result.success("订单已取消");
    }

    /**
     * 申请退款
     */
    @Operation(summary = "申请退款", description = "对已支付订单申请退款")
    @PostMapping("/refund/{paymentNo}")
    public Result<Void> refund(
            @Parameter(description = "支付订单号", required = true)
            @PathVariable String paymentNo,
            @Parameter(description = "退款原因")
            @RequestParam(required = false) String refundReason) {

        log.info("申请退款: paymentNo={}, reason={}", paymentNo, refundReason);
        paymentService.refund(paymentNo, refundReason);
        return Result.success("退款申请已提交");
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
