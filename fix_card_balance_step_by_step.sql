-- 分步修复会员卡余额问题
-- 请按顺序执行以下步骤

-- ========================================
-- 步骤1: 查看当前会员卡状态
-- ========================================
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
    mc.balance as current_balance,
    mct.price as should_be_balance,
    mc.remaining_times,
    mc.status
FROM member_card mc
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
ORDER BY mc.id;

-- ========================================
-- 步骤2: 修复会员卡余额
-- ========================================

-- 2.1 为时间卡设置初始余额
UPDATE member_card mc
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
SET mc.balance = mct.price
WHERE mct.card_type = 1  -- 时间卡
  AND (mc.balance IS NULL OR mc.balance = 0);

-- 2.2 为储值卡设置初始余额
UPDATE member_card mc
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
SET mc.balance = mct.price
WHERE mct.card_type = 3  -- 储值卡
  AND (mc.balance IS NULL OR mc.balance = 0);

-- 2.3 为次卡设置余额为0
UPDATE member_card mc
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
SET mc.balance = 0
WHERE mct.card_type = 2;  -- 次卡

-- ========================================
-- 步骤3: 修复使用记录的余额字段
-- ========================================

-- 3.1 为时间卡和储值卡的购买记录设置余额
UPDATE member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
SET
    mcr.balance_before = 0,
    mcr.balance_after = mct.price
WHERE
    mcr.record_type = 1  -- 充值/购买
    AND (mct.card_type = 1 OR mct.card_type = 3)
    AND mcr.balance_after IS NULL;

-- 3.2 为次卡的购买记录设置次数
UPDATE member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
SET
    mcr.times_before = 0,
    mcr.times_after = mct.times,
    mcr.change_times = mct.times
WHERE
    mcr.record_type = 1
    AND mct.card_type = 2
    AND mcr.times_after IS NULL;

-- ========================================
-- 步骤4: 查看修复后的结果
-- ========================================

-- 4.1 查看会员卡余额
SELECT
    mc.id,
    mc.card_no,
    mct.card_name,
    mct.card_type,
    mc.balance,
    mc.remaining_times,
    mc.status
FROM member_card mc
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
ORDER BY mc.id;

-- 4.2 查看使用记录
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
    mcr.description,
    mcr.create_time
FROM member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
ORDER BY mcr.card_id, mcr.create_time;
