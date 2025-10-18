package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.dto.request.NotificationSendDTO;
import com.basketball.entity.*;
import com.basketball.mapper.*;
import com.basketball.service.INotificationService;
import com.basketball.service.IScheduledReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时提醒服务实现
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Service
public class ScheduledReminderServiceImpl implements IScheduledReminderService {

    @Resource
    private ScheduledReminderMapper scheduledReminderMapper;

    @Resource
    private INotificationService notificationService;

    @Resource
    private MemberCardMapper memberCardMapper;

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private CourseEnrollmentMapper courseEnrollmentMapper;

    @Resource
    private BookingMapper bookingMapper;

    /**
     * 检查会员卡到期提醒
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkMemberCardExpiration() {
        log.info("检查会员卡到期提醒...");

        LocalDateTime now = LocalDateTime.now();

        // 检查7天、3天、1天后到期的会员卡
        checkExpiringCards(now, 7, "member_expire_7", "您的会员卡将在7天后到期，请及时续费");
        checkExpiringCards(now, 3, "member_expire_3", "您的会员卡将在3天后到期，请尽快续费");
        checkExpiringCards(now, 1, "member_expire_1", "您的会员卡将在明天到期，请立即续费");

        log.info("会员卡到期提醒检查完成");
    }

    /**
     * 检查即将到期的会员卡并创建提醒
     */
    private void checkExpiringCards(LocalDateTime now, int days, String reminderType, String content) {
        java.time.LocalDate targetDate = now.toLocalDate().plusDays(days);

        // 查询即将到期的会员卡
        LambdaQueryWrapper<MemberCard> query = new LambdaQueryWrapper<>();
        query.eq(MemberCard::getExpireDate, targetDate)
                .eq(MemberCard::getStatus, 1); // 状态为正常

        List<MemberCard> cards = memberCardMapper.selectList(query);
        log.info("查询到{}张{}天后到期的会员卡", cards.size(), days);

        for (MemberCard card : cards) {
            try {
                // 检查是否已创建该类型提醒
                Long count = scheduledReminderMapper.selectCount(
                        new LambdaQueryWrapper<ScheduledReminder>()
                                .eq(ScheduledReminder::getBizId, String.valueOf(card.getId()))
                                .eq(ScheduledReminder::getTaskType, reminderType)
                );

                if (count == 0) {
                    // 创建定时提醒记录
                    ScheduledReminder reminder = new ScheduledReminder();
                    reminder.setTargetUserId(card.getUserId());
                    reminder.setTaskType(reminderType);
                    reminder.setBizId(String.valueOf(card.getId()));
                    reminder.setBizType("member_card");
                    reminder.setRemindTime(now); // 立即提醒
                    reminder.setContent(content);
                    reminder.setTitle("会员卡到期提醒");
                    reminder.setStatus(0); // 待执行
                    reminder.setRetryCount(0);
                    reminder.setCreateTime(now);

                    scheduledReminderMapper.insert(reminder);
                    log.info("创建会员卡到期提醒: cardId={}, userId={}, type={}",
                            card.getId(), card.getUserId(), reminderType);
                }
            } catch (Exception e) {
                log.error("创建会员卡到期提醒失败: cardId={}", card.getId(), e);
            }
        }
    }

