package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.OperationLog;
import com.basketball.mapper.OperationLogMapper;
import com.basketball.service.IOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

    @Override
    public Page<OperationLog> getOperationLogPage(Page<OperationLog> page, Long userId, String username, String operation, Integer status) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(OperationLog::getUserId, userId);
        }
        if (username != null && !username.isEmpty()) {
            wrapper.like(OperationLog::getUsername, username);
        }
        if (operation != null && !operation.isEmpty()) {
            wrapper.like(OperationLog::getOperation, operation);
        }
        if (status != null) {
            wrapper.eq(OperationLog::getStatus, status);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void recordOperationLog(Long userId, String username, String operation, String method, String params, String result, Integer status, String ip, String location, String browser, String os, Long executeTime) {
        OperationLog operationLog = new OperationLog();
        operationLog.setUserId(userId);
        operationLog.setUsername(username);
        operationLog.setOperation(operation);
        operationLog.setMethod(method);
        operationLog.setParams(params);
        operationLog.setResult(result);
        operationLog.setStatus(status);
        operationLog.setIp(ip);
        operationLog.setLocation(location);
        operationLog.setBrowser(browser);
        operationLog.setOs(os);
        operationLog.setExecuteTime(executeTime);
        operationLog.setCreateTime(java.time.LocalDateTime.now());

        save(operationLog);
    }

    @Override
    public Long countSuccessfulOperations() {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getStatus, 1); // 成功
        return count(wrapper);
    }

    @Override
    public Long countFailedOperations() {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getStatus, 0); // 失败
        return count(wrapper);
    }
}