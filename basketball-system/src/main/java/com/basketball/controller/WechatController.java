package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.dto.request.WechatBindDTO;
import com.basketball.dto.response.WechatAuthUrlVO;
import com.basketball.service.IWechatService;
import com.basketball.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 微信登录控制器
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Tag(name = "微信登录", description = "微信OAuth登录和绑定接口")
@RestController
@RequestMapping("/api/wechat")
@Validated
public class WechatController {

    @Resource
    private IWechatService wechatService;

    /**
     * 获取微信授权URL
     */
    @Operation(summary = "获取微信授权URL", description = "生成微信扫码登录的授权URL")
    @GetMapping("/auth/url")
    public Result<WechatAuthUrlVO> getAuthorizationUrl() {
        log.info("请求获取微信授权URL");
        WechatAuthUrlVO authUrl = wechatService.getAuthorizationUrl();
        return Result.success(authUrl);
    }

    /**
     * 微信授权回调
     */
    @Operation(summary = "微信授权回调", description = "处理微信授权回调,完成登录")
    @GetMapping("/auth/callback")
    public Result<Map<String, Object>> handleCallback(
            @Parameter(description = "授权码", required = true)
            @RequestParam String code,
            @Parameter(description = "状态码", required = true)
            @RequestParam String state) {
        log.info("微信授权回调: code={}, state={}", code, state);
        Map<String, Object> result = wechatService.handleCallback(code, state);
        return Result.success(result);
    }

    /**
     * 绑定微信账号
     */
    @Operation(summary = "绑定微信账号", description = "为当前用户绑定微信账号")
    @PostMapping("/bind")
    public Result<Void> bindWechat(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId,
            @Valid @RequestBody WechatBindDTO bindDTO) {
        log.info("绑定微信账号: userId={}", userId);
        wechatService.bindWechat(userId, bindDTO.getCode(), bindDTO.getState());
        return Result.success("微信账号绑定成功");
    }

    /**
     * 解绑微信账号
     */
    @Operation(summary = "解绑微信账号", description = "解绑当前用户的微信账号")
    @DeleteMapping("/unbind")
    public Result<Void> unbindWechat(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId) {
        log.info("解绑微信账号: userId={}", userId);
        wechatService.unbindWechat(userId);
        return Result.success("微信账号解绑成功");
    }
}
