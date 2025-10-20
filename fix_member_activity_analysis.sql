-- 修复 member_activity_analysis 表结构
-- 添加缺失的字段

USE basketball_system;

-- 添加 cancel_count 字段
ALTER TABLE `member_activity_analysis`
ADD COLUMN `cancel_count` int NULL DEFAULT 0 COMMENT '取消次数' AFTER `payment_amount`;

-- 添加 last_active_time 字段
ALTER TABLE `member_activity_analysis`
ADD COLUMN `last_active_time` datetime NULL DEFAULT NULL COMMENT '最后活跃时间' AFTER `activity_score`;

-- 添加 remark 字段
ALTER TABLE `member_activity_analysis`
ADD COLUMN `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注' AFTER `churn_risk`;

-- 验证表结构
DESC member_activity_analysis;
