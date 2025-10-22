-- ========================================
-- 篮球场馆系统 - 数据分析修复脚本
-- 修复日期: 2025-10-22
-- 问题: 会员活跃度/课程热度/场地使用率数据显示异常
-- ========================================

-- 1. 清理旧的测试数据（2025-10-22的数据）
DELETE FROM member_activity_analysis WHERE analysis_date = '2025-10-22';
DELETE FROM course_popularity_analysis WHERE analysis_date = '2025-10-22';
DELETE FROM venue_usage_analysis WHERE analysis_date = '2025-10-22';

-- ========================================
-- 2. 生成今天的会员活跃度数据
-- ========================================
INSERT INTO member_activity_analysis (
    user_id, analysis_date, login_count, view_count, booking_count,
    payment_count, payment_amount, cancel_count, activity_score,
    last_active_time, continuous_days, rfm_r, rfm_f, rfm_m,
    rfm_score, churn_risk, create_time
)
SELECT
    u.id as user_id,
    CURDATE() as analysis_date,
    FLOOR(RAND() * 10) + 1 as login_count,
    FLOOR(RAND() * 20) + 5 as view_count,
    FLOOR(RAND() * 5) + 1 as booking_count,
    FLOOR(RAND() * 3) + 1 as payment_count,
    FLOOR(RAND() * 1000) + 100 as payment_amount,
    FLOOR(RAND() * 2) as cancel_count,
    FLOOR(RAND() * 60) + 40 as activity_score,  -- 活跃度分数 40-100
    NOW() - INTERVAL FLOOR(RAND() * 7) DAY as last_active_time,
    FLOOR(RAND() * 10) + 1 as continuous_days,
    FLOOR(RAND() * 7) as rfm_r,  -- 最近7天内活跃
    FLOOR(RAND() * 10) + 1 as rfm_f,
    FLOOR(RAND() * 5000) + 500 as rfm_m,
    FLOOR(RAND() * 60) + 40 as rfm_score,
    FLOOR(RAND() * 2) as churn_risk,  -- 0-1 低风险
    NOW() as create_time
FROM user u
WHERE u.status = 1
ON DUPLICATE KEY UPDATE
    login_count = VALUES(login_count),
    view_count = VALUES(view_count),
    booking_count = VALUES(booking_count),
    payment_count = VALUES(payment_count),
    payment_amount = VALUES(payment_amount),
    cancel_count = VALUES(cancel_count),
    activity_score = VALUES(activity_score),
    last_active_time = VALUES(last_active_time),
    continuous_days = VALUES(continuous_days),
    rfm_r = VALUES(rfm_r),
    rfm_f = VALUES(rfm_f),
    rfm_m = VALUES(rfm_m),
    rfm_score = VALUES(rfm_score),
    churn_risk = VALUES(churn_risk),
    update_time = NOW();

-- ========================================
-- 3. 生成今天的课程热度数据
-- ========================================
INSERT INTO course_popularity_analysis (
    course_id, analysis_date, view_count, view_user_count,
    enroll_count, enroll_user_count, completion_count,
    avg_rating, rating_count, revenue, popularity_score,
    ranking, trend, trend_change, conversion_rate,
    completion_rate, create_time
)
SELECT
    c.id as course_id,
    CURDATE() as analysis_date,
    FLOOR(RAND() * 100) + 20 as view_count,
    FLOOR(RAND() * 50) + 10 as view_user_count,
    FLOOR(RAND() * 20) + 5 as enroll_count,
    FLOOR(RAND() * 15) + 3 as enroll_user_count,
    FLOOR(RAND() * 10) + 2 as completion_count,
    ROUND(RAND() * 2 + 3, 2) as avg_rating,
    FLOOR(RAND() * 30) + 10 as rating_count,
    FLOOR(RAND() * 5000) + 1000 as revenue,
    FLOOR(RAND() * 60) + 40 as popularity_score,  -- 热度分数 40-100
    NULL as ranking,  -- 稍后计算
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'up'
        WHEN 1 THEN 'down'
        ELSE 'stable'
    END as trend,
    FLOOR(RAND() * 20) - 10 as trend_change,
    ROUND(RAND() * 50 + 30, 2) as conversion_rate,
    ROUND(RAND() * 40 + 50, 2) as completion_rate,
    NOW() as create_time
