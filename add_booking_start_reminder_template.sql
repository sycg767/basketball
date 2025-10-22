-- ========================================
-- 添加预订开始提醒模板
-- 用于修复定时任务中缺失的通知模板
-- ========================================

-- USE basketball_system;  -- 如果需要切换数据库,请取消注释并修改为正确的数据库名

-- 添加预订开始提醒模板
INSERT INTO `notification_template`
(`template_code`, `template_name`, `template_type`, `scene`, `title`, `content`, `variables`, `priority`, `status`, `remark`)
VALUES
('BOOKING_START_REMINDER', '预订开始提醒', 'system', 'booking_remind', '预订即将开始',
'您的场地预订将在1小时后开始，请准时到场', '[]', 3, 1, '预订开始提醒');

-- 查询验证
SELECT id, template_code, template_name, template_type, scene, status
FROM notification_template
WHERE template_code = 'BOOKING_START_REMINDER';
