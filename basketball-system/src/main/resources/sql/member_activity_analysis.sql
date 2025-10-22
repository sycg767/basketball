-- 会员活跃度分析表
CREATE TABLE IF NOT EXISTS member_activity_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    analysis_date DATE NOT NULL COMMENT '分析日期',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    booking_count INT DEFAULT 0 COMMENT '预订次数',
    course_count INT DEFAULT 0 COMMENT '课程次数',
    activity_score INT DEFAULT 0 COMMENT '活跃度分数(0-100)',
    last_active_time DATETIME COMMENT '最后活跃时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_date (user_id, analysis_date),
    INDEX idx_date (analysis_date),
    INDEX idx_score (activity_score),
    UNIQUE KEY uk_user_date (user_id, analysis_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员活跃度分析表';
