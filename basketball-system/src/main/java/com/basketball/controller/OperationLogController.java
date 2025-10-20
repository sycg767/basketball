package com.basketball.controller;

import com.basketball.entity.OperationLog;
import com.basketball.service.IOperationLogService;
import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/operation-log")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class OperationLogController {

    @Autowired
    private IOperationLogService operationLogService;

    @GetMapping("/page")
    public Result<PageResult<OperationLog>> getOperationLogPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) Integer status) {

        var page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<OperationLog>(current, size);
        var result = operationLogService.getOperationLogPage(page, userId, username, operation, status);

        PageResult<OperationLog> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent(),
                result.getPages()
        );

        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOperationStatistics() {
        Long successfulOperations = operationLogService.countSuccessfulOperations();
        Long failedOperations = operationLogService.countFailedOperations();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("successfulOperations", successfulOperations != null ? successfulOperations : 0L);
        statistics.put("failedOperations", failedOperations != null ? failedOperations : 0L);
        statistics.put("totalOperations", (successfulOperations != null ? successfulOperations : 0L) + (failedOperations != null ? failedOperations : 0L));

        return Result.success(statistics);
    }

    @GetMapping("/{id}")
    public Result<OperationLog> getOperationLogById(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        if (log == null) {
            return Result.error("操作日志不存在");
        }
        return Result.success(log);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteOperationLog(@PathVariable Long id) {
        boolean success = operationLogService.removeById(id);
        return success ? Result.success() : Result.error("删除操作日志失败");
    }

    @DeleteMapping("/clear")
    public Result<Void> clearOperationLogs() {
        boolean success = operationLogService.remove(null);
        return success ? Result.success() : Result.error("清空操作日志失败");
    }
}