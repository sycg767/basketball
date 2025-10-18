package com.basketball.service;

/**
 * 定时提醒服务接口
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
public interface IScheduledReminderService {

    /**
     * 检查会员卡到期提醒
     * 在会员卡到期前7天、3天、1天发送提醒
     */
    void checkMemberCardExpiration();

    /**
     * 检查课程开始提醒
     * 在课程开始前1小时发送提醒
     */
    void checkCourseStartReminder();

    /**
     * 检查预订开始提醒
     * 在预订时间前1小时发送提醒
     */
    void checkBookingStartReminder();

    /**
     * 执行待发送的定时提醒
     */
    void executePendingReminders();
}
