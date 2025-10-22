-- 初始化分析数据的SQL脚本
-- 用于生成测试数据，让分析功能有数据展示

-- 1. 会员活跃度分析测试数据
INSERT INTO member_activity_analysis (user_id, analysis_date, login_count, view_count, booking_count, payment_count, payment_amount, activity_score, last_active_time, create_time)
SELECT
    u.id as user_id,
    CURDATE() as analysis_date,
    FLOOR(RAND() * 10) + 1 as login_count,
    FLOOR(RAND() * 20) + 5 as view_count,
    FLOOR(RAND() * 5) as booking_count,
    FLOOR(RAND() * 3) as payment_count,
    FLOOR(RAND() * 1000) + 100 as payment_amount,
    FLOOR(RAND() * 100) as activity_score,
    NOW() - INTERVAL FLOOR(RAND() * 30) DAY as last_active_time,
    NOW() as create_time
FROM user u
WHERE u.status = 1
ON DUPLICATE KEY UPDATE
    login_count = VALUES(login_count),
    view_count = VALUES(view_count),
    booking_count = VALUES(booking_count),
    payment_count = VALUES(payment_count),
    payment_amount = VALUES(payment_amount),
    activity_score = VALUES(activity_score),
    last_active_time = VALUES(last_active_time),
    update_time = NOW();

-- 2. 课程热度分析测试数据
INSERT INTO course_popularity_analysis (course_id, analysis_date, view_count, enrollment_count, completion_count, popularity_score, create_time)
SELECT
    c.id as course_id,
    CURDATE() as analysis_date,
    FLOOR(RAND() * 100) + 10 as view_count,
    FLOOR(RAND() * 20) + 1 as enrollment_count,
    FLOOR(RAND() * 15) as completion_count,
    FLOOR(RAND() * 100) as popularity_score,
    NOW() as create_time
FROM course c
WHERE c.status = 1
ON DUPLICATE KEY UPDATE
    view_count = VALUES(view_count),
    enrollment_count = VALUES(enrollment_count),
    completion_count = VALUES(completion_count),
    popularity_score = VALUES(popularity_score),
    update_time = NOW();

-- 3. 场地使用分析测试数据
INSERT INTO venue_usage_analysis (venue_id, analysis_date, booking_count, usage_hours, revenue, usage_rate, peak_period, create_time)
SELECT
    v.id as venue_id,
    CURDATE() as analysis_date,
    FLOOR(RAND() * 10) + 1 as booking_count,
    FLOOR(RAND() * 8) + 2 as usage_hours,
    FLOOR(RAND() * 1000) + 100 as revenue,
    FLOOR(RAND() * 100) as usage_rate,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN '08:00-12:00'
        WHEN 1 THEN '12:00-18:00'
        ELSE '18:00-22:00'
    END as peak_period,
    NOW() as create_time
FROM venue v
WHERE v.status = 1
ON DUPLICATE KEY UPDATE
    booking_count = VALUES(booking_count),
    usage_hours = VALUES(usage_hours),
    revenue = VALUES(revenue),
    usage_rate = VALUES(usage_rate),
    peak_period = VALUES(peak_period),
    update_time = NOW();

-- 生成最近7天的历史数据
INSERT INTO member_activity_analysis (user_id, analysis_date, login_count, view_count, booking_count, payment_count, activity_score, last_active_time, create_time)
SELECT
    u.id as user_id,
    CURDATE() - INTERVAL d.day DAY as analysis_date,
    FLOOR(RAND() * 10) + 1 as login_count,
    FLOOR(RAND() * 20) + 5 as view_count,
    FLOOR(RAND() * 5) as booking_count,
    FLOOR(RAND() * 3) as payment_count,
    FLOOR(RAND() * 100) as activity_score,
    NOW() - INTERVAL d.day DAY as last_active_time,
    NOW() as create_time
FROM user u
CROSS JOIN (
    SELECT 1 as day UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
) d
WHERE u.status = 1
ON DUPLICATE KEY UPDATE
    login_count = VALUES(login_count),
    view_count = VALUES(view_count),
    booking_count = VALUES(booking_count),
    payment_count = VALUES(payment_count),
    activity_score = VALUES(activity_score);
