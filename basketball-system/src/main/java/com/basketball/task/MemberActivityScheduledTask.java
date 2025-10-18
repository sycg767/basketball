package com.basketball.task;

import com.basketball.service.IMemberActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * 会员活跃度统计定时任务
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@Component
public class MemberActivityScheduledTask {

    @Resource
    private IMemberActivityService memberActivityService;

    /**
     * 每天凌晨1:30执行
     * 统计前一天的会员活跃度
     */
    @Scheduled(cron = "0 30 1 * * ?")
    public void analyzeDailyActivity() {
        log.info("开始执行会员活跃度统计定时任务...");

        try {
            // 统计前一天的数据
            LocalDate yesterday = LocalDate.now().minusDays(1);
            memberActivityService.analyzeDailyActivity(yesterday);

            log.info("会员活跃度统计定时任务执行成功");
        } catch (Exception e) {
            log.error("会员活跃度统计定时任务执行失败", e);
        }
    }
}
