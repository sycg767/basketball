package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.dto.request.*;
import com.basketball.service.IUserService;
import com.basketball.vo.UserVO;
import com.basketball.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等接口")
@RestController
@Validated
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "新用户注册接口")
    @PostMapping("/api/auth/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT Token")
    @PostMapping("/api/auth/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        Map<String, Object> result = userService.login(loginDTO);
        return Result.success(result);
    }

    /**
     * 手机验证码登录
     */
    @Operation(summary = "手机验证码登录", description = "使用手机号和验证码登录，如用户不存在则自动注册")
    @PostMapping("/api/auth/phone/login")
    public Result<Map<String, Object>> loginByPhone(@Valid @RequestBody SmsLoginDTO loginDTO) {
        Map<String, Object> result = userService.loginByPhone(loginDTO);
        return Result.success(result);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "用户退出登录，清除Token")
    @PostMapping("/api/auth/logout")
    public Result<Void> logout(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId) {
        // 清除Redis中的Token
        String redisKey = "user:token:" + userId;
        // 由拦截器或过滤器处理Token清除逻辑
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "根据Token获取当前登录用户的详细信息")
    @GetMapping("/api/user/info")
    public Result<UserVO> getUserInfo(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId) {
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "更新当前用户的基本信息")
    @PutMapping("/api/user/info")
    public Result<Void> updateUserInfo(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUserInfo(userId, updateDTO);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    @PutMapping("/api/user/password")
    public Result<Void> updatePassword(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId,
            @Valid @RequestBody PasswordUpdateDTO passwordDTO) {
        userService.updatePassword(userId, passwordDTO);
        return Result.success();
    }

    /**
     * 绑定手机号
     */
    @Operation(summary = "绑定手机号", description = "为当前用户绑定手机号，需要验证码验证")
    @PostMapping("/api/user/bind/phone")
    public Result<Void> bindPhone(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId,
            @Valid @RequestBody PhoneBindDTO bindDTO) {
        userService.bindPhone(userId, bindDTO);
        return Result.success("手机号绑定成功");
    }

    /**
     * 解绑手机号
     */
    @Operation(summary = "解绑手机号", description = "解绑当前用户的手机号")
    @DeleteMapping("/api/user/bind/phone")
    public Result<Void> unbindPhone(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId) {
        userService.unbindPhone(userId);
        return Result.success("手机号解绑成功");
    }
}