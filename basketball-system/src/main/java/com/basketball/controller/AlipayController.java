package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.dto.request.PaymentCreateDTO;
import com.basketball.dto.response.PaymentResultVO;
import com.basketball.service.IAlipayService;
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
 * 支付宝支付Controller
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Tag(name = "支付宝支付接口")
@RestController
@RequestMapping("/api/payment/alipay")
@Validated
public class AlipayController {

    @Resource
    private IAlipayService alipayService;

    /**
     * 电脑网站支付 (PC)
     */
    @Operation(summary = "电脑网站支付", description = "PC端支付宝支付")
    @PostMapping("/page")
    public Result<PaymentResultVO> createPage(@Valid @RequestBody PaymentCreateDTO createDTO) {
        log.info("创建支付宝PC支付: businessNo={}, amount={}", createDTO.getBusinessNo(), createDTO.getAmount());

        String paymentNo = generatePaymentNo();

        PaymentResultVO result = alipayService.createPageOrder(
                paymentNo,
                createDTO.getDescription(),
                createDTO.getAmount(),
                "https://yourdomain.com/api/payment/alipay/return",
                "https://yourdomain.com/api/payment/callback/alipay"
        );

        return Result.success(result);
    }

    /**
     * 手机网站支付 (WAP)
     */
    @Operation(summary = "手机网站支付", description = "手机端支付宝支付")
    @PostMapping("/wap")
    public Result<PaymentResultVO> createWap(@Valid @RequestBody PaymentCreateDTO createDTO) {
        log.info("创建支付宝WAP支付: businessNo={}, amount={}", createDTO.getBusinessNo(), createDTO.getAmount());

        String paymentNo = generatePaymentNo();

        PaymentResultVO result = alipayService.createWapOrder(
                paymentNo,
                createDTO.getDescription(),
                createDTO.getAmount(),
                "https://yourdomain.com/api/payment/alipay/return",
                "https://yourdomain.com/api/payment/callback/alipay"
        );

        return Result.success(result);
    }

    /**
     * 查询订单
     */
    @Operation(summary = "查询订单", description = "查询支付宝订单状态")
    @GetMapping("/query/{paymentNo}")
    public Result<PaymentResultVO> query(
            @Parameter(description = "支付订单号") @PathVariable String paymentNo) {

        log.info("查询支付宝订单: paymentNo={}", paymentNo);

        PaymentResultVO result = alipayService.queryOrder(paymentNo);
        return Result.success(result);
    }

    /**
     * 支付异步通知
     */
    @Operation(summary = "支付回调", description = "支付宝异步通知")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        log.info("接收支付宝支付回调通知");

        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        return alipayService.handleNotify(params);
    }

    /**
     * 支付同步返回
     */
    @Operation(summary = "支付同步返回", description = "支付宝同步跳转")
    @GetMapping("/return")
    public String returnUrl(HttpServletRequest request) {
        log.info("接收支付宝同步返回");

        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        // 验证签名并处理
        String result = alipayService.handleNotify(params);

        if ("success".equals(result)) {
            return "支付成功!";
        } else {
            return "支付失败!";
        }
    }

    /**
     * 关闭订单
     */
    @Operation(summary = "关闭订单", description = "关闭支付宝订单")
    @PostMapping("/close/{paymentNo}")
    public Result<Void> close(
            @Parameter(description = "支付订单号") @PathVariable String paymentNo) {

        log.info("关闭支付宝订单: paymentNo={}", paymentNo);

        alipayService.closeOrder(paymentNo);
        return Result.success("订单已关闭");
    }

    /**
     * 生成支付订单号
     */
    private String generatePaymentNo() {
        return "ALI" + System.currentTimeMillis();
    }
}