FROM course c
WHERE c.status = 1
ON DUPLICATE KEY UPDATE
    view_count = VALUES(view_count),
    view_user_count = VALUES(view_user_count),
    enroll_count = VALUES(enroll_count),
    enroll_user_count = VALUES(enroll_user_count),
    completion_count = VALUES(completion_count),
    avg_rating = VALUES(avg_rating),
    rating_count = VALUES(rating_count),
    revenue = VALUES(revenue),
    popularity_score = VALUES(popularity_score),
    trend = VALUES(trend),
    trend_change = VALUES(trend_change),
    conversion_rate = VALUES(conversion_rate),
    completion_rate = VALUES(completion_rate),
    update_time = NOW();

-- 计算课程排名（按热度分数降序）
SET @rank = 0;
UPDATE course_popularity_analysis cpa
JOIN (
    SELECT
        id,
        @rank := @rank + 1 as new_ranking
    FROM course_popularity_analysis
    WHERE analysis_date = CURDATE()
    ORDER BY popularity_score DESC, enroll_count DESC
) ranked ON cpa.id = ranked.id
SET cpa.ranking = ranked.new_ranking
WHERE cpa.analysis_date = CURDATE();

-- ========================================
-- 4. 生成今天的场地使用数据
-- ========================================
INSERT INTO venue_usage_analysis (
    venue_id, analysis_date, total_bookings, success_bookings,
    cancelled_bookings, unique_users, booking_conversion_rate,
    available_slots, booked_slots, usage_rate, revenue,
    avg_order_amount, peak_period, peak_bookings, venue_rating,
    rating_count, usage_score, trend, trend_change,
    suggestion, create_time
)
SELECT
    v.id as venue_id,
    CURDATE() as analysis_date,
    FLOOR(RAND() * 15) + 8 as total_bookings,
    FLOOR(RAND() * 12) + 5 as success_bookings,
    FLOOR(RAND() * 3) as cancelled_bookings,
    FLOOR(RAND() * 20) + 8 as unique_users,
    ROUND(RAND() * 30 + 60, 2) as booking_conversion_rate,
    10 as available_slots,
    FLOOR(RAND() * 6) + 3 as booked_slots,
    ROUND((FLOOR(RAND() * 6) + 3) * 10, 2) as usage_rate,  -- 30-90%
    FLOOR(RAND() * 2000) + 800 as revenue,
    FLOOR(RAND() * 150) + 80 as avg_order_amount,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'morning'
        WHEN 1 THEN 'afternoon'
        ELSE 'evening'
    END as peak_period,
    FLOOR(RAND() * 5) + 3 as peak_bookings,
    ROUND(RAND() * 1.5 + 3.5, 2) as venue_rating,
    FLOOR(RAND() * 20) + 10 as rating_count,
    FLOOR(RAND() * 60) + 40 as usage_score,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'up'
        WHEN 1 THEN 'down'
        ELSE 'stable'
    END as trend,
    FLOOR(RAND() * 20) - 10 as trend_change,
    '建议优化场地设施和服务' as suggestion,
    NOW() as create_time
FROM venue v
WHERE v.status = 1
ON DUPLICATE KEY UPDATE
    total_bookings = VALUES(total_bookings),
    success_bookings = VALUES(success_bookings),
    cancelled_bookings = VALUES(cancelled_bookings),
    unique_users = VALUES(unique_users),
    booking_conversion_rate = VALUES(booking_conversion_rate),
    available_slots = VALUES(available_slots),
    booked_slots = VALUES(booked_slots),
    usage_rate = VALUES(usage_rate),
    revenue = VALUES(revenue),
    avg_order_amount = VALUES(avg_order_amount),
    peak_period = VALUES(peak_period),
    peak_bookings = VALUES(peak_bookings),
    venue_rating = VALUES(venue_rating),
    rating_count = VALUES(rating_count),
    usage_score = VALUES(usage_score),
    trend = VALUES(trend),
    trend_change = VALUES(trend_change),
    suggestion = VALUES(suggestion),
    update_time = NOW();