    /**
     * 检查课程开始提醒
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkCourseStartReminder() {
        log.info("检查课程开始提醒...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        // 查询1小时后开始的课程排期
        LambdaQueryWrapper<CourseSchedule> query = new LambdaQueryWrapper<>();
        query.between(CourseSchedule::getStartTime, now, oneHourLater)
                .eq(CourseSchedule::getStatus, 1); // 正常状态

        List<CourseSchedule> schedules = courseScheduleMapper.selectList(query);
        log.info("查询到{}个即将开始的课程排期", schedules.size());

        for (CourseSchedule schedule : schedules) {
            try {
                // 查询报名该课程的学员
                LambdaQueryWrapper<CourseEnrollment> enrollQuery = new LambdaQueryWrapper<>();
                enrollQuery.eq(CourseEnrollment::getCourseId, schedule.getCourseId())
                        .in(CourseEnrollment::getStatus, 0, 1); // 待确认或已确认

                List<CourseEnrollment> enrollments = courseEnrollmentMapper.selectList(enrollQuery);
                log.info("课程{}有{}个学员报名", schedule.getCourseId(), enrollments.size());

                // 为每个学员创建提醒
                for (CourseEnrollment enrollment : enrollments) {
                    // 检查是否已创建提醒
                    Long count = scheduledReminderMapper.selectCount(
                            new LambdaQueryWrapper<ScheduledReminder>()
                                    .eq(ScheduledReminder::getTargetUserId, enrollment.getUserId())
                                    .eq(ScheduledReminder::getBizId, String.valueOf(schedule.getId()))
                                    .eq(ScheduledReminder::getTaskType, "course_start")
                    );

                    if (count == 0) {
                        // 创建定时提醒记录
                        ScheduledReminder reminder = new ScheduledReminder();
                        reminder.setTargetUserId(enrollment.getUserId());
                        reminder.setTaskType("course_start");
                        reminder.setBizId(String.valueOf(schedule.getId()));
                        reminder.setBizType("course");
                        reminder.setRemindTime(now); // 立即提醒
                        reminder.setContent("您报名的课程将在1小时后开始，请准时参加");
                        reminder.setTitle("课程开始提醒");
                        reminder.setStatus(0);
                        reminder.setRetryCount(0);
                        reminder.setCreateTime(now);

                        scheduledReminderMapper.insert(reminder);
                        log.info("创建课程开始提醒: scheduleId={}, userId={}", schedule.getId(), enrollment.getUserId());
                    }
                }
            } catch (Exception e) {
                log.error("创建课程开始提醒失败: scheduleId={}", schedule.getId(), e);
            }
        }

        log.info("课程开始提醒检查完成");
    }

    /**
     * 检查预订开始提醒
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkBookingStartReminder() {
        log.info("检查预订开始提醒...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        // 查询1小时后开始的预订
        LambdaQueryWrapper<Booking> query = new LambdaQueryWrapper<>();
        query.between(Booking::getStartTime, now, oneHourLater)
                .in(Booking::getStatus, 1, 2); // 已确认或已支付

        List<Booking> bookings = bookingMapper.selectList(query);
        log.info("查询到{}个即将开始的预订", bookings.size());

        for (Booking booking : bookings) {
            try {
                // 检查是否已创建提醒
                Long count = scheduledReminderMapper.selectCount(
                        new LambdaQueryWrapper<ScheduledReminder>()
                                .eq(ScheduledReminder::getBizId, String.valueOf(booking.getId()))
                                .eq(ScheduledReminder::getTaskType, "booking_start")
                );

                if (count == 0) {
                    // 创建定时提醒记录
                    ScheduledReminder reminder = new ScheduledReminder();
                    reminder.setTargetUserId(booking.getUserId());
                    reminder.setTaskType("booking_start");
                    reminder.setBizId(String.valueOf(booking.getId()));
                    reminder.setBizType("booking");
                    reminder.setRemindTime(now); // 立即提醒
                    reminder.setContent("您的场地预订将在1小时后开始，请准时到场");
                    reminder.setTitle("预订开始提醒");
                    reminder.setStatus(0);
                    reminder.setRetryCount(0);
                    reminder.setCreateTime(now);

                    scheduledReminderMapper.insert(reminder);
                    log.info("创建预订开始提醒: bookingId={}, userId={}", booking.getId(), booking.getUserId());
                }
            } catch (Exception e) {
                log.error("创建预订开始提醒失败: bookingId={}", booking.getId(), e);
            }
        }

        log.info("预订开始提醒检查完成");
    }

    /**
     * 执行待发送的定时提醒
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executePendingReminders() {
        log.info("执行待发送的定时提醒...");

        // 查询待执行的提醒 (提醒时间已到且未执行)
        LambdaQueryWrapper<ScheduledReminder> query = new LambdaQueryWrapper<>();
        query.eq(ScheduledReminder::getStatus, 0)
                .le(ScheduledReminder::getRemindTime, LocalDateTime.now())
                .orderByAsc(ScheduledReminder::getRemindTime);

        List<ScheduledReminder> reminders = scheduledReminderMapper.selectList(query);

        log.info("找到{}条待执行的提醒", reminders.size());

        for (ScheduledReminder reminder : reminders) {
            try {
                // 更新状态为执行中(状态1已在实体修改为"已执行")
                reminder.setStatus(1);
                scheduledReminderMapper.updateById(reminder);

                // 发送通知
                NotificationSendDTO sendDTO = new NotificationSendDTO();
                sendDTO.setUserId(reminder.getTargetUserId());
                sendDTO.setTemplateCode(getReminderTemplateCode(reminder.getTaskType()));
                sendDTO.setNotificationType("system");
                sendDTO.setBizId(reminder.getBizId());
                sendDTO.setBizType(reminder.getBizType());
                sendDTO.setCustomContent(reminder.getContent());

                // 构建模板参数
                Map<String, Object> params = new HashMap<>();
                params.put("content", reminder.getContent());
                sendDTO.setParams(params);

                notificationService.sendNotification(sendDTO);

                // 更新状态为已执行
                reminder.setStatus(1);
                reminder.setExecuteTime(LocalDateTime.now());
                reminder.setResult("执行成功");
                scheduledReminderMapper.updateById(reminder);

                log.info("定时提醒执行成功: reminderId={}, userId={}", reminder.getId(), reminder.getTargetUserId());

            } catch (Exception e) {
                log.error("定时提醒执行失败: reminderId={}", reminder.getId(), e);

                // 更新状态为执行失败(状态3)
                reminder.setStatus(3);
                reminder.setResult("执行失败: " + e.getMessage());
                reminder.setRetryCount(reminder.getRetryCount() + 1);
                scheduledReminderMapper.updateById(reminder);
            }
        }

        log.info("定时提醒执行完成");
    }

    /**
     * 根据提醒类型获取模板编码
     */
    private String getReminderTemplateCode(String reminderType) {
        switch (reminderType) {
            case "member_expire_7":
            case "member_expire_3":
            case "member_expire_1":
                return "MEMBER_EXPIRE_REMINDER";
            case "course_start":
                return "COURSE_START_REMINDER";
            case "booking_start":
                return "BOOKING_START_REMINDER";
            default:
                return "SYSTEM_NOTIFICATION";
        }
    }
}
