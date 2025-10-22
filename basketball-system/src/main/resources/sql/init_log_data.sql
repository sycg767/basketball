-- ========================================
-- 日志测试数据初始化脚本
-- 生成最近7天的登录日志和操作日志
-- ========================================

-- 清空现有日志数据（可选）
-- TRUNCATE TABLE login_log;
-- TRUNCATE TABLE operation_log;

-- ========================================
-- 登录日志测试数据（最近7天）
-- ========================================

-- 今天的登录记录
INSERT INTO `login_log` (`user_id`, `username`, `login_type`, `ip`, `location`, `browser`, `os`, `status`, `message`, `login_time`) VALUES
(1, 'admin', 1, '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(9, 'testuser', 1, '192.168.1.101', NULL, 'Firefox', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(NULL, 'wronguser', 1, '192.168.1.102', NULL, 'Chrome', 'Mac OS X', 0, '用户不存在', DATE_SUB(NOW(), INTERVAL 30 MINUTE));

-- 昨天的登录记录
INSERT INTO `login_log` (`user_id`, `username`, `login_type`, `ip`, `location`, `browser`, `os`, `status`, `message`, `login_time`) VALUES
(1, 'admin', 1, '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(9, 'testuser', 2, '192.168.1.101', NULL, 'Safari', 'iOS', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 2 HOUR),
(9, 'testuser', 1, '192.168.1.103', NULL, 'Edge', 'Windows 11', 0, '密码错误', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 5 HOUR);

-- 2天前的登录记录
INSERT INTO `login_log` (`user_id`, `username`, `login_type`, `ip`, `location`, `browser`, `os`, `status`, `message`, `login_time`) VALUES
(1, 'admin', 1, '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(9, 'testuser', 1, '192.168.1.101', NULL, 'Chrome', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 3 HOUR);

-- 3天前的登录记录
INSERT INTO `login_log` (`user_id`, `username`, `login_type`, `ip`, `location`, `browser`, `os`, `status`, `message`, `login_time`) VALUES
(1, 'admin', 1, '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(NULL, 'testuser', 1, '192.168.1.101', NULL, 'Firefox', 'Linux', 0, '密码错误', DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 2 HOUR),
(9, 'testuser', 1, '192.168.1.101', NULL, 'Firefox', 'Linux', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 2 HOUR + INTERVAL 5 MINUTE);

-- 4天前的登录记录
INSERT INTO `login_log` (`user_id`, `username`, `login_type`, `ip`, `location`, `browser`, `os`, `status`, `message`, `login_time`) VALUES
(1, 'admin', 1, '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(9, 'testuser', 2, '192.168.1.105', NULL, 'Chrome', 'Android', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 4 DAY) + INTERVAL 4 HOUR);

-- 5天前的登录记录
INSERT INTO `login_log` (`user_id`, `username`, `login_type`, `ip`, `location`, `browser`, `os`, `status`, `message`, `login_time`) VALUES
(1, 'admin', 1, '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(9, 'testuser', 1, '192.168.1.101', NULL, 'Chrome', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 5 DAY) + INTERVAL 1 HOUR);

-- 6天前的登录记录
INSERT INTO `login_log` (`user_id`, `username`, `login_type`, `ip`, `location`, `browser`, `os`, `status`, `message`, `login_time`) VALUES
(1, 'admin', 1, '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(NULL, 'admin', 1, '192.168.1.200', NULL, 'Chrome', 'Windows 10', 0, '密码错误', DATE_SUB(NOW(), INTERVAL 6 DAY) + INTERVAL 2 HOUR),
(9, 'testuser', 1, '192.168.1.101', NULL, 'Firefox', 'Windows 10', 1, '登录成功', DATE_SUB(NOW(), INTERVAL 6 DAY) + INTERVAL 3 HOUR);

-- ========================================
-- 操作日志测试数据（最近7天）
-- ========================================

-- 今天的操作记录
INSERT INTO `operation_log` (`user_id`, `username`, `operation`, `method`, `params`, `result`, `ip`, `location`, `browser`, `os`, `status`, `error_msg`, `execute_time`, `create_time`) VALUES
(1, 'admin', '更新用户信息', 'com.basketball.controller.AdminUserController.updateUser', '[{"id":9,"nickname":"测试用户"}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 125, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 'admin', '启用/禁用用户', 'com.basketball.controller.AdminUserController.toggleUserStatus', '[{"id":10,"status":0}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 89, DATE_SUB(NOW(), INTERVAL 30 MINUTE));

-- 昨天的操作记录
INSERT INTO `operation_log` (`user_id`, `username`, `operation`, `method`, `params`, `result`, `ip`, `location`, `browser`, `os`, `status`, `error_msg`, `execute_time`, `create_time`) VALUES
(1, 'admin', '删除用户', 'com.basketball.controller.AdminUserController.deleteUser', '[{"id":15}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 156, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 'admin', '更新用户信息', 'com.basketball.controller.AdminUserController.updateUser', '[{"id":9,"nickname":"新昵称"}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 98, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 2 HOUR),
(1, 'admin', '启用/禁用用户', 'com.basketball.controller.AdminUserController.toggleUserStatus', '[{"id":12,"status":1}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 76, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 4 HOUR);

-- 2天前的操作记录
INSERT INTO `operation_log` (`user_id`, `username`, `operation`, `method`, `params`, `result`, `ip`, `location`, `browser`, `os`, `status`, `error_msg`, `execute_time`, `create_time`) VALUES
(1, 'admin', '更新用户信息', 'com.basketball.controller.AdminUserController.updateUser', '[{"id":9}]', '{"code":500,"message":"系统错误"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 0, '数据库连接超时', 2345, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'admin', '删除用户', 'com.basketball.controller.AdminUserController.deleteUser', '[{"id":16}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 134, DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 3 HOUR);

-- 3天前的操作记录
INSERT INTO `operation_log` (`user_id`, `username`, `operation`, `method`, `params`, `result`, `ip`, `location`, `browser`, `os`, `status`, `error_msg`, `execute_time`, `create_time`) VALUES
(1, 'admin', '启用/禁用用户', 'com.basketball.controller.AdminUserController.toggleUserStatus', '[{"id":11,"status":0}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 92, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 'admin', '更新用户信息', 'com.basketball.controller.AdminUserController.updateUser', '[{"id":9,"email":"new@example.com"}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 112, DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 2 HOUR);

-- 4天前的操作记录
INSERT INTO `operation_log` (`user_id`, `username`, `operation`, `method`, `params`, `result`, `ip`, `location`, `browser`, `os`, `status`, `error_msg`, `execute_time`, `create_time`) VALUES
(1, 'admin', '删除用户', 'com.basketball.controller.AdminUserController.deleteUser', '[{"id":17}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 145, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, 'admin', '更新用户信息', 'com.basketball.controller.AdminUserController.updateUser', '[{"id":10}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Firefox', 'Windows 10', 1, NULL, 103, DATE_SUB(NOW(), INTERVAL 4 DAY) + INTERVAL 3 HOUR);

-- 5天前的操作记录
INSERT INTO `operation_log` (`user_id`, `username`, `operation`, `method`, `params`, `result`, `ip`, `location`, `browser`, `os`, `status`, `error_msg`, `execute_time`, `create_time`) VALUES
(1, 'admin', '启用/禁用用户', 'com.basketball.controller.AdminUserController.toggleUserStatus', '[{"id":13,"status":1}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 88, DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 6天前的操作记录
INSERT INTO `operation_log` (`user_id`, `username`, `operation`, `method`, `params`, `result`, `ip`, `location`, `browser`, `os`, `status`, `error_msg`, `execute_time`, `create_time`) VALUES
(1, 'admin', '更新用户信息', 'com.basketball.controller.AdminUserController.updateUser', '[{"id":9,"phone":"13800138000"}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 118, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, 'admin', '删除用户', 'com.basketball.controller.AdminUserController.deleteUser', '[{"id":18}]', '{"code":200,"message":"操作成功"}', '192.168.1.100', NULL, 'Chrome', 'Windows 10', 1, NULL, 152, DATE_SUB(NOW(), INTERVAL 6 DAY) + INTERVAL 2 HOUR);

-- ========================================
-- 数据统计
-- ========================================
-- 登录日志：约20条（成功16条，失败4条）
-- 操作日志：约18条（成功17条，失败1条）
-- ========================================