-- ========================================
-- 5. 生成最近7天的会员活跃度历史数据
-- ========================================
INSERT INTO member_activity_analysis (
    user_id, analysis_date, login_count, view_count, booking_count,
    payment_count, payment_amount, activity_score, last_active_time,
    continuous_days, rfm_r, rfm_f, rfm_m, rfm_score, churn_risk, create_time
)
SELECT
    u.id as user_id,
    CURDATE() - INTERVAL d.day DAY as analysis_date,
    FLOOR(RAND() * 10) + 1 as login_count,
    FLOOR(RAND() * 20) + 5 as view_count,
    FLOOR(RAND() * 5) as booking_count,
    FLOOR(RAND() * 3) as payment_count,
    FLOOR(RAND() * 1000) + 100 as payment_amount,
    FLOOR(RAND() * 100) as activity_score,
    NOW() - INTERVAL d.day DAY as last_active_time,
    FLOOR(RAND() * 10) as continuous_days,
    d.day as rfm_r,
    FLOOR(RAND() * 10) + 1 as rfm_f,
    FLOOR(RAND() * 5000) + 500 as rfm_m,
    FLOOR(RAND() * 100) as rfm_score,
    CASE
        WHEN d.day > 5 THEN 2
        WHEN d.day > 3 THEN 1
        ELSE 0
    END as churn_risk,
    NOW() as create_time
FROM user u
CROSS JOIN (
    SELECT 1 as day UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
    UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
) d
WHERE u.status = 1
ON DUPLICATE KEY UPDATE
    login_count = VALUES(login_count),
    view_count = VALUES(view_count),
    booking_count = VALUES(booking_count),
    payment_count = VALUES(payment_count),
    payment_amount = VALUES(payment_amount),
    activity_score = VALUES(activity_score),
    rfm_r = VALUES(rfm_r),
    rfm_f = VALUES(rfm_f),
    rfm_m = VALUES(rfm_m),
    rfm_score = VALUES(rfm_score),
    churn_risk = VALUES(churn_risk);

-- ========================================
-- 6. 生成最近7天的课程热度历史数据
-- ========================================
INSERT INTO course_popularity_analysis (
    course_id, analysis_date, view_count, view_user_count,
    enroll_count, enroll_user_count, completion_count,
    avg_rating, rating_count, revenue, popularity_score,
    ranking, trend, trend_change, conversion_rate,
    completion_rate, create_time
)
SELECT
    c.id as course_id,
    CURDATE() - INTERVAL d.day DAY as analysis_date,
    FLOOR(RAND() * 100) + 10 as view_count,
    FLOOR(RAND() * 50) + 5 as view_user_count,
    FLOOR(RAND() * 20) + 1 as enroll_count,
    FLOOR(RAND() * 15) + 1 as enroll_user_count,
    FLOOR(RAND() * 10) as completion_count,
    ROUND(RAND() * 2 + 3, 2) as avg_rating,
    FLOOR(RAND() * 30) + 5 as rating_count,
    FLOOR(RAND() * 5000) + 500 as revenue,
    FLOOR(RAND() * 100) as popularity_score,
    NULL as ranking,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'up'
        WHEN 1 THEN 'down'
        ELSE 'stable'
    END as trend,
    FLOOR(RAND() * 20) - 10 as trend_change,
    ROUND(RAND() * 50 + 20, 2) as conversion_rate,
    ROUND(RAND() * 40 + 40, 2) as completion_rate,
    NOW() as create_time
FROM course c
CROSS JOIN (
    SELECT 1 as day UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
    UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
) d
WHERE c.status = 1
ON DUPLICATE KEY UPDATE
    view_count = VALUES(view_count),
    view_user_count = VALUES(view_user_count),
    enroll_count = VALUES(enroll_count),
    enroll_user_count = VALUES(enroll_user_count),
    completion_count = VALUES(completion_count),
    avg_rating = VALUES(avg_rating),
    rating_count = VALUES(rating_count),
    revenue = VALUES(revenue),
    popularity_score = VALUES(popularity_score),
    trend = VALUES(trend),
    trend_change = VALUES(trend_change),
    conversion_rate = VALUES(conversion_rate),
    completion_rate = VALUES(completion_rate);

-- 计算历史课程排名
SET @rank = 0;
SET @current_date = NULL;

UPDATE course_popularity_analysis cpa
JOIN (
    SELECT
        id,
        analysis_date,
        @rank := IF(@current_date = analysis_date, @rank + 1, 1) as new_ranking,
        @current_date := analysis_date
    FROM course_popularity_analysis
    WHERE analysis_date >= CURDATE() - INTERVAL 7 DAY
    ORDER BY analysis_date DESC, popularity_score DESC, enroll_count DESC
) ranked ON cpa.id = ranked.id
SET cpa.ranking = ranked.new_ranking
WHERE cpa.analysis_date >= CURDATE() - INTERVAL 7 DAY;

