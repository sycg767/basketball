package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.LoginLog;
import com.basketball.mapper.LoginLogMapper;
import com.basketball.service.ILoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {

    @Override
    public Page<LoginLog> getLoginLogPage(Page<LoginLog> page, Long userId, String username, Integer status) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(LoginLog::getUserId, userId);
        }
        if (username != null && !username.isEmpty()) {
            wrapper.like(LoginLog::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(LoginLog::getStatus, status);
        }
        wrapper.orderByDesc(LoginLog::getLoginTime);
        return page(page, wrapper);
    }

    @Override
    public void recordLoginLog(Long userId, String username, Integer loginType, String ip, String location, String browser, String os, Integer status, String message) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(userId);
        loginLog.setUsername(username);
        loginLog.setLoginType(loginType);
        loginLog.setIp(ip);
        loginLog.setLocation(location);
        loginLog.setBrowser(browser);
        loginLog.setOs(os);
        loginLog.setStatus(status);
        loginLog.setMessage(message);
        loginLog.setLoginTime(java.time.LocalDateTime.now());

        save(loginLog);
    }

    @Override
    public Long countSuccessfulLogins() {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginLog::getStatus, 1); // 成功
        return count(wrapper);
    }

    @Override
    public Long countFailedLogins() {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginLog::getStatus, 0); // 失败
        return count(wrapper);
    }
}