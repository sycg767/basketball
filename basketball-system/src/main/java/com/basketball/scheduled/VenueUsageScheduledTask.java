package com.basketball.scheduled;

import com.basketball.service.IVenueUsageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * 场地使用分析定时任务
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@Component
public class VenueUsageScheduledTask {

    @Resource
    private IVenueUsageService venueUsageService;

    /**
     * 每5分钟执行场地使用分析（测试用）
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void analyzeDailyVenueUsage() {
        try {
            log.info("开始执行场地使用分析定时任务");

            // 分析昨天的数据
            LocalDate yesterday = LocalDate.now().minusDays(1);
            venueUsageService.analyzeDailyVenueUsage(yesterday);

            log.info("场地使用分析定时任务执行完成");
        } catch (Exception e) {
            log.error("场地使用分析定时任务执行失败", e);
        }
    }
}