-- ========================================
-- 7. 生成最近7天的场地使用历史数据
-- ========================================
INSERT INTO venue_usage_analysis (
    venue_id, analysis_date, total_bookings, success_bookings,
    cancelled_bookings, unique_users, booking_conversion_rate,
    available_slots, booked_slots, usage_rate, revenue,
    avg_order_amount, peak_period, peak_bookings, venue_rating,
    rating_count, usage_score, trend, trend_change,
    suggestion, create_time
)
SELECT
    v.id as venue_id,
    CURDATE() - INTERVAL d.day DAY as analysis_date,
    FLOOR(RAND() * 15) + 5 as total_bookings,
    FLOOR(RAND() * 12) + 3 as success_bookings,
    FLOOR(RAND() * 3) as cancelled_bookings,
    FLOOR(RAND() * 20) + 5 as unique_users,
    ROUND(RAND() * 30 + 60, 2) as booking_conversion_rate,
    10 as available_slots,
    FLOOR(RAND() * 8) + 2 as booked_slots,
    ROUND((FLOOR(RAND() * 8) + 2) * 10, 2) as usage_rate,
    FLOOR(RAND() * 2000) + 500 as revenue,
    FLOOR(RAND() * 150) + 50 as avg_order_amount,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'morning'
        WHEN 1 THEN 'afternoon'
        ELSE 'evening'
    END as peak_period,
    FLOOR(RAND() * 5) + 2 as peak_bookings,
    ROUND(RAND() * 1.5 + 3.5, 2) as venue_rating,
    FLOOR(RAND() * 20) + 5 as rating_count,
    FLOOR(RAND() * 100) as usage_score,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'up'
        WHEN 1 THEN 'down'
        ELSE 'stable'
    END as trend,
    FLOOR(RAND() * 20) - 10 as trend_change,
    '建议优化场地设施和服务' as suggestion,
    NOW() as create_time
FROM venue v
CROSS JOIN (
    SELECT 1 as day UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
    UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
) d
WHERE v.status = 1
ON DUPLICATE KEY UPDATE
    total_bookings = VALUES(total_bookings),
    success_bookings = VALUES(success_bookings),
    cancelled_bookings = VALUES(cancelled_bookings),
    unique_users = VALUES(unique_users),
    booking_conversion_rate = VALUES(booking_conversion_rate),
    available_slots = VALUES(available_slots),
    booked_slots = VALUES(booked_slots),
    usage_rate = VALUES(usage_rate),
    revenue = VALUES(revenue),
    avg_order_amount = VALUES(avg_order_amount),
    peak_period = VALUES(peak_period),
    peak_bookings = VALUES(peak_bookings),
    venue_rating = VALUES(venue_rating),
    rating_count = VALUES(rating_count),
    usage_score = VALUES(usage_score),
    trend = VALUES(trend),
    trend_change = VALUES(trend_change),
    suggestion = VALUES(suggestion);

-- ========================================
-- 8. 验证数据完整性
-- ========================================
SELECT
    '数据修复完成！' as message,
    (SELECT COUNT(*) FROM member_activity_analysis WHERE analysis_date >= CURDATE() - INTERVAL 7 DAY) as member_records,
    (SELECT COUNT(*) FROM course_popularity_analysis WHERE analysis_date >= CURDATE() - INTERVAL 7 DAY) as course_records,
    (SELECT COUNT(*) FROM venue_usage_analysis WHERE analysis_date >= CURDATE() - INTERVAL 7 DAY) as venue_records,
    (SELECT COUNT(*) FROM member_activity_analysis WHERE analysis_date = CURDATE()) as today_member_records,
    (SELECT COUNT(*) FROM course_popularity_analysis WHERE analysis_date = CURDATE()) as today_course_records,
    (SELECT COUNT(*) FROM venue_usage_analysis WHERE analysis_date = CURDATE()) as today_venue_records;

-- 显示今天的课程排名（验证ranking字段）
SELECT
    c.course_name,
    cpa.popularity_score,
    cpa.ranking,
    cpa.enroll_count
FROM course_popularity_analysis cpa
JOIN course c ON cpa.course_id = c.id
WHERE cpa.analysis_date = CURDATE()
ORDER BY cpa.ranking
LIMIT 10;

-- 显示今天的场地使用情况（验证数据）
SELECT
    v.venue_name,
    vua.usage_rate,
    vua.revenue,
    vua.total_bookings
FROM venue_usage_analysis vua
JOIN venue v ON vua.venue_id = v.id
WHERE vua.analysis_date = CURDATE()
ORDER BY vua.usage_rate DESC
LIMIT 10;
