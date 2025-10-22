-- 添加课程相关通知模板
-- 用于修复课程报名时通知模板不存在的问题

-- USE basketball_system;  -- 如果需要切换数据库,请取消注释并修改为正确的数据库名

-- 1. 课程报名成功通知
INSERT INTO `notification_template`
(`template_code`, `template_name`, `template_type`, `scene`, `title`, `content`, `variables`, `priority`, `status`, `remark`)
VALUES
('COURSE_ENROLL_SUCCESS', '课程报名成功', 'system', 'course_enroll', '课程报名成功',
'恭喜您成功报名【{{courseName}}】!报名编号:{{enrollmentNo}},上课时间:{{scheduleDate}},请准时参加。',
'["courseName","enrollmentNo","scheduleDate"]', 2, 1, '课程报名成功后发送的通知');

-- 2. 课程报名取消通知
INSERT INTO `notification_template`
(`template_code`, `template_name`, `template_type`, `scene`, `title`, `content`, `variables`, `priority`, `status`, `remark`)
VALUES
('COURSE_ENROLL_CANCEL', '课程报名取消', 'system', 'course_enroll', '课程报名已取消',
'您已取消【{{courseName}}】的报名,报名编号:{{enrollmentNo}}。如有疑问请联系客服。',
'["courseName","enrollmentNo"]', 2, 1, '课程报名取消后发送的通知');

-- 3. 课程支付成功通知
INSERT INTO `notification_template`
(`template_code`, `template_name`, `template_type`, `scene`, `title`, `content`, `variables`, `priority`, `status`, `remark`)
VALUES
('COURSE_PAYMENT_SUCCESS', '课程支付成功', 'system', 'course_payment', '支付成功',
'您已成功支付【{{courseName}}】课程费用¥{{amount}},报名编号:{{enrollmentNo}},期待您的到来!',
'["courseName","amount","enrollmentNo"]', 2, 1, '课程支付成功后发送的通知');

-- 4. 课程即将开始提醒(提前1天)
INSERT INTO `notification_template`
(`template_code`, `template_name`, `template_type`, `scene`, `title`, `content`, `variables`, `priority`, `status`, `remark`)
VALUES
('COURSE_START_REMIND_1DAY', '课程开始提醒(1天)', 'system', 'course_remind', '课程即将开始',
'温馨提醒:您报名的【{{courseName}}】将于明天{{startTime}}开始,地点:{{venueName}},请准时参加!',
'["courseName","startTime","venueName"]', 3, 1, '课程开始前1天发送的提醒');

-- 5. 课程即将开始提醒(提前2小时)
INSERT INTO `notification_template`
(`template_code`, `template_name`, `template_type`, `scene`, `title`, `content`, `variables`, `priority`, `status`, `remark`)
VALUES
('COURSE_START_REMIND_2HOURS', '课程开始提醒(2小时)', 'system', 'course_remind', '课程即将开始',
'【重要提醒】您报名的【{{courseName}}】将于2小时后({{startTime}})开始,地点:{{venueName}},请尽快前往!',
'["courseName","startTime","venueName"]', 3, 1, '课程开始前2小时发送的提醒');

-- 6. 课程评价提醒
INSERT INTO `notification_template`
(`template_code`, `template_name`, `template_type`, `scene`, `title`, `content`, `variables`, `priority`, `status`, `remark`)
VALUES
('COURSE_RATE_REMIND', '课程评价提醒', 'system', 'course_rate', '课程评价邀请',
'感谢您参加【{{courseName}}】!您的评价对我们很重要,快来分享您的学习体验吧~',
'["courseName"]', 1, 1, '课程结束后发送的评价提醒');

-- 7. 课程排期变更通知
INSERT INTO `notification_template`
(`template_code`, `template_name`, `template_type`, `scene`, `title`, `content`, `variables`, `priority`, `status`, `remark`)
VALUES
('COURSE_SCHEDULE_CHANGE', '课程排期变更', 'system', 'course_schedule', '课程时间变更通知',
'【重要通知】您报名的【{{courseName}}】时间已变更,原时间:{{oldTime}},新时间:{{newTime}},地点不变。如有疑问请联系客服。',
'["courseName","oldTime","newTime"]', 3, 1, '课程排期变更时发送的通知');

-- 8. 课程满员通知
INSERT INTO `notification_template`
(`template_code`, `template_name`, `template_type`, `scene`, `title`, `content`, `variables`, `priority`, `status`, `remark`)
VALUES
('COURSE_FULL_NOTIFY', '课程满员通知', 'system', 'course_full', '课程已满员',
'抱歉,【{{courseName}}】({{scheduleDate}})已满员。您可以选择其他时间段或关注后续排期。',
'["courseName","scheduleDate"]', 2, 1, '课程满员时发送的通知');

-- 查询验证
SELECT id, template_code, template_name, template_type, scene, status
FROM notification_template
WHERE template_code LIKE 'COURSE%'
ORDER BY id;
