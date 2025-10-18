package com.basketball.controller;

import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.dto.request.UserUpdateDTO;
import com.basketball.service.IUserService;
import com.basketball.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户管理控制器（管理端）
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Tag(name = "用户管理（管理员）", description = "管理员用户管理接口")
@RestController
@RequestMapping("/api/admin/user")
@Validated
public class AdminUserController {

    @Resource
    private IUserService userService;

    /**
     * 获取用户列表（分页）
     */
    @Operation(summary = "获取用户列表", description = "分页查询用户列表")
    @GetMapping("/list")
    public Result<PageResult<UserVO>> getUserList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "会员等级") @RequestParam(required = false) Integer memberLevel,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        PageResult<UserVO> result = userService.getUserList(page, pageSize, username, memberLevel, status);
        return Result.success(result);
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "管理员更新用户信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUserInfo(id, updateDTO);
        return Result.success();
    }

    /**
     * 启用/禁用用户
     */
    @Operation(summary = "启用/禁用用户", description = "管理员启用或禁用用户")
    @PutMapping("/{id}/status")
    public Result<Void> toggleUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态", required = true) @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "管理员删除用户（软删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
