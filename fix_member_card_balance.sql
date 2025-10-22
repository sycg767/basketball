-- 修复会员卡使用记录的余额显示问题
-- 此脚本用于更新历史记录中缺失的 balance_after 字段
-- 新逻辑：所有卡类型（时间卡、次卡、储值卡）都有余额

-- 1. 为时间卡和储值卡的购买记录设置初始余额
UPDATE member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
SET
    mcr.balance_before = 0,
    mcr.balance_after = mcr.change_amount
WHERE
    mcr.record_type = 1  -- 充值/购买
    AND (mct.card_type = 1 OR mct.card_type = 3)  -- 时间卡或储值卡
    AND mcr.balance_after IS NULL;

-- 1.1 为时间卡和储值卡设置初始余额（如果为NULL）
UPDATE member_card mc
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
SET mc.balance = mct.price
WHERE (mct.card_type = 1 OR mct.card_type = 3)  -- 时间卡或储值卡
  AND (mc.balance IS NULL OR mc.balance = 0);

-- 1.2 为次卡设置余额为0
UPDATE member_card mc
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
SET mc.balance = 0
WHERE mct.card_type = 2  -- 次卡
  AND (mc.balance IS NULL OR mc.balance != 0);

-- 2. 为次卡的购买记录设置初始次数
UPDATE member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
SET
    mcr.times_before = 0,
    mcr.times_after = mct.times,
    mcr.change_times = mct.times
WHERE
    mcr.record_type = 1  -- 充值/购买
    AND mct.card_type = 2  -- 次卡
    AND mcr.times_after IS NULL;

-- 3. 查看需要修复的记录数量
SELECT
    COUNT(*) as total_records,
    SUM(CASE WHEN balance_after IS NULL THEN 1 ELSE 0 END) as missing_balance,
    SUM(CASE WHEN times_after IS NULL AND mct.card_type = 2 THEN 1 ELSE 0 END) as missing_times
FROM member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id;

-- 4. 查看会员卡余额情况
SELECT
    mc.id,
    mc.card_no,
    mct.card_name,
    mct.card_type,
    CASE mct.card_type
        WHEN 1 THEN '时间卡'
        WHEN 2 THEN '次卡'
        WHEN 3 THEN '储值卡'
    END as card_type_name,
    mc.balance,
    mc.remaining_times,
    mc.expire_date,
    mc.status
FROM member_card mc
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
ORDER BY mc.id;

-- 5. 查看修复后的使用记录
SELECT
    mcr.id,
    mcr.card_id,
    mct.card_name,
    CASE mct.card_type
        WHEN 1 THEN '时间卡'
        WHEN 2 THEN '次卡'
        WHEN 3 THEN '储值卡'
    END as card_type_name,
    CASE mcr.record_type
        WHEN 1 THEN '充值'
        WHEN 2 THEN '消费'
        WHEN 3 THEN '退款'
        WHEN 4 THEN '赠送'
    END as record_type_name,
    mcr.change_amount,
    mcr.balance_before,
    mcr.balance_after,
    mcr.change_times,
    mcr.times_before,
    mcr.times_after,
    mcr.description,
    mcr.create_time
FROM member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
ORDER BY mcr.card_id, mcr.create_time;
