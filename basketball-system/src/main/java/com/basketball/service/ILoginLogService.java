package com.basketball.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.LoginLog;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ILoginLogService extends IService<LoginLog> {

    Page<LoginLog> getLoginLogPage(Page<LoginLog> page, Long userId, String username, Integer status);

    void recordLoginLog(Long userId, String username, Integer loginType, String ip, String location, String browser, String os, Integer status, String message);

    Long countSuccessfulLogins();

    Long countFailedLogins();
}