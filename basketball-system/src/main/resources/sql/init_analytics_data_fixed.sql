-- 初始化分析数据的SQL脚本（已修复字段名）
-- 用于生成测试数据，让分析功能有数据展示

-- 1. 会员活跃度分析测试数据
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
    FLOOR(RAND() * 5) as booking_count,
    FLOOR(RAND() * 3) as payment_count,
    FLOOR(RAND() * 1000) + 100 as payment_amount,
    FLOOR(RAND() * 2) as cancel_count,
    FLOOR(RAND() * 100) as activity_score,
    NOW() - INTERVAL FLOOR(RAND() * 30) DAY as last_active_time,
    FLOOR(RAND() * 10) as continuous_days,
    FLOOR(RAND() * 30) as rfm_r,
    FLOOR(RAND() * 10) + 1 as rfm_f,
    FLOOR(RAND() * 5000) + 500 as rfm_m,
    FLOOR(RAND() * 100) as rfm_score,
    FLOOR(RAND() * 4) as churn_risk,
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

-- 2. 课程热度分析测试数据
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

-- 3. 场地使用分析测试数据
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
    FLOOR(RAND() * 15) + 5 as total_bookings,
    FLOOR(RAND() * 12) + 3 as success_bookings,
    FLOOR(RAND() * 3) as cancelled_bookings,
    FLOOR(RAND() * 20) + 5 as unique_users,
    ROUND(RAND() * 30 + 60, 2) as booking_conversion_rate,
    10 as available_slots,
    FLOOR(RAND() * 8) + 2 as booked_slots,
    ROUND(RAND() * 60 + 20, 2) as usage_rate,
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

-- 4. 生成最近7天的会员活跃度历史数据
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
        WHEN d.day > 20 THEN 3
        WHEN d.day > 10 THEN 2
        WHEN d.day > 5 THEN 1
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

-- 执行完成提示
SELECT '数据初始化完成！' as message,
       (SELECT COUNT(*) FROM member_activity_analysis) as member_records,
       (SELECT COUNT(*) FROM course_popularity_analysis) as course_records,
       (SELECT COUNT(*) FROM venue_usage_analysis) as venue_records;
