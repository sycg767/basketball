package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.entity.UserBehaviorLog;
import com.basketball.mapper.UserBehaviorLogMapper;
import com.basketball.service.IBehaviorLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户行为日志服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@Service
public class BehaviorLogServiceImpl implements IBehaviorLogService {

    @Resource
    private UserBehaviorLogMapper behaviorLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordBehavior(Long userId, String behaviorType, String module, String businessId, String description) {
        try {
            UserBehaviorLog behaviorLog = new UserBehaviorLog();
            behaviorLog.setUserId(userId);
            behaviorLog.setBehaviorType(behaviorType);
            behaviorLog.setModule(module);
            behaviorLog.setBusinessId(businessId);
            behaviorLog.setDescription(description);
            behaviorLog.setBehaviorTime(LocalDateTime.now());

            // 尝试获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                behaviorLog.setIpAddress(getClientIp(request));
                behaviorLog.setUserAgent(request.getHeader("User-Agent"));
                behaviorLog.setDeviceType(detectDeviceType(request.getHeader("User-Agent")));
            }

            behaviorLogMapper.insert(behaviorLog);
            log.info("记录用户行为: userId={}, type={}, module={}, businessId={}",
                    userId, behaviorType, module, businessId);

        } catch (Exception e) {
            log.error("记录用户行为失败: userId={}, type={}", userId, behaviorType, e);
            // 不抛出异常,避免影响业务流程
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRecordBehavior(List<UserBehaviorLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return;
        }

        try {
            for (UserBehaviorLog behaviorLog : logs) {
                if (behaviorLog.getBehaviorTime() == null) {
                    behaviorLog.setBehaviorTime(LocalDateTime.now());
                }
                behaviorLogMapper.insert(behaviorLog);
            }
            log.info("批量记录用户行为成功: count={}", logs.size());
        } catch (Exception e) {
            log.error("批量记录用户行为失败", e);
        }
    }

    @Override
    public Map<String, Object> getUserBehaviorStats(Long userId, String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 解析日期范围
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();

            // 查询该时间范围内的行为日志
            LambdaQueryWrapper<UserBehaviorLog> query = new LambdaQueryWrapper<>();
            query.eq(UserBehaviorLog::getUserId, userId)
                    .between(UserBehaviorLog::getBehaviorTime, startDateTime, endDateTime);
            List<UserBehaviorLog> logs = behaviorLogMapper.selectList(query);

            // 统计各类行为次数
            Map<String, Integer> behaviorCount = new HashMap<>();
            Map<String, Integer> moduleCount = new HashMap<>();

            for (UserBehaviorLog behaviorLog : logs) {
                behaviorCount.put(behaviorLog.getBehaviorType(),
                        behaviorCount.getOrDefault(behaviorLog.getBehaviorType(), 0) + 1);
                moduleCount.put(behaviorLog.getModule(),
                        moduleCount.getOrDefault(behaviorLog.getModule(), 0) + 1);
            }

            stats.put("totalCount", logs.size());
            stats.put("behaviorCount", behaviorCount);
            stats.put("moduleCount", moduleCount);
            stats.put("loginCount", behaviorCount.getOrDefault("login", 0));
            stats.put("viewCount", behaviorCount.getOrDefault("view", 0));
            stats.put("bookingCount", behaviorCount.getOrDefault("booking", 0));
            stats.put("paymentCount", behaviorCount.getOrDefault("payment", 0));
            stats.put("cancelCount", behaviorCount.getOrDefault("cancel", 0));

            log.info("查询用户行为统计: userId={}, startDate={}, endDate={}, total={}",
                    userId, startDate, endDate, logs.size());

        } catch (Exception e) {
            log.error("查询用户行为统计失败: userId={}", userId, e);
        }

        return stats;
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个IP取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 检测设备类型
     */
    private String detectDeviceType(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }
        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("micromessenger")) {
            return "mini-program";
        } else if (userAgent.contains("android")) {
            return "android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "ios";
        } else {
            return "web";
        }
    }
}
