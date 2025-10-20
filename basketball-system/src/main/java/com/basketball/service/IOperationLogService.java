package com.basketball.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.OperationLog;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IOperationLogService extends IService<OperationLog> {

    Page<OperationLog> getOperationLogPage(Page<OperationLog> page, Long userId, String username, String operation, Integer status);

    void recordOperationLog(Long userId, String username, String operation, String method, String params, String result, Integer status, String ip, String location, String browser, String os, Long executeTime);

    Long countSuccessfulOperations();

    Long countFailedOperations();
}