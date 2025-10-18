package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.service.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟支付控制器
 * 仅用于测试环境，模拟支付回调
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@RestController
@RequestMapping("/api/mock/payment")
@Tag(name = "模拟支付", description = "测试环境模拟支付接口")
public class MockPaymentController {

    @Resource
    private IPaymentService paymentService;

    /**
     * 模拟扫码支付成功
     */
    @PostMapping("/scan-success/{paymentNo}")
    @Operation(summary = "模拟扫码支付成功", description = "测试环境使用，模拟用户扫码支付成功")
    public Result<String> mockScanSuccess(@PathVariable String paymentNo) {
        log.info("模拟扫码支付成功: paymentNo={}", paymentNo);
        
        try {
            // 构建模拟的回调数据
            Map<String, String> callbackData = new HashMap<>();
            callbackData.put("out_trade_no", paymentNo);  // 商户订单号
            callbackData.put("transaction_id", "MOCK_" + System.currentTimeMillis());  // 第三方交易号
            callbackData.put("trade_state", "SUCCESS");  // 交易状态
            callbackData.put("total_fee", "100");  // 金额（分）
            
            // 调用支付回调处理
            String result = paymentService.handleCallback("mock", callbackData);
            
            log.info("模拟支付回调处理完成: paymentNo={}, result={}", paymentNo, result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("模拟支付失败: paymentNo={}", paymentNo, e);
            return Result.error("模拟支付失败: " + e.getMessage());
        }
    }

    /**
     * 模拟支付失败
     */
    @PostMapping("/scan-fail/{paymentNo}")
    @Operation(summary = "模拟扫码支付失败", description = "测试环境使用，模拟用户扫码支付失败")
    public Result<String> mockScanFail(@PathVariable String paymentNo) {
        log.info("模拟扫码支付失败: paymentNo={}", paymentNo);
        
        try {
            Map<String, String> callbackData = new HashMap<>();
            callbackData.put("out_trade_no", paymentNo);
            callbackData.put("trade_state", "FAIL");
            callbackData.put("error_code", "MOCK_ERROR");
            callbackData.put("error_msg", "模拟支付失败");
            
            String result = paymentService.handleCallback("mock", callbackData);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("模拟支付失败处理异常: paymentNo={}", paymentNo, e);
            return Result.error("处理失败: " + e.getMessage());
        }
    }
}

