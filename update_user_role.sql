-- 添加用户角色字段
ALTER TABLE `user` ADD COLUMN `role` int DEFAULT 0 COMMENT '用户角色：0-普通用户，1-管理员' AFTER `balance`;

-- 更新现有用户的角色：用户ID为1的设为管理员，其他为普通用户
UPDATE `user` SET `role` = 1 WHERE `id` = 1;
UPDATE `user` SET `role` = 0 WHERE `id` != 1 AND `role` IS NULL;