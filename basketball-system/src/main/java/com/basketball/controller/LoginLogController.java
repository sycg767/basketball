package com.basketball.controller;

import com.basketball.entity.LoginLog;
import com.basketball.service.ILoginLogService;
import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login-log")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class LoginLogController {

    @Autowired
    private ILoginLogService loginLogService;

    @GetMapping("/page")
    public Result<PageResult<LoginLog>> getLoginLogPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {

        var page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<LoginLog>(current, size);
        var result = loginLogService.getLoginLogPage(page, userId, username, status);

        PageResult<LoginLog> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent(),
                result.getPages()
        );

        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getLoginStatistics() {
        Long successfulLogins = loginLogService.countSuccessfulLogins();
        Long failedLogins = loginLogService.countFailedLogins();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("successfulLogins", successfulLogins != null ? successfulLogins : 0L);
        statistics.put("failedLogins", failedLogins != null ? failedLogins : 0L);
        statistics.put("totalLogins", (successfulLogins != null ? successfulLogins : 0L) + (failedLogins != null ? failedLogins : 0L));

        return Result.success(statistics);
    }

    @GetMapping("/{id}")
    public Result<LoginLog> getLoginLogById(@PathVariable Long id) {
        LoginLog log = loginLogService.getById(id);
        if (log == null) {
            return Result.error("登录日志不存在");
        }
        return Result.success(log);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteLoginLog(@PathVariable Long id) {
        boolean success = loginLogService.removeById(id);
        return success ? Result.success() : Result.error("删除登录日志失败");
    }

    @DeleteMapping("/clear")
    public Result<Void> clearLoginLogs() {
        boolean success = loginLogService.remove(null);
        return success ? Result.success() : Result.error("清空登录日志失败");
    }
}