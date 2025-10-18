package com.basketball.task;

import com.basketball.service.IScheduledReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时提醒任务
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Component
public class ReminderScheduledTask {

    @Resource
    private IScheduledReminderService scheduledReminderService;

    /**
     * 检查会员卡到期提醒
     * 每天早上8点执行
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkMemberCardExpiration() {
        log.info("开始执行会员卡到期提醒检查任务");
        try {
            scheduledReminderService.checkMemberCardExpiration();
        } catch (Exception e) {
            log.error("会员卡到期提醒检查任务失败", e);
        }
    }

    /**
     * 检查课程开始提醒
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkCourseStartReminder() {
        log.info("开始执行课程开始提醒检查任务");
        try {
            scheduledReminderService.checkCourseStartReminder();
        } catch (Exception e) {
            log.error("课程开始提醒检查任务失败", e);
        }
    }

    /**
     * 检查预订开始提醒
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkBookingStartReminder() {
        log.info("开始执行预订开始提醒检查任务");
        try {
            scheduledReminderService.checkBookingStartReminder();
        } catch (Exception e) {
            log.error("预订开始提醒检查任务失败", e);
        }
    }

    /**
     * 执行待发送的定时提醒
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void executePendingReminders() {
        log.info("开始执行待发送的定时提醒任务");
        try {
            scheduledReminderService.executePendingReminders();
        } catch (Exception e) {
            log.error("定时提醒执行任务失败", e);
        }
    }
}
