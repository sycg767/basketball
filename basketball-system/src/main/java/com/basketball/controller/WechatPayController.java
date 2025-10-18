package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.dto.request.PaymentCreateDTO;
import com.basketball.dto.response.PaymentResultVO;
import com.basketball.service.IWechatPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 微信支付Controller
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Tag(name = "微信支付接口")
@RestController
@RequestMapping("/api/payment/wechat")
@Validated
public class WechatPayController {

    @Resource
    private IWechatPayService wechatPayService;

    /**
     * Native扫码支付
     */
    @Operation(summary = "Native扫码支付", description = "生成微信支付二维码URL")
    @PostMapping("/native")
    public Result<PaymentResultVO> createNative(@Valid @RequestBody PaymentCreateDTO createDTO) {
        log.info("创建微信Native支付: businessNo={}, amount={}", createDTO.getBusinessNo(), createDTO.getAmount());

        // 生成支付订单号
        String paymentNo = generatePaymentNo();

        PaymentResultVO result = wechatPayService.createNativeOrder(
                paymentNo,
                createDTO.getDescription(),
                createDTO.getAmount(),
                "https://yourdomain.com/api/payment/callback/wechat"
        );

        return Result.success(result);
    }

    /**
     * JSAPI支付 (公众号/小程序)
     */
    @Operation(summary = "JSAPI支付", description = "公众号/小程序支付")
    @PostMapping("/jsapi")
    public Result<PaymentResultVO> createJsapi(
            @Valid @RequestBody PaymentCreateDTO createDTO,
            @Parameter(description = "用户OpenID") @RequestParam String openId) {

        log.info("创建微信JSAPI支付: businessNo={}, openId={}", createDTO.getBusinessNo(), openId);

        String paymentNo = generatePaymentNo();

        PaymentResultVO result = wechatPayService.createJsapiOrder(
                paymentNo,
                createDTO.getDescription(),
                createDTO.getAmount(),
                openId,
                "https://yourdomain.com/api/payment/callback/wechat"
        );

        return Result.success(result);
    }

    /**
     * H5支付
     */
    @Operation(summary = "H5支付", description = "手机网页支付")
    @PostMapping("/h5")
    public Result<PaymentResultVO> createH5(@Valid @RequestBody PaymentCreateDTO createDTO) {
        log.info("创建微信H5支付: businessNo={}, amount={}", createDTO.getBusinessNo(), createDTO.getAmount());

        String paymentNo = generatePaymentNo();

        PaymentResultVO result = wechatPayService.createH5Order(
                paymentNo,
                createDTO.getDescription(),
                createDTO.getAmount(),
                createDTO.getClientIp(),
                "https://yourdomain.com/api/payment/callback/wechat"
        );

        return Result.success(result);
    }

    /**
     * 查询订单
     */
    @Operation(summary = "查询订单", description = "查询微信支付订单状态")
    @GetMapping("/query/{paymentNo}")
    public Result<PaymentResultVO> query(
            @Parameter(description = "支付订单号") @PathVariable String paymentNo) {

        log.info("查询微信支付订单: paymentNo={}", paymentNo);

        PaymentResultVO result = wechatPayService.queryOrder(paymentNo);
        return Result.success(result);
    }

    /**
     * 支付回调通知
     */
    @Operation(summary = "支付回调", description = "微信支付异步通知")
    @PostMapping("/notify")
    public String notify(@RequestBody String requestBody) {
        log.info("接收微信支付回调通知");

        return wechatPayService.handleNotify(requestBody);
    }

    /**
     * 关闭订单
     */
    @Operation(summary = "关闭订单", description = "关闭微信支付订单")
    @PostMapping("/close/{paymentNo}")
    public Result<Void> close(
            @Parameter(description = "支付订单号") @PathVariable String paymentNo) {

        log.info("关闭微信支付订单: paymentNo={}", paymentNo);

        wechatPayService.closeOrder(paymentNo);
        return Result.success("订单已关闭");
    }

    /**
     * 生成支付订单号
     */
    private String generatePaymentNo() {
        return "WX" + System.currentTimeMillis();
    }
}
