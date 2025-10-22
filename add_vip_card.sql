-- 添加VIP会员卡并修正会员等级
-- 会员等级统一标准：0-普通用户, 1-银卡, 2-金卡, 3-铂金, 4-VIP

-- 添加VIP至尊年卡
INSERT INTO `member_card_type`
(`card_name`, `card_code`, `card_type`, `duration`, `times`, `price`, `original_price`, `discount`, `member_level`, `benefits`, `description`, `sort_order`, `status`)
VALUES
('VIP至尊年卡', 'VIP_YEAR_CARD', 1, 365, NULL, 4999.00, 7188.00, 0.75, 4,
'["场地预订8折优惠","课程报名8折优惠","每月赠送2000积分","最高优先预订权","生日专属福利","会员专属活动","免费私教课程2次","专属客服"]',
'有效期365天，VIP尊享服务', 0, 1);

-- 更新现有会员卡的会员等级说明（如果需要调整）
-- 月卡会员 -> 银卡会员 (member_level = 1) ✓ 已正确
-- 季卡会员 -> 金卡会员 (member_level = 2) ✓ 已正确
-- 年卡会员 -> 铂金会员 (member_level = 3) ✓ 已正确
-- VIP至尊年卡 -> VIP会员 (member_level = 4) ✓ 新增

-- 更新会员卡的折扣说明，使其与会员等级一致
UPDATE `member_card_type` SET `discount` = 0.95 WHERE `member_level` = 1 AND `card_type` = 1;
UPDATE `member_card_type` SET `discount` = 0.90 WHERE `member_level` = 2 AND `card_type` = 1;
UPDATE `member_card_type` SET `discount` = 0.85 WHERE `member_level` = 3 AND `card_type` = 1;
UPDATE `member_card_type` SET `discount` = 0.80 WHERE `member_level` = 4 AND `card_type` = 1;

-- 更新会员卡的权益说明
UPDATE `member_card_type` SET
  `benefits` = '["场地预订95折优惠","课程报名95折优惠","每月赠送200积分","优先预订权"]',
  `description` = '有效期30天，银卡会员专属优惠'
WHERE `card_code` = 'MONTH_CARD';

UPDATE `member_card_type` SET
  `benefits` = '["场地预订9折优惠","课程报名9折优惠","每月赠送500积分","优先预订权","生日专属福利"]',
  `description` = '有效期90天，金卡会员性价比之选'
WHERE `card_code` = 'SEASON_CARD';

UPDATE `member_card_type` SET
  `benefits` = '["场地预订85折优惠","课程报名85折优惠","每月赠送1000积分","最高优先预订权","生日专属福利","会员专属活动"]',
  `description` = '有效期365天，铂金会员尊享全年优惠'
WHERE `card_code` = 'YEAR_CARD';
