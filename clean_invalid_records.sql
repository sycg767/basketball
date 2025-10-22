-- 清理无效的历史消费记录
-- 这些记录没有余额信息，无法准确显示

-- 查看需要清理的记录
SELECT
    mcr.id,
    mcr.card_id,
    mct.card_name,
    mcr.record_type,
    mcr.change_amount,
    mcr.balance_after,
    mcr.description,
    mcr.create_time
FROM member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
WHERE mcr.record_type = 2  -- 消费记录
  AND mcr.balance_after IS NULL  -- 没有余额信息
  AND (mct.card_type = 1 OR mct.card_type = 3);  -- 时间卡或储值卡

-- 选项1：删除这些无效记录（推荐）
DELETE mcr FROM member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
WHERE mcr.record_type = 2
  AND mcr.balance_after IS NULL
  AND (mct.card_type = 1 OR mct.card_type = 3);

-- 选项2：或者为这些记录添加标记（保留历史）
-- UPDATE member_card_record mcr
-- INNER JOIN member_card mc ON mcr.card_id = mc.id
-- INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
-- SET mcr.description = CONCAT('[历史记录] ', mcr.description)
-- WHERE mcr.record_type = 2
--   AND mcr.balance_after IS NULL
--   AND (mct.card_type = 1 OR mct.card_type = 3);

-- 查看清理后的结果
SELECT
    mcr.id,
    mcr.card_id,
    mct.card_name,
    CASE mcr.record_type
        WHEN 1 THEN '充值'
        WHEN 2 THEN '消费'
    END as record_type_name,
    mcr.change_amount,
    mcr.balance_before,
    mcr.balance_after,
    mcr.description,
    mcr.create_time
FROM member_card_record mcr
INNER JOIN member_card mc ON mcr.card_id = mc.id
INNER JOIN member_card_type mct ON mc.card_type_id = mct.id
WHERE mcr.card_id = 3  -- 查看ID=3的卡（年卡）
ORDER BY mcr.create_time;
