package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.dto.request.SmsSendDTO;
import com.basketball.service.ISmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 短信验证码控制器
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Tag(name = "短信验证码", description = "短信验证码发送和验证接口")
@RestController
@RequestMapping("/api/sms")
@Validated
public class SmsController {

    @Resource
    private ISmsService smsService;

    /**
     * 发送验证码
     */
    @Operation(summary = "发送短信验证码", description = "发送验证码到指定手机号，支持登录、注册、绑定、重置密码等场景")
    @PostMapping("/send")
    public Result<Void> sendVerificationCode(@Valid @RequestBody SmsSendDTO sendDTO) {
        log.info("发送短信验证码请求: phone={}, type={}, scene={}",
                sendDTO.getPhone(), sendDTO.getType(), sendDTO.getScene());

        smsService.sendVerificationCode(sendDTO.getPhone(), sendDTO.getType());

        return Result.success("验证码发送成功，请注意查收");
    }
}
