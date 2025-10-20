/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : basketball_system

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 20/10/2025 18:17:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `type` tinyint NOT NULL COMMENT '类型: 1-系统通知, 2-活动公告, 3-维护公告, 4-优惠活动',
  `priority` tinyint NULL DEFAULT 0 COMMENT '优先级: 0-普通, 1-重要, 2-紧急',
  `publisher_id` bigint NULL DEFAULT NULL COMMENT '发布人ID',
  `target_type` tinyint NULL DEFAULT 1 COMMENT '目标对象: 1-所有用户, 2-会员, 3-教练',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
  `is_popup` tinyint NULL DEFAULT 0 COMMENT '是否弹窗: 0-否, 1-是',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布, 2-已下架',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publisher_id`(`publisher_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE,
  CONSTRAINT `announcement_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, '系统上线通知', '篮球场馆预约系统正式上线运营，欢迎各位用户使用！', 1, 1, 1, 1, 0, 0, NULL, 1, 0, '2025-10-20 18:12:28', NULL, '2025-10-20 18:12:28', '2025-10-20 18:12:28');
INSERT INTO `announcement` VALUES (2, '系统维护通知', '系统将于2025年10月25日凌晨2:00-4:00进行维护，期间服务可能受到影响。', 2, 1, 1, 1, 0, 0, NULL, 1, 0, '2025-10-20 18:12:28', NULL, '2025-10-20 18:12:28', '2025-10-20 18:12:28');
INSERT INTO `announcement` VALUES (3, '新功能发布', '系统新增了会员管理、课程预约等功能，体验更丰富的服务！', 1, 1, 1, 1, 0, 0, NULL, 1, 0, '2025-10-20 18:12:28', NULL, '2025-10-20 18:12:28', '2025-10-20 18:12:28');
INSERT INTO `announcement` VALUES (4, '价格调整通知', '由于场地维护成本上升，部分场地价格将于下月1日起调整。', 2, 1, 1, 1, 0, 0, NULL, 1, 0, '2025-10-20 18:12:28', NULL, '2025-10-20 18:12:28', '2025-10-20 18:12:28');
INSERT INTO `announcement` VALUES (5, '节日放假通知', '国庆节期间场馆开放时间调整，请各位用户注意。', 3, 1, 1, 1, 0, 0, NULL, 1, 0, '2025-10-20 18:12:28', NULL, '2025-10-20 18:12:28', '2025-10-20 18:12:28');

-- ----------------------------
-- Table structure for announcement_read
-- ----------------------------
DROP TABLE IF EXISTS `announcement_read`;
CREATE TABLE `announcement_read`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '阅读记录ID',
  `announcement_id` bigint NOT NULL COMMENT '公告ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_announcement_user`(`announcement_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `announcement_read_ibfk_1` FOREIGN KEY (`announcement_id`) REFERENCES `announcement` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `announcement_read_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告阅读记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement_read
-- ----------------------------

-- ----------------------------
-- Table structure for booking
-- ----------------------------
DROP TABLE IF EXISTS `booking`;
CREATE TABLE `booking`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预订ID',
  `booking_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预订编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `venue_id` bigint NOT NULL COMMENT '场地ID',
  `booking_date` date NOT NULL COMMENT '预订日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `duration` int NOT NULL COMMENT '时长(小时)',
  `booking_type` tinyint NULL DEFAULT 1 COMMENT '预订类型: 1-按时段, 2-包场',
  `total_price` decimal(10, 2) NOT NULL COMMENT '总价',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `actual_price` decimal(10, 2) NOT NULL COMMENT '实际支付金额',
  `payment_method` tinyint NULL DEFAULT NULL COMMENT '支付方式: 1-在线支付, 2-余额, 3-会员卡, 4-现场支付',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-已支付, 2-已取消, 3-已完成, 4-已退款, 5-超时取消',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
  `contact_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `people_count` int NULL DEFAULT 1 COMMENT '人数',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `is_checked_in` tinyint NULL DEFAULT 0 COMMENT '是否已签到: 0-否, 1-是',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间(待支付订单)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `booking_no`(`booking_no` ASC) USING BTREE,
  INDEX `idx_booking_no`(`booking_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_venue_id`(`venue_id` ASC) USING BTREE,
  INDEX `idx_date_venue`(`booking_date` ASC, `venue_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_booking_user_date`(`user_id` ASC, `booking_date` ASC) USING BTREE,
  INDEX `idx_booking_venue_status`(`venue_id` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预订表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booking
-- ----------------------------
INSERT INTO `booking` VALUES (1, 'BK2025100108001', 2, 1, '2025-09-28', '18:00:00', '20:00:00', 2, 1, 300.00, 30.00, 270.00, 2, 3, NULL, NULL, '张三', '13800138001', 8, '公司团建活动', 1, '2025-09-28 17:50:00', '2025-09-26 14:00:00', '2025-10-02 20:49:48', '2025-09-26 14:05:00', NULL);
INSERT INTO `booking` VALUES (2, 'BK2025100108002', 3, 5, '2025-09-29', '14:00:00', '16:00:00', 2, 1, 140.00, 14.00, 126.00, 1, 3, NULL, NULL, '李四', '13800138002', 5, '周末打球', 1, '2025-09-29 13:55:00', '2025-09-28 10:30:00', '2025-10-02 20:49:48', '2025-09-28 10:35:00', NULL);
INSERT INTO `booking` VALUES (3, 'BK2025100108003', 4, 7, '2025-09-30', '19:00:00', '21:00:00', 2, 1, 120.00, 0.00, 120.00, 4, 3, NULL, NULL, '王五', '13800138003', 6, '', 1, '2025-09-30 18:55:00', '2025-09-29 16:00:00', '2025-10-02 20:49:48', '2025-09-30 18:55:00', NULL);
INSERT INTO `booking` VALUES (4, 'BK2025100208004', 2, 2, '2025-10-06', '14:00:00', '16:00:00', 2, 1, 200.00, 20.00, 180.00, 2, 1, NULL, NULL, '张三', '13800138001', 10, '朋友聚会', 0, NULL, '2025-10-03 09:00:00', '2025-10-02 20:49:48', '2025-10-03 09:05:00', NULL);
INSERT INTO `booking` VALUES (5, 'BK2025100208005', 3, 3, '2025-10-08', '19:00:00', '21:00:00', 2, 1, 160.00, 16.00, 144.00, 1, 1, NULL, NULL, '李四', '13800138002', 8, '', 0, NULL, '2025-10-03 10:00:00', '2025-10-02 20:49:48', '2025-10-03 10:02:00', NULL);
INSERT INTO `booking` VALUES (6, 'BK2025100208006', 2, 1, '2025-10-10', '18:00:00', '20:00:00', 2, 1, 300.00, 30.00, 270.00, NULL, 0, NULL, NULL, '张三', '13800138001', 12, '公司篮球赛', 0, NULL, '2025-10-03 11:00:00', '2025-10-02 20:49:48', NULL, NULL);
INSERT INTO `booking` VALUES (7, 'BK2025100208007', 4, 6, '2025-10-12', '10:00:00', '12:00:00', 2, 1, 130.00, 0.00, 130.00, NULL, 0, NULL, NULL, '王五', '13800138003', 6, '', 0, NULL, '2025-10-03 11:30:00', '2025-10-02 20:49:48', NULL, NULL);
INSERT INTO `booking` VALUES (8, 'BK2025100208008', 3, 4, '2025-10-07', '15:00:00', '17:00:00', 2, 1, 150.00, 15.00, 135.00, NULL, 2, NULL, NULL, '李四', '13800138002', 5, '时间冲突', 0, NULL, '2025-10-02 14:00:00', '2025-10-02 20:49:48', NULL, NULL);
INSERT INTO `booking` VALUES (9, 'BK2025100208009', 2, 5, '2025-10-05', '09:00:00', '11:00:00', 2, 1, 140.00, 14.00, 126.00, 1, 4, NULL, NULL, '张三', '13800138001', 8, '临时有事取消', 0, NULL, '2025-10-01 16:00:00', '2025-10-02 20:49:48', '2025-10-01 16:05:00', NULL);
INSERT INTO `booking` VALUES (10, 'BK2025100208010', 4, 8, '2025-10-15', '18:00:00', '20:00:00', 2, 1, 110.00, 0.00, 110.00, NULL, 5, NULL, NULL, '王五', '13800138003', 4, '', 0, NULL, '2025-10-03 12:00:00', '2025-10-02 20:49:48', NULL, NULL);
INSERT INTO `booking` VALUES (11, 'BK1760725977687', 9, 2, '2025-10-23', '09:00:00', '13:00:00', 4, 1, 400.00, 0.00, 400.00, NULL, 0, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-18 02:32:58', '2025-10-18 02:32:58', NULL, '2025-10-18 03:02:58');
INSERT INTO `booking` VALUES (12, 'BK1760727377802', 9, 1, '2025-10-20', '08:00:00', '11:00:00', 3, 1, 360.00, 0.00, 360.00, 1, 1, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-18 02:56:18', '2025-10-18 03:14:42', '2025-10-18 03:14:42', '2025-10-18 03:26:18');
INSERT INTO `booking` VALUES (13, 'BK1760739804199', 9, 1, '2025-10-26', '10:00:00', '12:00:00', 2, 1, 300.00, 0.00, 300.00, 1, 0, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-18 06:23:24', '2025-10-18 06:37:32', NULL, '2025-10-18 06:53:24');
INSERT INTO `booking` VALUES (14, 'BK1760802760177', 9, 2, '2025-10-28', '09:00:00', '12:00:00', 3, 1, 300.00, 0.00, 300.00, 1, 0, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-18 23:52:40', '2025-10-19 00:22:25', NULL, '2025-10-19 00:22:40');
INSERT INTO `booking` VALUES (15, 'BK1760804982329', 9, 2, '2025-11-04', '11:00:00', '21:00:00', 10, 1, 1000.00, 0.00, 1000.00, 1, 0, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-19 00:29:42', '2025-10-19 00:50:18', NULL, '2025-10-19 00:59:42');
INSERT INTO `booking` VALUES (16, 'BK1760806250791', 9, 3, '2025-10-20', '08:00:00', '14:00:00', 6, 1, 360.00, 0.00, 360.00, 1, 0, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-19 00:50:51', '2025-10-19 00:50:57', NULL, '2025-10-19 01:20:51');
INSERT INTO `booking` VALUES (17, 'BK1760807197060', 9, 4, '2025-10-22', '12:00:00', '14:00:00', 2, 1, 110.00, 0.00, 110.00, 1, 1, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-19 01:06:37', '2025-10-19 01:06:42', '2025-10-19 01:06:42', '2025-10-19 01:36:37');
INSERT INTO `booking` VALUES (18, 'BK1760923408792', 9, 3, '2025-10-28', '09:00:00', '12:00:00', 3, 1, 180.00, 0.00, 180.00, 1, 1, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-20 09:23:29', '2025-10-20 09:23:45', '2025-10-20 09:23:45', '2025-10-20 09:53:29');
INSERT INTO `booking` VALUES (19, 'BK1760923670119', 9, 3, '2025-11-04', '10:00:00', '12:00:00', 2, 1, 120.00, 0.00, 120.00, 1, 1, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-20 09:27:50', '2025-10-20 09:34:08', '2025-10-20 09:34:08', '2025-10-20 09:57:50');
INSERT INTO `booking` VALUES (20, 'BK1760924690784', 9, 4, '2025-11-27', '11:00:00', '14:00:00', 3, 1, 165.00, 0.00, 165.00, 1, 1, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-20 09:44:51', '2025-10-20 09:45:00', '2025-10-20 09:45:00', '2025-10-20 10:14:51');
INSERT INTO `booking` VALUES (21, 'BK1760925040943', 9, 6, '2025-10-29', '09:00:00', '11:00:00', 2, 1, 130.00, 0.00, 130.00, 1, 1, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-20 09:50:41', '2025-10-20 09:50:50', '2025-10-20 09:50:50', '2025-10-20 10:20:41');
INSERT INTO `booking` VALUES (22, 'BK1760925852660', 9, 1, '2025-10-31', '07:00:00', '11:00:00', 4, 1, 400.00, 0.00, 400.00, 1, 1, NULL, NULL, '', '', 1, '', 0, NULL, '2025-10-20 10:04:13', '2025-10-20 10:04:37', '2025-10-20 10:04:37', '2025-10-20 10:34:13');
INSERT INTO `booking` VALUES (23, 'BK1760929190001', 9, 1, '2025-10-21', '14:00:00', '16:00:00', 2, 1, 200.00, 0.00, 200.00, 1, 3, NULL, NULL, '张三', '13800138000', 5, '周末预订', 0, NULL, '2025-10-20 10:59:50', '2025-10-20 10:59:50', '2025-10-21 13:55:00', NULL);
INSERT INTO `booking` VALUES (24, 'BK1760929190002', 9, 2, '2025-10-22', '10:00:00', '12:00:00', 2, 1, 160.00, 0.00, 160.00, 1, 3, NULL, NULL, '张三', '13800138000', 4, '上午场', 0, NULL, '2025-10-20 10:59:50', '2025-10-20 10:59:50', '2025-10-22 09:55:00', NULL);
INSERT INTO `booking` VALUES (25, 'BK1760929190003', 9, 1, '2025-10-23', '16:00:00', '18:00:00', 2, 1, 200.00, 0.00, 200.00, 1, 3, NULL, NULL, '张三', '13800138000', 6, '晚场预订', 0, NULL, '2025-10-20 10:59:50', '2025-10-20 10:59:50', '2025-10-23 15:55:00', NULL);
INSERT INTO `booking` VALUES (26, 'BK1760929190004', 9, 3, '2025-10-24', '09:00:00', '11:00:00', 2, 1, 240.00, 0.00, 240.00, 1, 3, NULL, NULL, '张三', '13800138000', 8, '早场预订', 0, NULL, '2025-10-20 10:59:50', '2025-10-20 10:59:50', '2025-10-24 08:55:00', NULL);
INSERT INTO `booking` VALUES (27, 'BK1760929190005', 9, 1, '2025-10-25', '14:00:00', '16:00:00', 2, 1, 200.00, 0.00, 200.00, 1, 3, NULL, NULL, '张三', '13800138000', 5, '下午场', 0, NULL, '2025-10-20 10:59:50', '2025-10-20 10:59:50', '2025-10-25 13:55:00', NULL);
INSERT INTO `booking` VALUES (28, 'BK1760929338001', 9, 1, '2025-10-21', '14:00:00', '16:00:00', 2, 1, 200.00, 0.00, 200.00, 1, 3, NULL, NULL, '张三', '13800138000', 5, '周末预订', 0, NULL, '2025-10-20 11:02:18', '2025-10-20 11:02:18', '2025-10-21 13:55:00', NULL);
INSERT INTO `booking` VALUES (29, 'BK1760929338002', 9, 2, '2025-10-22', '10:00:00', '12:00:00', 2, 1, 160.00, 0.00, 160.00, 1, 3, NULL, NULL, '张三', '13800138000', 4, '上午场', 0, NULL, '2025-10-20 11:02:18', '2025-10-20 11:02:18', '2025-10-22 09:55:00', NULL);
INSERT INTO `booking` VALUES (30, 'BK1760929338003', 9, 1, '2025-10-23', '16:00:00', '18:00:00', 2, 1, 200.00, 0.00, 200.00, 1, 3, NULL, NULL, '张三', '13800138000', 6, '晚场预订', 0, NULL, '2025-10-20 11:02:18', '2025-10-20 11:02:18', '2025-10-23 15:55:00', NULL);
INSERT INTO `booking` VALUES (31, 'BK1760929338004', 9, 3, '2025-10-24', '09:00:00', '11:00:00', 2, 1, 240.00, 0.00, 240.00, 1, 3, NULL, NULL, '张三', '13800138000', 8, '早场预订', 0, NULL, '2025-10-20 11:02:18', '2025-10-20 11:02:18', '2025-10-24 08:55:00', NULL);
INSERT INTO `booking` VALUES (32, 'BK1760929338005', 9, 1, '2025-10-25', '14:00:00', '16:00:00', 2, 1, 200.00, 0.00, 200.00, 1, 3, NULL, NULL, '张三', '13800138000', 5, '下午场', 0, NULL, '2025-10-20 11:02:18', '2025-10-20 11:02:18', '2025-10-25 13:55:00', NULL);

-- ----------------------------
-- Table structure for booking_detail
-- ----------------------------
DROP TABLE IF EXISTS `booking_detail`;
CREATE TABLE `booking_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `booking_id` bigint NOT NULL COMMENT '预订ID',
  `time_slot` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时段',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `price` decimal(10, 2) NOT NULL COMMENT '该时段价格',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_booking_id`(`booking_id` ASC) USING BTREE,
  CONSTRAINT `booking_detail_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预订详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booking_detail
-- ----------------------------

-- ----------------------------
-- Table structure for booking_review
-- ----------------------------
DROP TABLE IF EXISTS `booking_review`;
CREATE TABLE `booking_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `booking_id` bigint NOT NULL COMMENT '预订ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `venue_id` bigint NOT NULL COMMENT '场地ID',
  `rating` tinyint NOT NULL COMMENT '评分: 1-5星',
  `environment_rating` tinyint NULL DEFAULT NULL COMMENT '环境评分',
  `facility_rating` tinyint NULL DEFAULT NULL COMMENT '设施评分',
  `service_rating` tinyint NULL DEFAULT NULL COMMENT '服务评分',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价图片(JSON数组)',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商家回复',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_booking_id`(`booking_id` ASC) USING BTREE,
  INDEX `idx_venue_id`(`venue_id` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  CONSTRAINT `booking_review_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `booking_review_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `booking_review_ibfk_3` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预订评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booking_review
-- ----------------------------

-- ----------------------------
-- Table structure for coach_income
-- ----------------------------
DROP TABLE IF EXISTS `coach_income`;
CREATE TABLE `coach_income`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收入ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `income_type` tinyint NULL DEFAULT NULL COMMENT '收入类型: 1-课程分成, 2-私教课, 3-奖金',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联ID(课程ID或排期ID)',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `commission_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '分成比例',
  `income_amount` decimal(10, 2) NOT NULL COMMENT '收入金额',
  `settlement_status` tinyint NULL DEFAULT 0 COMMENT '结算状态: 0-未结算, 1-已结算',
  `settlement_time` datetime NULL DEFAULT NULL COMMENT '结算时间',
  `month` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属月份: 2025-10',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_month`(`month` ASC) USING BTREE,
  INDEX `idx_settlement_status`(`settlement_status` ASC) USING BTREE,
  CONSTRAINT `coach_income_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教练收入表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coach_income
-- ----------------------------

-- ----------------------------
-- Table structure for coach_info
-- ----------------------------
DROP TABLE IF EXISTS `coach_info`;
CREATE TABLE `coach_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教练信息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `certificate` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资格证书',
  `specialty` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专长',
  `experience_years` int NULL DEFAULT NULL COMMENT '从教年限',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人简介',
  `achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人成就(JSON数组)',
  `hourly_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '私教课时费',
  `commission_rate` decimal(5, 2) NULL DEFAULT 50.00 COMMENT '课程分成比例(%)',
  `rating` decimal(3, 2) NULL DEFAULT 5.00 COMMENT '评分',
  `total_students` int NULL DEFAULT 0 COMMENT '教授学员数',
  `total_courses` int NULL DEFAULT 0 COMMENT '开设课程数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-离职, 1-在职, 2-休假',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `coach_info_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教练信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coach_info
-- ----------------------------
INSERT INTO `coach_info` VALUES (1, 5, '国家一级篮球运动员,篮球E级教练员', '基础技术,投篮训练,体能训练', 8, '前职业球员，曾效力于CBA青年队，退役后专注于青少年篮球培训。擅长基础技术教学和投篮训练，注重培养学员的基本功和运动习惯。', '[\"2018年获得市青少年篮球培训优秀教练员\",\"培养学员超过200人\",\"学员多次获得市级比赛冠军\"]', 200.00, 50.00, 4.80, 156, 12, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `coach_info` VALUES (2, 6, '国家二级篮球运动员,篮球D级教练员,体能训练师', '战术分析,防守技术,团队配合', 12, '资深篮球教练，拥有12年执教经验。曾担任多支校队主教练，擅长战术分析和防守体系构建。教学风格严谨，注重细节。', '[\"2015-2020年连续6年获得优秀教练员称号\",\"带队获得省级比赛亚军2次\",\"发表篮球教学论文3篇\"]', 300.00, 55.00, 4.90, 280, 18, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `coach_info` VALUES (3, 7, '国家一级篮球运动员,篮球E级教练员,康复训练师', '运球技术,进攻技巧,伤病预防', 5, '年轻有活力的女教练，专注于青少年和女子篮球培训。教学方法灵活多样，深受学员喜爱。拥有康复训练师资质，注重运动安全。', '[\"2021年获得新锐教练员奖\",\"女子篮球培训专家\",\"学员满意度98%\"]', 180.00, 50.00, 4.70, 95, 8, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `coach_info` VALUES (4, 8, '国家一级篮球运动员,篮球E级教练员', '成人篮球,三人篮球,趣味训练', 6, '专注于成人篮球培训和趣味篮球教学。教学风格轻松幽默，善于调动学员积极性。同时也是三人篮球专业教练。', '[\"三人篮球国家级裁判员\",\"举办篮球公益培训50余场\",\"学员遍布各行各业\"]', 150.00, 45.00, 4.60, 120, 10, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');

-- ----------------------------
-- Table structure for coupon_template
-- ----------------------------
DROP TABLE IF EXISTS `coupon_template`;
CREATE TABLE `coupon_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `coupon_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '优惠券名称',
  `coupon_type` tinyint NOT NULL COMMENT '类型: 1-满减券, 2-折扣券, 3-抵用券, 4-次卡券',
  `discount_type` tinyint NULL DEFAULT NULL COMMENT '优惠类型: 1-金额, 2-折扣',
  `discount_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠金额',
  `discount_rate` decimal(3, 2) NULL DEFAULT NULL COMMENT '折扣率: 0.80表示8折',
  `min_consume` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最低消费金额',
  `max_discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '最大优惠金额(折扣券)',
  `applicable_type` tinyint NULL DEFAULT 1 COMMENT '适用范围: 1-全场通用, 2-场地, 3-课程',
  `total_count` int NULL DEFAULT NULL COMMENT '发行总量(为空表示不限)',
  `receive_limit` int NULL DEFAULT 1 COMMENT '每人限领数量',
  `valid_days` int NULL DEFAULT NULL COMMENT '有效天数(领取后)',
  `start_time` datetime NULL DEFAULT NULL COMMENT '有效期开始',
  `end_time` datetime NULL DEFAULT NULL COMMENT '有效期结束',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '使用说明',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-停用, 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '优惠券模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon_template
-- ----------------------------

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `course_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程编号',
  `course_type` tinyint NOT NULL COMMENT '课程类型: 1-基础班, 2-提高班, 3-专业班, 4-私教课',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `max_students` int NULL DEFAULT 20 COMMENT '最大学员数',
  `min_students` int NULL DEFAULT 5 COMMENT '最小开班人数',
  `price` decimal(10, 2) NOT NULL COMMENT '课程价格',
  `member_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '会员价格',
  `duration` int NOT NULL COMMENT '课时(分钟)',
  `total_sessions` int NULL DEFAULT 1 COMMENT '总课时数',
  `difficulty_level` tinyint NULL DEFAULT NULL COMMENT '难度等级: 1-入门, 2-初级, 3-中级, 4-高级',
  `age_range` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适合年龄: 如 10-18岁',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程描述',
  `syllabus` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程大纲(JSON格式)',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宣传视频',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架, 2-已满',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `enroll_count` int NULL DEFAULT 0 COMMENT '报名人数',
  `rating` decimal(3, 2) NULL DEFAULT 5.00 COMMENT '评分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `course_code`(`course_code` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_type`(`course_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_course_coach_status`(`coach_id` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '青少年篮球基础班', 'COU001BASIC01', 1, 5, 21, 10, 1200.00, 1000.00, 90, 12, 1, '10-15岁', '专为青少年设计的篮球基础课程，从零开始学习篮球基本技术。课程包括运球、传球、投篮等基础动作训练，以及基本规则讲解。', '[\"第1-2课：篮球基础知识和规则\",\"第3-4课：运球基本功训练\",\"第5-6课：传接球技术\",\"第7-8课：投篮姿势和技巧\",\"第9-10课：基础防守动作\",\"第11-12课：小型对抗赛\"]', 'https://images.unsplash.com/photo-1546519638-68e109498ffc?w=600&h=400&fit=crop', NULL, 1, 163, 45, 4.80, '2025-10-02 20:49:48', '2025-10-02 21:35:52');
INSERT INTO `course` VALUES (2, '成人篮球入门班', 'COU002BASIC02', 1, 8, 15, 8, 1500.00, 1200.00, 90, 10, 1, '18-45岁', '专为零基础成人设计的篮球课程。轻松愉快的教学氛围，注重实战和趣味性，帮助成人快速掌握篮球基本技能。', '[\"第1-2课：篮球基础和体能准备\",\"第3-4课：运球和传球基础\",\"第5-6课：投篮技术入门\",\"第7-8课：基础战术配合\",\"第9-10课：实战对抗训练\"]', 'https://images.unsplash.com/photo-1608245449230-4ac19066d2d0?w=600&h=400&fit=crop', NULL, 1, 89, 28, 4.60, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (3, '少儿篮球启蒙班', 'COU003BASIC03', 1, 7, 15, 8, 800.00, 680.00, 60, 16, 1, '6-9岁', '针对学龄前和小学低年级儿童的篮球启蒙课程。通过游戏化教学，培养孩子对篮球的兴趣，发展基础运动能力。', '[\"第1-4课：球性培养和协调性训练\",\"第5-8课：基础运球游戏\",\"第9-12课：传接球趣味练习\",\"第13-16课：简单投篮和团队游戏\"]', 'https://images.unsplash.com/photo-1519766304817-4f37bda74a26?w=600&h=400&fit=crop', NULL, 1, 206, 56, 4.90, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (4, '青少年技能提高班', 'COU004IMPROVE01', 2, 5, 18, 10, 1800.00, 1500.00, 90, 12, 2, '12-17岁', '面向有一定基础的青少年学员。深化技术训练，强化战术意识，提升比赛能力。', '[\"第1-3课：高级运球技术\",\"第4-6课：各种投篮技巧\",\"第7-9课：防守技术和战术\",\"第10-12课：进攻战术和实战演练\"]', 'https://images.unsplash.com/photo-1517649763962-0c623066013b?w=600&h=400&fit=crop', NULL, 1, 124, 38, 4.70, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (5, '成人技能提升班', 'COU005IMPROVE02', 2, 8, 12, 6, 2000.00, 1600.00, 90, 10, 2, '20-50岁', '针对有基础的成人学员，提升个人技术水平和战术理解。课程强度适中，注重技战术结合。', '[\"第1-2课：技术动作优化\",\"第3-4课：战术意识培养\",\"第5-6课：位置技术训练\",\"第7-8课：团队配合演练\",\"第9-10课：对抗实战\"]', 'https://images.unsplash.com/photo-1504450758481-7338eba7524a?w=600&h=400&fit=crop', NULL, 1, 67, 22, 4.50, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (6, '投篮专项提升班', 'COU006IMPROVE03', 2, 6, 15, 8, 1600.00, 1300.00, 75, 12, 2, '12岁以上', '专注于投篮技术的提升课程。系统训练各种投篮技术，提高命中率。适合想要强化投篮能力的学员。', '[\"第1-2课：投篮基础动作矫正\",\"第3-4课：定点投篮训练\",\"第5-6课：运动中投篮\",\"第7-8课：三分球技术\",\"第9-10课：对抗下的投篮\",\"第11-12课：实战投篮演练\"]', 'https://images.unsplash.com/photo-1515523110800-9415d13b84a8?w=600&h=400&fit=crop', NULL, 1, 145, 42, 4.80, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (7, '青少年职业预备班', 'COU007PRO01', 3, 6, 12, 8, 3500.00, 3000.00, 120, 20, 4, '14-18岁', '面向有志于走职业道路的优秀青少年球员。高强度专业训练，全面提升技战术水平，培养职业素养。', '[\"第1-5课：职业级基本功强化\",\"第6-10课：高级战术体系\",\"第11-15课：位置专项训练\",\"第16-20课：实战对抗和心理训练\"]', 'https://images.unsplash.com/photo-1529654423582-310c920228e1?w=600&h=400&fit=crop', NULL, 1, 78, 18, 4.90, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (8, '战术分析精英班', 'COU008PRO02', 3, 6, 10, 6, 2800.00, 2400.00, 90, 10, 3, '16岁以上', '深度战术分析课程，适合有较高水平的学员。通过视频分析、战术演练等方式，提升战术理解和执行能力。', '[\"第1-2课：现代篮球战术体系\",\"第3-4课：进攻战术详解\",\"第5-6课：防守战术体系\",\"第7-8课：战术对抗演练\",\"第9-10课：比赛战术应用\"]', 'https://images.unsplash.com/photo-1627627256672-027a4613d028?w=600&h=400&fit=crop', NULL, 1, 52, 15, 4.70, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (9, '防守技术强化班', 'COU009PRO03', 3, 6, 15, 8, 2200.00, 1800.00, 75, 12, 3, '14岁以上', '专注于防守技术的专业课程。系统学习个人防守、团队防守技术，打造铁血防线。', '[\"第1-3课：个人防守基础\",\"第4-6课：各种防守步伐\",\"第7-9课：团队防守配合\",\"第10-12课：防守战术体系\"]', 'https://images.unsplash.com/photo-1546519638-68e109498ffc?w=600&h=400&fit=crop', NULL, 1, 63, 19, 4.60, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (10, '一对一定制私教课', 'COU010PRIVATE01', 4, 5, 1, 1, 400.00, 350.00, 60, 1, 2, '不限', '一对一私人教练课程，根据学员具体情况定制训练计划。时间灵活，针对性强，效果显著。', '[\"根据学员水平量身定制\",\"一对一指导纠正动作\",\"灵活安排训练时间\",\"快速提升个人能力\"]', 'https://images.unsplash.com/photo-1608245449230-4ac19066d2d0?w=600&h=400&fit=crop', NULL, 1, 234, 89, 4.90, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (11, '投篮技术私教课', 'COU011PRIVATE02', 4, 6, 1, 1, 500.00, 450.00, 60, 1, 2, '10岁以上', '专注投篮技术的一对一私教。由投篮专家亲自指导，针对性解决投篮问题，快速提升命中率。', '[\"投篮动作诊断和矫正\",\"个性化投篮训练方案\",\"专业投篮技术指导\",\"投篮心理辅导\"]', 'https://images.unsplash.com/photo-1519766304817-4f37bda74a26?w=600&h=400&fit=crop', NULL, 1, 179, 67, 4.80, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course` VALUES (12, '体能提升私教课', 'COU012PRIVATE03', 4, 7, 1, 1, 350.00, 300.00, 60, 1, 2, '12岁以上', '专业体能训练私教课。提升力量、速度、耐力、爆发力等综合运动能力，预防运动损伤。', '[\"体能测试和评估\",\"个性化体能训练计划\",\"力量和爆发力训练\",\"运动康复指导\"]', 'https://images.unsplash.com/photo-1517649763962-0c623066013b?w=600&h=400&fit=crop', NULL, 1, 142, 53, 4.70, '2025-10-02 20:49:48', '2025-10-02 20:49:48');

-- ----------------------------
-- Table structure for course_enrollment
-- ----------------------------
DROP TABLE IF EXISTS `course_enrollment`;
CREATE TABLE `course_enrollment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `enrollment_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报名编号',
  `schedule_id` bigint NOT NULL COMMENT '排期ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单编号',
  `price` decimal(10, 2) NOT NULL COMMENT '报名价格',
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '支付状态: 0-待支付, 1-已支付, 2-已退款',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `attendance_status` tinyint NULL DEFAULT 0 COMMENT '出勤状态: 0-未签到, 1-已签到, 2-缺席, 3-请假',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `rating` tinyint NULL DEFAULT NULL COMMENT '评分: 1-5星',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `comment_time` datetime NULL DEFAULT NULL COMMENT '评价时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-已取消, 1-正常',
  `enroll_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `enrollment_no`(`enrollment_no` ASC) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_schedule_id`(`schedule_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_pay_status`(`pay_status` ASC) USING BTREE,
  CONSTRAINT `course_enrollment_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `course_schedule` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `course_enrollment_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `course_enrollment_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_enrollment
-- ----------------------------
INSERT INTO `course_enrollment` VALUES (1, 'ENR20251002001', 1, 1, 2, 'ORD20251002001', 1000.00, 1, '2025-10-02 10:30:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-02 10:25:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (2, 'ENR20251002002', 2, 1, 2, 'ORD20251002002', 1000.00, 1, '2025-10-02 10:30:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-02 10:25:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (3, 'ENR20251002003', 5, 2, 2, 'ORD20251002003', 1200.00, 1, '2025-10-02 11:00:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-02 10:58:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (4, 'ENR20251002004', 18, 6, 2, 'ORD20251002004', 1300.00, 1, '2025-10-02 14:20:00', 0, NULL, 5, '王教练讲解很详细，进步明显！', '2025-10-06 18:00:00', 1, '2025-10-02 14:15:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (5, 'ENR20251002005', 8, 3, 3, 'ORD20251002005', 680.00, 1, '2025-10-02 15:00:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-02 14:55:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (6, 'ENR20251002006', 9, 3, 3, 'ORD20251002006', 680.00, 1, '2025-10-02 15:00:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-02 14:55:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (7, 'ENR20251002007', 12, 4, 3, 'ORD20251002007', 1500.00, 0, NULL, 0, NULL, NULL, NULL, NULL, 1, '2025-10-02 16:20:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (8, 'ENR20251002008', 30, 10, 3, 'ORD20251002008', 350.00, 1, '2025-10-03 09:00:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-03 08:55:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (9, 'ENR20251002009', 8, 3, 4, 'ORD20251002009', 800.00, 1, '2025-10-02 16:30:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-02 16:25:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (10, 'ENR20251002010', 15, 5, 4, 'ORD20251002010', 2000.00, 0, NULL, 0, NULL, NULL, NULL, NULL, 1, '2025-10-03 10:00:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (11, 'ENR20251002011', 1, 1, 2, 'ORD20251002011', 1000.00, 1, '2025-10-02 12:00:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-02 11:55:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (12, 'ENR20251002012', 22, 7, 3, 'ORD20251002012', 3000.00, 1, '2025-10-03 14:00:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-03 13:50:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (13, 'ENR20251002013', 25, 8, 2, 'ORD20251002013', 2400.00, 1, '2025-10-03 15:30:00', 0, NULL, 4, '战术分析很专业，收获很大', '2025-10-08 12:00:00', 1, '2025-10-03 15:25:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (14, 'ENR20251002014', 27, 9, 3, 'ORD20251002014', 1800.00, 0, NULL, 0, NULL, NULL, NULL, NULL, 1, '2025-10-03 16:00:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (15, 'ENR20251002015', 18, 6, 4, 'ORD20251002015', 1600.00, 1, '2025-10-03 17:00:00', 0, NULL, NULL, NULL, NULL, 1, '2025-10-03 16:55:00', '2025-10-02 20:49:48');
INSERT INTO `course_enrollment` VALUES (16, 'CE1760929338001', 1, 1, 9, 'ORD1760929338001', 500.00, 1, '2025-10-13 09:05:00', 1, NULL, 5, '课程很好', NULL, 1, '2025-10-13 09:00:00', '2025-10-20 11:02:18');
INSERT INTO `course_enrollment` VALUES (17, 'CE1760929338002', 2, 2, 9, 'ORD1760929338002', 800.00, 1, '2025-10-15 14:35:00', 1, NULL, 5, '教练专业', NULL, 1, '2025-10-15 14:30:00', '2025-10-20 11:02:18');
INSERT INTO `course_enrollment` VALUES (18, 'CE1760929338003', 3, 3, 9, 'ORD1760929338003', 1200.00, 1, '2025-10-17 09:35:00', 1, NULL, 4, '内容丰富', NULL, 1, '2025-10-17 09:30:00', '2025-10-20 11:02:18');

-- ----------------------------
-- Table structure for course_popularity_analysis
-- ----------------------------
DROP TABLE IF EXISTS `course_popularity_analysis`;
CREATE TABLE `course_popularity_analysis`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分析ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `analysis_date` date NOT NULL COMMENT '分析日期',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `view_user_count` int NULL DEFAULT 0 COMMENT '浏览人数(去重)',
  `enroll_count` int NULL DEFAULT 0 COMMENT '报名次数',
  `enroll_user_count` int NULL DEFAULT 0 COMMENT '报名人数(去重)',
  `completion_count` int NULL DEFAULT 0 COMMENT '完成人数',
  `avg_rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '平均评分(0-5分)',
  `rating_count` int NULL DEFAULT 0 COMMENT '评价数量',
  `revenue` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '收入金额',
  `popularity_score` int NULL DEFAULT 0 COMMENT '热度评分(0-100)',
  `ranking` int NULL DEFAULT NULL COMMENT '排名',
  `trend` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '趋势: up-上升, down-下降, stable-稳定',
  `trend_change` int NULL DEFAULT 0 COMMENT '趋势变化值',
  `conversion_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '转化率(%)',
  `completion_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '完成率(%)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_course_date`(`course_id` ASC, `analysis_date` ASC) USING BTREE,
  INDEX `idx_analysis_date`(`analysis_date` ASC) USING BTREE,
  INDEX `idx_popularity`(`popularity_score` DESC) USING BTREE,
  INDEX `idx_ranking`(`ranking` ASC) USING BTREE,
  CONSTRAINT `course_popularity_analysis_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程热度分析表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_popularity_analysis
-- ----------------------------

-- ----------------------------
-- Table structure for course_schedule
-- ----------------------------
DROP TABLE IF EXISTS `course_schedule`;
CREATE TABLE `course_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排期ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `venue_id` bigint NOT NULL COMMENT '场地ID',
  `schedule_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '排期编号',
  `schedule_date` date NOT NULL COMMENT '上课日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `max_students` int NOT NULL COMMENT '最大人数',
  `enrolled_count` int NULL DEFAULT 0 COMMENT '已报名人数',
  `checked_in_count` int NULL DEFAULT 0 COMMENT '已签到人数',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-未开始, 1-报名中, 2-已满员, 3-进行中, 4-已结束, 5-已取消',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `schedule_no`(`schedule_no` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_venue_id`(`venue_id` ASC) USING BTREE,
  INDEX `idx_date`(`schedule_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `course_schedule_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `course_schedule_ibfk_2` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程排期表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_schedule
-- ----------------------------
INSERT INTO `course_schedule` VALUES (1, 1, 3, 'SCH20251005001', '2025-10-05', '14:00:00', '15:30:00', 20, 8, 0, 1, NULL, '第1课：篮球基础知识', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (2, 1, 3, 'SCH20251007001', '2025-10-07', '14:00:00', '15:30:00', 20, 8, 0, 1, NULL, '第2课：规则讲解', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (3, 1, 3, 'SCH20251009001', '2025-10-09', '14:00:00', '15:30:00', 20, 8, 0, 1, NULL, '第3课：运球基础', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (4, 1, 3, 'SCH20251011001', '2025-10-11', '14:00:00', '15:30:00', 20, 8, 0, 1, NULL, '第4课：运球进阶', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (5, 2, 4, 'SCH20251005002', '2025-10-05', '19:00:00', '20:30:00', 15, 5, 0, 1, NULL, '第1课：基础入门', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (6, 2, 4, 'SCH20251008002', '2025-10-08', '19:00:00', '20:30:00', 15, 5, 0, 1, NULL, '第2课：体能训练', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (7, 2, 4, 'SCH20251010002', '2025-10-10', '19:00:00', '20:30:00', 15, 5, 0, 1, NULL, '第3课：运球传球', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (8, 3, 4, 'SCH20251006003', '2025-10-06', '16:00:00', '17:00:00', 15, 12, 0, 1, NULL, '第1课：球性培养', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (9, 3, 4, 'SCH20251008003', '2025-10-08', '16:00:00', '17:00:00', 15, 12, 0, 1, NULL, '第2课：协调性训练', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (10, 3, 4, 'SCH20251010003', '2025-10-10', '16:00:00', '17:00:00', 15, 12, 0, 1, NULL, '第3课：运球游戏', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (11, 3, 4, 'SCH20251012003', '2025-10-12', '16:00:00', '17:00:00', 15, 12, 0, 1, NULL, '第4课：传接球游戏', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (12, 4, 3, 'SCH20251006004', '2025-10-06', '14:00:00', '15:30:00', 18, 10, 0, 1, NULL, '第1课：高级运球', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (13, 4, 3, 'SCH20251009004', '2025-10-09', '14:00:00', '15:30:00', 18, 10, 0, 1, NULL, '第2课：变向技术', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (14, 4, 3, 'SCH20251011004', '2025-10-11', '14:00:00', '15:30:00', 18, 10, 0, 1, NULL, '第3课：背后运球', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (15, 5, 5, 'SCH20251007005', '2025-10-07', '19:30:00', '21:00:00', 12, 6, 0, 1, NULL, '第1课：技术优化', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (16, 5, 5, 'SCH20251010005', '2025-10-10', '19:30:00', '21:00:00', 12, 6, 0, 1, NULL, '第2课：战术意识', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (17, 5, 5, 'SCH20251012005', '2025-10-12', '19:30:00', '21:00:00', 12, 6, 0, 1, NULL, '第3课：位置训练', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (18, 6, 3, 'SCH20251005006', '2025-10-05', '16:00:00', '17:15:00', 15, 8, 0, 1, NULL, '第1课：投篮基础', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (19, 6, 3, 'SCH20251007006', '2025-10-07', '16:00:00', '17:15:00', 15, 8, 0, 1, NULL, '第2课：动作矫正', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (20, 6, 3, 'SCH20251009006', '2025-10-09', '16:00:00', '17:15:00', 15, 8, 0, 1, NULL, '第3课：定点投篮', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (21, 6, 3, 'SCH20251011006', '2025-10-11', '16:00:00', '17:15:00', 15, 8, 0, 1, NULL, '第4课：移动投篮', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (22, 7, 1, 'SCH20251006007', '2025-10-06', '09:00:00', '11:00:00', 12, 4, 0, 1, NULL, '第1课：职业基本功', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (23, 7, 1, 'SCH20251009007', '2025-10-09', '09:00:00', '11:00:00', 12, 4, 0, 1, NULL, '第2课：强化训练', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (24, 7, 1, 'SCH20251011007', '2025-10-11', '09:00:00', '11:00:00', 12, 4, 0, 1, NULL, '第3课：专项技术', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (25, 8, 2, 'SCH20251007008', '2025-10-07', '10:00:00', '11:30:00', 10, 3, 0, 1, NULL, '第1课：战术体系', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (26, 8, 2, 'SCH20251010008', '2025-10-10', '10:00:00', '11:30:00', 10, 3, 0, 1, NULL, '第2课：进攻战术', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (27, 9, 2, 'SCH20251008009', '2025-10-08', '15:00:00', '16:15:00', 15, 5, 0, 1, NULL, '第1课：个人防守', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (28, 9, 2, 'SCH20251010009', '2025-10-10', '15:00:00', '16:15:00', 15, 5, 0, 1, NULL, '第2课：防守步伐', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (29, 9, 2, 'SCH20251012009', '2025-10-12', '15:00:00', '16:15:00', 15, 5, 0, 1, NULL, '第3课：团队防守', '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `course_schedule` VALUES (30, 10, 3, 'SCH20251014010', '2025-10-14', '10:00:00', '11:00:00', 1, 1, 0, 1, NULL, '一对一私教', '2025-10-02 20:49:48', '2025-10-02 20:49:48');

-- ----------------------------
-- Table structure for dictionary
-- ----------------------------
DROP TABLE IF EXISTS `dictionary`;
CREATE TABLE `dictionary`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典类型',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典值',
  `dict_sort` int NULL DEFAULT 0 COMMENT '排序',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父级ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dictionary
-- ----------------------------
INSERT INTO `dictionary` VALUES (1, 'venue_type', '室内全场', '1', 1, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (2, 'venue_type', '室内半场', '2', 2, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (3, 'venue_type', '室外全场', '3', 3, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (4, 'venue_type', '室外半场', '4', 4, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (5, 'booking_status', '待支付', '0', 1, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (6, 'booking_status', '已支付', '1', 2, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (7, 'booking_status', '已取消', '2', 3, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (8, 'booking_status', '已完成', '3', 4, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (9, 'booking_status', '已退款', '4', 5, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (10, 'course_type', '基础班', '1', 1, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (11, 'course_type', '提高班', '2', 2, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (12, 'course_type', '专业班', '3', 3, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (13, 'course_type', '私教课', '4', 4, 0, 1, NULL, '2025-10-01 00:25:39');
INSERT INTO `dictionary` VALUES (14, 'venue_type', '室内全场', '1', 1, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (15, 'venue_type', '室内半场', '2', 2, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (16, 'venue_type', '室外全场', '3', 3, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (17, 'venue_type', '室外半场', '4', 4, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (18, 'venue_status', '不可用', '0', 1, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (19, 'venue_status', '可用', '1', 2, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (20, 'venue_status', '维护中', '2', 3, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (21, 'booking_status', '待支付', '0', 1, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (22, 'booking_status', '已支付', '1', 2, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (23, 'booking_status', '已取消', '2', 3, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (24, 'booking_status', '已完成', '3', 4, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (25, 'booking_status', '已退款', '4', 5, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (26, 'booking_status', '超时取消', '5', 6, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (27, 'course_type', '基础班', '1', 1, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (28, 'course_type', '提高班', '2', 2, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (29, 'course_type', '专业班', '3', 3, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (30, 'course_type', '私教课', '4', 4, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (31, 'time_type', '工作日', '1', 1, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (32, 'time_type', '周末', '2', 2, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (33, 'time_type', '节假日', '3', 3, 0, 1, NULL, '2025-10-02 14:35:38');
INSERT INTO `dictionary` VALUES (34, 'venue_type', '室内全场', '1', 1, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (35, 'venue_type', '室内半场', '2', 2, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (36, 'venue_type', '室外全场', '3', 3, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (37, 'venue_type', '室外半场', '4', 4, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (38, 'venue_status', '不可用', '0', 1, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (39, 'venue_status', '可用', '1', 2, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (40, 'venue_status', '维护中', '2', 3, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (41, 'booking_status', '待支付', '0', 1, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (42, 'booking_status', '已支付', '1', 2, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (43, 'booking_status', '已取消', '2', 3, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (44, 'booking_status', '已完成', '3', 4, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (45, 'booking_status', '已退款', '4', 5, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (46, 'booking_status', '超时取消', '5', 6, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (47, 'course_type', '基础班', '1', 1, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (48, 'course_type', '提高班', '2', 2, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (49, 'course_type', '专业班', '3', 3, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (50, 'course_type', '私教课', '4', 4, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (51, 'time_type', '工作日', '1', 1, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (52, 'time_type', '周末', '2', 2, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (53, 'time_type', '节假日', '3', 3, 0, 1, NULL, '2025-10-02 20:42:29');
INSERT INTO `dictionary` VALUES (54, 'venue_type', '室内全场', '1', 1, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (55, 'venue_type', '室内半场', '2', 2, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (56, 'venue_type', '室外全场', '3', 3, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (57, 'venue_type', '室外半场', '4', 4, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (58, 'venue_status', '不可用', '0', 1, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (59, 'venue_status', '可用', '1', 2, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (60, 'venue_status', '维护中', '2', 3, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (61, 'venue_status', '已删除', '3', 4, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (62, 'booking_status', '待支付', '0', 1, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (63, 'booking_status', '已支付', '1', 2, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (64, 'booking_status', '已取消', '2', 3, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (65, 'booking_status', '已完成', '3', 4, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (66, 'booking_status', '已退款', '4', 5, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (67, 'booking_status', '超时取消', '5', 6, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (68, 'course_type', '基础班', '1', 1, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (69, 'course_type', '提高班', '2', 2, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (70, 'course_type', '专业班', '3', 3, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (71, 'course_type', '私教课', '4', 4, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (72, 'difficulty_level', '入门', '1', 1, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (73, 'difficulty_level', '初级', '2', 2, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (74, 'difficulty_level', '中级', '3', 3, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (75, 'difficulty_level', '高级', '4', 4, 0, 1, NULL, '2025-10-02 20:49:48');
INSERT INTO `dictionary` VALUES (76, 'user_status', '正常', '1', 1, 0, 1, '用户正常状态', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (77, 'user_status', '禁用', '2', 2, 0, 1, '用户禁用状态', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (78, 'booking_status', '待支付', '0', 1, 0, 1, '预订待支付', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (79, 'booking_status', '已支付', '1', 2, 0, 1, '预订已支付', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (80, 'booking_status', '已取消', '2', 3, 0, 1, '预订已取消', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (81, 'booking_status', '已完成', '3', 4, 0, 1, '预订已完成', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (82, 'payment_method', '微信支付', 'wechat', 1, 0, 1, '微信支付方式', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (83, 'payment_method', '支付宝', 'alipay', 2, 0, 1, '支付宝支付方式', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (84, 'payment_method', '现金支付', 'cash', 3, 0, 1, '现金支付方式', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (85, 'venue_type', '标准篮球场', 'standard', 1, 0, 1, '标准篮球场地', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (86, 'venue_type', 'VIP篮球场', 'vip', 2, 0, 1, 'VIP篮球场地', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (87, 'venue_type', '综合运动场', 'complex', 3, 0, 1, '综合运动场地', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (88, 'member_type', '普通会员', 'normal', 1, 0, 1, '普通会员类型', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (89, 'member_type', '银卡会员', 'silver', 2, 0, 1, '银卡会员类型', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (90, 'member_type', '金卡会员', 'gold', 3, 0, 1, '金卡会员类型', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (91, 'member_type', '钻石会员', 'diamond', 4, 0, 1, '钻石会员类型', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (92, 'log_type', '登录日志', 'login', 1, 0, 1, '用户登录日志', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (93, 'log_type', '操作日志', 'operation', 2, 0, 1, '用户操作日志', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (94, 'log_type', '预订日志', 'booking', 3, 0, 1, '预订相关日志', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (95, 'log_type', '支付日志', 'payment', 4, 0, 1, '支付相关日志', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (96, 'config_type', '字符串', '1', 1, 0, 1, '字符串类型配置', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (97, 'config_type', '数字', '2', 2, 0, 1, '数字类型配置', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (98, 'config_type', '布尔值', '3', 3, 0, 1, '布尔类型配置', '2025-10-20 17:55:13');
INSERT INTO `dictionary` VALUES (99, 'config_type', 'JSON', '4', 4, 0, 1, 'JSON类型配置', '2025-10-20 17:55:13');

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `venue_id` bigint NULL DEFAULT NULL COMMENT '所属场地ID(为空则为公共设备)',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备名称',
  `equipment_type` tinyint NULL DEFAULT NULL COMMENT '设备类型: 1-篮球, 2-计时器, 3-灯光, 4-空调, 5-其他',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-损坏, 1-正常, 2-维修中',
  `purchase_date` date NULL DEFAULT NULL COMMENT '购买日期',
  `last_maintenance_date` date NULL DEFAULT NULL COMMENT '最后维护日期',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id`(`venue_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `equipment_ibfk_1` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment
-- ----------------------------

-- ----------------------------
-- Table structure for financial_record
-- ----------------------------
DROP TABLE IF EXISTS `financial_record`;
CREATE TABLE `financial_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `record_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流水号',
  `record_type` tinyint NOT NULL COMMENT '类型: 1-收入, 2-支出, 3-退款',
  `business_type` tinyint NULL DEFAULT NULL COMMENT '业务类型: 1-场地, 2-课程, 3-会员卡, 4-其他',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联订单号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `balance_before` decimal(10, 2) NULL DEFAULT NULL COMMENT '变动前余额',
  `balance_after` decimal(10, 2) NULL DEFAULT NULL COMMENT '变动后余额',
  `payment_method` tinyint NULL DEFAULT NULL COMMENT '支付方式',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `record_no`(`record_no` ASC) USING BTREE,
  INDEX `idx_record_no`(`record_no` ASC) USING BTREE,
  INDEX `idx_type`(`record_type` ASC, `business_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of financial_record
-- ----------------------------
INSERT INTO `financial_record` VALUES (1, 'REC202510200001', 1, 3, 'ORD202510200001', 1, 500.00, 0.00, 500.00, 1, '用户充值会员卡500元', 1, '2025-10-20 18:17:03');
INSERT INTO `financial_record` VALUES (2, 'REC202510200002', 1, 1, 'ORD202510200002', 2, 200.00, 0.00, 200.00, 1, '预订标准场地2小时费用', 2, '2025-10-20 18:17:03');
INSERT INTO `financial_record` VALUES (3, 'REC202510200003', 1, 2, 'ORD202510200003', 3, 300.00, 0.00, 300.00, 1, '预约篮球课程1小时费用', 3, '2025-10-20 18:17:03');
INSERT INTO `financial_record` VALUES (4, 'REC202510200004', 1, 3, 'ORD202510200004', 4, 1000.00, 0.00, 1000.00, 1, '用户续费会员卡1000元', 4, '2025-10-20 18:17:03');
INSERT INTO `financial_record` VALUES (5, 'REC202510200005', 1, 4, 'ORD202510200005', 5, 800.00, 0.00, 800.00, 1, '预约教练培训课程费用', 5, '2025-10-20 18:17:03');
INSERT INTO `financial_record` VALUES (6, 'REC202510200006', 3, 1, 'ORD202510200006', 2, -150.00, 200.00, 50.00, 1, '用户取消预订退款', 2, '2025-10-20 18:17:03');
INSERT INTO `financial_record` VALUES (7, 'REC202510200007', 2, 4, 'ORD202510200007', 1, -500.00, 800.00, 300.00, 3, '场地设备维护费用', 1, '2025-10-20 18:17:03');
INSERT INTO `financial_record` VALUES (8, 'REC202510200008', 2, 4, 'ORD202510200008', 1, -200.00, 300.00, 100.00, 3, '购买运动器材用品', 1, '2025-10-20 18:17:03');
INSERT INTO `financial_record` VALUES (9, 'REC202510200009', 2, 4, 'ORD202510200009', 1, -300.00, 100.00, -200.00, 3, '推广营销活动费用', 1, '2025-10-20 18:17:03');
INSERT INTO `financial_record` VALUES (10, 'REC202510200010', 2, 3, 'ORD202510200010', 1, -50.00, -200.00, -250.00, 3, '会员卡制作费用', 1, '2025-10-20 18:17:03');

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `login_type` tinyint NULL DEFAULT NULL COMMENT '登录类型: 1-账号密码, 2-手机验证码, 3-第三方',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态: 0-失败, 1-成功',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提示信息',
  `login_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_login_time`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_log
-- ----------------------------

-- ----------------------------
-- Table structure for member_activity_analysis
-- ----------------------------
DROP TABLE IF EXISTS `member_activity_analysis`;
CREATE TABLE `member_activity_analysis`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分析ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `analysis_date` date NOT NULL COMMENT '分析日期',
  `login_count` int NULL DEFAULT 0 COMMENT '登录次数',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `booking_count` int NULL DEFAULT 0 COMMENT '预订次数',
  `payment_count` int NULL DEFAULT 0 COMMENT '支付次数',
  `payment_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付金额',
  `cancel_count` int NULL DEFAULT 0 COMMENT '取消次数',
  `activity_score` int NULL DEFAULT 0 COMMENT '活跃度评分(0-100)',
  `last_active_time` datetime NULL DEFAULT NULL COMMENT '最后活跃时间',
  `continuous_days` int NULL DEFAULT 0 COMMENT '连续活跃天数',
  `rfm_r` int NULL DEFAULT 0 COMMENT 'RFM-R值(Recency)',
  `rfm_f` int NULL DEFAULT 0 COMMENT 'RFM-F值(Frequency)',
  `rfm_m` decimal(10, 2) NULL DEFAULT 0.00 COMMENT 'RFM-M值(Monetary)',
  `rfm_score` int NULL DEFAULT 0 COMMENT 'RFM总分',
  `churn_risk` tinyint NULL DEFAULT 0 COMMENT '流失风险: 0-无风险, 1-低风险, 2-中风险, 3-高风险',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `analysis_date` ASC) USING BTREE,
  INDEX `idx_analysis_date`(`analysis_date` ASC) USING BTREE,
  INDEX `idx_activity_score`(`activity_score` ASC) USING BTREE,
  INDEX `idx_churn_risk`(`churn_risk` ASC) USING BTREE,
  CONSTRAINT `member_activity_analysis_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员活跃度分析表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_activity_analysis
-- ----------------------------

-- ----------------------------
-- Table structure for member_card
-- ----------------------------
DROP TABLE IF EXISTS `member_card`;
CREATE TABLE `member_card`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会员卡ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `card_type_id` bigint NOT NULL COMMENT '卡类型ID',
  `card_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '卡号',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '余额(储值卡)',
  `remaining_times` int NULL DEFAULT NULL COMMENT '剩余次数(次卡)',
  `start_date` date NULL DEFAULT NULL COMMENT '生效日期',
  `expire_date` date NULL DEFAULT NULL COMMENT '到期日期',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-未激活, 1-有效, 2-已过期, 3-已冻结, 4-已注销',
  `activate_time` datetime NULL DEFAULT NULL COMMENT '激活时间',
  `freeze_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '冻结原因',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `card_no`(`card_no` ASC) USING BTREE,
  INDEX `card_type_id`(`card_type_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_card_no`(`card_no` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_expire_date`(`expire_date` ASC) USING BTREE,
  CONSTRAINT `member_card_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `member_card_ibfk_2` FOREIGN KEY (`card_type_id`) REFERENCES `member_card_type` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员卡表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_card
-- ----------------------------
INSERT INTO `member_card` VALUES (1, 2, 1, 'CARD1759414552251617', 0.00, NULL, '2025-10-02', '2025-11-01', 0, NULL, NULL, NULL, '2025-10-02 22:15:52', '2025-10-02 22:15:52');

-- ----------------------------
-- Table structure for member_card_record
-- ----------------------------
DROP TABLE IF EXISTS `member_card_record`;
CREATE TABLE `member_card_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `card_id` bigint NOT NULL COMMENT '会员卡ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `record_type` tinyint NOT NULL COMMENT '记录类型: 1-充值, 2-消费, 3-退款, 4-赠送',
  `change_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '金额变动',
  `change_times` int NULL DEFAULT 0 COMMENT '次数变动',
  `balance_before` decimal(10, 2) NULL DEFAULT NULL COMMENT '变动前余额',
  `balance_after` decimal(10, 2) NULL DEFAULT NULL COMMENT '变动后余额',
  `times_before` int NULL DEFAULT NULL COMMENT '变动前次数',
  `times_after` int NULL DEFAULT NULL COMMENT '变动后次数',
  `related_order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联订单号',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_card_id`(`card_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `member_card_record_ibfk_1` FOREIGN KEY (`card_id`) REFERENCES `member_card` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `member_card_record_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员卡使用记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_card_record
-- ----------------------------
INSERT INTO `member_card_record` VALUES (1, 1, 2, 1, 299.00, 0, NULL, NULL, NULL, NULL, NULL, '购买会员卡', '2025-10-02 22:15:52');

-- ----------------------------
-- Table structure for member_card_type
-- ----------------------------
DROP TABLE IF EXISTS `member_card_type`;
CREATE TABLE `member_card_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '卡类型ID',
  `card_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '卡名称',
  `card_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '卡代码',
  `card_type` tinyint NOT NULL COMMENT '卡类型: 1-时间卡(月/季/年), 2-次卡, 3-储值卡',
  `duration` int NULL DEFAULT NULL COMMENT '有效期(天)',
  `times` int NULL DEFAULT NULL COMMENT '可用次数',
  `price` decimal(10, 2) NOT NULL COMMENT '售价',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `discount` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '折扣率: 0.90表示9折',
  `member_level` int NULL DEFAULT 1 COMMENT '对应会员等级',
  `benefits` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '权益说明(JSON格式)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `sort_order` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `card_code`(`card_code` ASC) USING BTREE,
  INDEX `idx_card_type`(`card_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员卡类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_card_type
-- ----------------------------
INSERT INTO `member_card_type` VALUES (1, '月卡会员', 'MONTH_CARD', 1, 30, NULL, 299.00, 399.00, 0.90, 1, '[\"场地预订9折优惠\",\"课程报名9.5折优惠\",\"每月赠送200积分\",\"优先预订权\"]', '有效期30天，享受会员专属优惠', 1, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `member_card_type` VALUES (2, '季卡会员', 'SEASON_CARD', 1, 90, NULL, 799.00, 1197.00, 0.85, 2, '[\"场地预订8.5折优惠\",\"课程报名9折优惠\",\"每月赠送500积分\",\"优先预订权\",\"生日专属福利\"]', '有效期90天，性价比之选', 2, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `member_card_type` VALUES (3, '年卡会员', 'YEAR_CARD', 1, 365, NULL, 2999.00, 4788.00, 0.80, 3, '[\"场地预订8折优惠\",\"课程报名8.5折优惠\",\"每月赠送1000积分\",\"最高优先预订权\",\"生日专属福利\",\"会员专属活动\"]', '有效期365天，尊享全年优惠', 3, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `member_card_type` VALUES (4, '10次卡', 'TIMES_10', 2, 180, 10, 499.00, 600.00, 0.90, 1, '[\"10次场地使用机会\",\"半年有效期\",\"场地预订9折优惠\"]', '180天内可使用10次，灵活便捷', 4, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `member_card_type` VALUES (5, '30次卡', 'TIMES_30', 2, 365, 30, 1299.00, 1800.00, 0.85, 2, '[\"30次场地使用机会\",\"一年有效期\",\"场地预订8.5折优惠\",\"赠送3次课程体验\"]', '一年内可使用30次，更多优惠', 5, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `member_card_type` VALUES (6, '储值卡', 'STORED_VALUE', 3, 365, NULL, 1000.00, 1000.00, 0.90, 1, '[\"充值1000送100\",\"场地预订9折优惠\",\"余额可用于场地和课程\"]', '储值消费，多充多送', 6, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');

-- ----------------------------
-- Table structure for notification_record
-- ----------------------------
DROP TABLE IF EXISTS `notification_record`;
CREATE TABLE `notification_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板ID',
  `template_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板编码',
  `notification_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型: sms/wechat/system/email',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `target` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送目标(手机号/openId/邮箱)',
  `biz_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
  `biz_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务ID',
  `send_status` tinyint NULL DEFAULT 0 COMMENT '发送状态: 0-待发送, 1-发送中, 2-已发送, 3-失败',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `send_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '发送结果',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `retry_count` int NULL DEFAULT 0 COMMENT '重试次数',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读(站内消息): 0-未读, 1-已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '额外数据(JSON)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_read`(`user_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_send_status`(`send_status` ASC) USING BTREE,
  INDEX `idx_send_time`(`send_time` ASC) USING BTREE,
  INDEX `idx_biz`(`biz_type` ASC, `biz_id` ASC) USING BTREE,
  INDEX `idx_template`(`template_id` ASC) USING BTREE,
  INDEX `idx_notification_user_type_status`(`user_id` ASC, `notification_type` ASC, `send_status` ASC) USING BTREE,
  CONSTRAINT `notification_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification_record
-- ----------------------------
INSERT INTO `notification_record` VALUES (1, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A1专业篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '12', 2, '2025-10-18 03:14:42', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-18 03:14:42', '2025-10-18 03:14:42');
INSERT INTO `notification_record` VALUES (2, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A1专业篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '13', 2, '2025-10-18 06:37:32', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-18 06:37:32', '2025-10-18 06:37:32');
INSERT INTO `notification_record` VALUES (3, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:00:42', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:00:42', '2025-10-19 00:00:42');
INSERT INTO `notification_record` VALUES (4, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:00:53', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:00:53', '2025-10-19 00:00:53');
INSERT INTO `notification_record` VALUES (5, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:06:18', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:06:18', '2025-10-19 00:06:18');
INSERT INTO `notification_record` VALUES (6, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:21:49', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:21:49', '2025-10-19 00:21:49');
INSERT INTO `notification_record` VALUES (7, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:22:17', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:22:17', '2025-10-19 00:22:17');
INSERT INTO `notification_record` VALUES (8, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:22:18', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:22:18', '2025-10-19 00:22:18');
INSERT INTO `notification_record` VALUES (9, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:22:22', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:22:22', '2025-10-19 00:22:22');
INSERT INTO `notification_record` VALUES (10, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:22:22', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:22:22', '2025-10-19 00:22:22');
INSERT INTO `notification_record` VALUES (11, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:22:22', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:22:22', '2025-10-19 00:22:22');
INSERT INTO `notification_record` VALUES (12, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:22:23', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:22:23', '2025-10-19 00:22:23');
INSERT INTO `notification_record` VALUES (13, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '14', 2, '2025-10-19 00:22:25', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:22:25', '2025-10-19 00:22:25');
INSERT INTO `notification_record` VALUES (14, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '15', 2, '2025-10-19 00:29:45', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:29:45', '2025-10-19 00:29:45');
INSERT INTO `notification_record` VALUES (15, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '15', 2, '2025-10-19 00:37:56', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:37:56', '2025-10-19 00:37:56');
INSERT INTO `notification_record` VALUES (16, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{A2标准篮球场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '15', 2, '2025-10-19 00:50:18', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:50:18', '2025-10-19 00:50:18');
INSERT INTO `notification_record` VALUES (17, 9, 7, 'PAYMENT_SUCCESS', 'system', '支付成功', '支付成功!金额:¥{1000.00},订单号:{{orderNo}}', '9', 'booking', 'BK1760804982329', 2, '2025-10-19 00:50:20', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:50:20', '2025-10-19 00:50:20');
INSERT INTO `notification_record` VALUES (18, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{B1训练半场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '16', 2, '2025-10-19 00:50:57', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 00:50:57', '2025-10-19 00:50:57');
INSERT INTO `notification_record` VALUES (19, 9, 7, 'PAYMENT_SUCCESS', 'system', '支付成功', '支付成功!金额:¥{360.00},订单号:{{orderNo}}', '9', 'booking', 'BK1760806250791', 2, '2025-10-19 00:50:59', '站内信创建成功', NULL, 0, 1, '2025-10-20 09:36:42', NULL, '2025-10-19 00:50:59', '2025-10-19 00:50:59');
INSERT INTO `notification_record` VALUES (20, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{B2青少年训练场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '17', 2, '2025-10-19 01:06:40', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 01:06:40', '2025-10-19 01:06:40');
INSERT INTO `notification_record` VALUES (21, 9, 7, 'PAYMENT_SUCCESS', 'system', '支付成功', '支付成功!金额:¥{110.00},订单号:{{orderNo}}', '9', 'booking', 'BK1760807197060', 2, '2025-10-19 01:06:42', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-19 01:06:42', '2025-10-19 01:06:42');
INSERT INTO `notification_record` VALUES (22, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{B1训练半场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '18', 2, '2025-10-20 09:23:39', '站内信创建成功', NULL, 0, 1, '2025-10-20 09:41:47', NULL, '2025-10-20 09:23:39', '2025-10-20 09:23:39');
INSERT INTO `notification_record` VALUES (23, 9, 7, 'PAYMENT_SUCCESS', 'system', '支付成功', '支付成功!金额:¥{180.00},订单号:{{orderNo}}', '9', 'booking', 'BK1760923408792', 2, '2025-10-20 09:23:45', '站内信创建成功', NULL, 0, 1, '2025-10-20 09:41:19', NULL, '2025-10-20 09:23:45', '2025-10-20 09:23:45');
INSERT INTO `notification_record` VALUES (24, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{B1训练半场},时间:{{bookingTime}},订单号:{{orderNo}}', '9', 'booking', '19', 2, '2025-10-20 09:34:04', '站内信创建成功', NULL, 0, 1, '2025-10-20 09:36:49', NULL, '2025-10-20 09:34:04', '2025-10-20 09:34:04');
INSERT INTO `notification_record` VALUES (25, 9, 7, 'PAYMENT_SUCCESS', 'system', '支付成功', '支付成功!金额:¥{120.00},订单号:{{orderNo}}', '9', 'booking', 'BK1760923670119', 2, '2025-10-20 09:34:08', '站内信创建成功', NULL, 0, 1, '2025-10-20 09:34:42', NULL, '2025-10-20 09:34:08', '2025-10-20 09:34:08');
INSERT INTO `notification_record` VALUES (26, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:{B2青少年训练场},时间:{2025-11-27 11:00-14:00},订单号:{BK1760924690784}', '9', 'booking', '20', 2, '2025-10-20 09:44:57', '站内信创建成功', NULL, 0, 1, '2025-10-20 09:45:17', NULL, '2025-10-20 09:44:57', '2025-10-20 09:44:57');
INSERT INTO `notification_record` VALUES (27, 9, 7, 'PAYMENT_SUCCESS', 'system', '支付成功', '支付成功!金额:¥{165.00},订单号:{{orderNo}}', '9', 'booking', 'BK1760924690784', 2, '2025-10-20 09:45:00', '站内信创建成功', NULL, 0, 1, '2025-10-20 09:45:24', NULL, '2025-10-20 09:45:00', '2025-10-20 09:45:00');
INSERT INTO `notification_record` VALUES (28, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:C2阳光篮球场,时间:2025-10-29 09:00-11:00,订单号:BK1760925040943', '9', 'booking', '21', 2, '2025-10-20 09:50:47', '站内信创建成功', NULL, 0, 1, '2025-10-20 09:51:01', NULL, '2025-10-20 09:50:47', '2025-10-20 09:50:47');
INSERT INTO `notification_record` VALUES (29, 9, 7, 'PAYMENT_SUCCESS', 'system', '支付成功', '支付成功!金额:¥130.00,订单号:BK1760925040943', '9', 'booking', 'BK1760925040943', 2, '2025-10-20 09:50:50', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-20 09:50:50', '2025-10-20 09:50:50');
INSERT INTO `notification_record` VALUES (30, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:A1专业篮球场,时间:2025-10-31 07:00-11:00,订单号:BK1760925852660', '9', 'booking', '22', 2, '2025-10-20 10:04:21', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:11', NULL, '2025-10-20 10:04:21', '2025-10-20 10:04:21');
INSERT INTO `notification_record` VALUES (31, 9, 5, 'BOOKING_SUCCESS', 'system', '预订成功', '预订成功!场地:A1专业篮球场,时间:2025-10-31 07:00-11:00,订单号:BK1760925852660', '9', 'booking', '22', 2, '2025-10-20 10:04:33', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:22', NULL, '2025-10-20 10:04:33', '2025-10-20 10:04:33');
INSERT INTO `notification_record` VALUES (32, 9, 7, 'PAYMENT_SUCCESS', 'system', '支付成功', '支付成功!金额:¥400.00,订单号:BK1760925852660', '9', 'booking', 'BK1760925852660', 2, '2025-10-20 10:04:37', '站内信创建成功', NULL, 0, 1, '2025-10-20 10:05:18', NULL, '2025-10-20 10:04:37', '2025-10-20 10:04:37');

-- ----------------------------
-- Table structure for notification_subscribe
-- ----------------------------
DROP TABLE IF EXISTS `notification_subscribe`;
CREATE TABLE `notification_subscribe`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订阅ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `scene` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知场景',
  `notification_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型',
  `is_enabled` tinyint NULL DEFAULT 1 COMMENT '是否启用: 0-否, 1-是',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_scene_type`(`user_id` ASC, `scene` ASC, `notification_type` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `notification_subscribe_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知订阅配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification_subscribe
-- ----------------------------

-- ----------------------------
-- Table structure for notification_template
-- ----------------------------
DROP TABLE IF EXISTS `notification_template`;
CREATE TABLE `notification_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板编码',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `template_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型: sms/wechat/system/email',
  `scene` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '使用场景: member_expire/course_remind/booking_success等',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题模板',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容模板(支持变量占位符)',
  `variables` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '变量列表(JSON数组): [\"userName\",\"expireDate\"]',
  `third_party_template_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方平台模板ID',
  `priority` tinyint NULL DEFAULT 1 COMMENT '优先级: 1-低, 2-中, 3-高',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `example_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '示例数据(JSON)',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注说明',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `template_code`(`template_code` ASC) USING BTREE,
  INDEX `idx_scene`(`scene` ASC) USING BTREE,
  INDEX `idx_type`(`template_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification_template
-- ----------------------------
INSERT INTO `notification_template` VALUES (1, 'MEMBER_EXPIRE_7DAYS', '会员到期提醒(7天)', 'system', 'member_expire', '会员到期提醒', '尊敬的{{userName}},您的会员卡将于{{expireDate}}到期,请及时续费以享受会员权益。', '[\"userName\",\"expireDate\"]', NULL, 2, 1, NULL, NULL, '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `notification_template` VALUES (2, 'MEMBER_EXPIRE_3DAYS', '会员到期提醒(3天)', 'system', 'member_expire', '会员到期提醒', '尊敬的{{userName}},您的会员卡将于{{expireDate}}到期,仅剩3天!立即续费享9折优惠。', '[\"userName\",\"expireDate\"]', NULL, 3, 1, NULL, NULL, '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `notification_template` VALUES (3, 'COURSE_START_REMIND', '课程开始提醒', 'system', 'course_remind', '课程即将开始', '您报名的【{{courseName}}】将于{{startTime}}开始,地点:{{location}},请准时参加!', '[\"courseName\",\"startTime\",\"location\"]', NULL, 3, 1, NULL, NULL, '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `notification_template` VALUES (4, 'COURSE_CANCEL_NOTIFY', '课程取消通知', 'system', 'course_cancel', '课程取消通知', '抱歉,【{{courseName}}】因{{reason}}已取消,费用将原路退回。', '[\"courseName\",\"reason\"]', NULL, 3, 1, NULL, NULL, '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `notification_template` VALUES (5, 'BOOKING_SUCCESS', '预订成功通知', 'system', 'booking_success', '预订成功', '预订成功!场地:{{venueName}},时间:{{bookingTime}},订单号:{{orderNo}}', '[\"venueName\",\"bookingTime\",\"orderNo\"]', NULL, 2, 1, NULL, NULL, '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `notification_template` VALUES (6, 'BOOKING_CANCEL', '预订取消通知', 'system', 'booking_cancel', '预订已取消', '您的预订已取消,订单号:{{orderNo}},退款将在3-5个工作日内到账。', '[\"orderNo\"]', NULL, 2, 1, NULL, NULL, '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `notification_template` VALUES (7, 'PAYMENT_SUCCESS', '支付成功通知', 'system', 'payment_success', '支付成功', '支付成功!金额:¥{{amount}},订单号:{{orderNo}}', '[\"amount\",\"orderNo\"]', NULL, 2, 1, NULL, NULL, '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `notification_template` VALUES (8, 'REFUND_SUCCESS', '退款成功通知', 'system', 'refund_success', '退款成功', '退款已到账,金额:¥{{amount}},订单号:{{orderNo}}', '[\"amount\",\"orderNo\"]', NULL, 2, 1, NULL, NULL, '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `notification_template` VALUES (9, 'PROMO_ACTIVITY', '优惠活动通知', 'system', 'promo', '优惠活动', '{{activityName}}火热进行中!{{description}}', '[\"activityName\",\"description\"]', NULL, 1, 1, NULL, NULL, '2025-10-18 01:04:34', '2025-10-18 01:04:34');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人用户名',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作名称',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方法名',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '返回结果',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作地点',
  `browser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-失败, 1-成功',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `execute_time` int NULL DEFAULT NULL COMMENT '执行时长(毫秒)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for payment
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `order_type` tinyint NOT NULL COMMENT '订单类型: 1-场地预订, 2-课程报名, 3-会员卡充值, 4-余额充值',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `pay_method` tinyint NULL DEFAULT NULL COMMENT '支付方式: 1-支付宝, 2-微信, 3-余额, 4-会员卡, 5-银行卡',
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '支付状态: 0-待支付, 1-已支付, 2-已退款, 3-支付失败',
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方交易号',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `refund_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '退款金额',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款时间',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '订单描述',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_pay_status`(`pay_status` ASC) USING BTREE,
  INDEX `idx_pay_time`(`pay_time` ASC) USING BTREE,
  INDEX `idx_payment_user_time`(`user_id` ASC, `pay_time` ASC) USING BTREE,
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment
-- ----------------------------

-- ----------------------------
-- Table structure for payment_config
-- ----------------------------
DROP TABLE IF EXISTS `payment_config`;
CREATE TABLE `payment_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `pay_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付方式: wechat_native/wechat_jsapi/alipay_page/alipay_wap',
  `pay_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付名称',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '应用ID',
  `merchant_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商户ID',
  `api_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'API密钥',
  `app_secret` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '应用密钥',
  `private_key` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '私钥',
  `public_key` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公钥',
  `notify_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '异步通知URL',
  `return_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '同步返回URL',
  `is_sandbox` tinyint NULL DEFAULT 0 COMMENT '是否沙箱环境: 0-否, 1-是',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `pay_method`(`pay_method` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_config
-- ----------------------------
INSERT INTO `payment_config` VALUES (1, 'wechat_native', '微信扫码支付', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 1, '需要配置微信商户号、API密钥等信息', '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `payment_config` VALUES (2, 'wechat_jsapi', '微信公众号支付', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 2, '需要配置微信公众号AppID、商户号等', '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `payment_config` VALUES (3, 'alipay_page', '支付宝电脑网站支付', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 3, '需要配置支付宝AppID、应用私钥等', '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `payment_config` VALUES (4, 'alipay_wap', '支付宝手机网站支付', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 4, '需要配置支付宝AppID、应用私钥等', '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `payment_config` VALUES (5, 'balance', '余额支付', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, 5, '使用账户余额支付', '2025-10-18 01:04:34', '2025-10-18 01:04:34');
INSERT INTO `payment_config` VALUES (6, 'member_card', '会员卡支付', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, 6, '使用会员卡余额支付', '2025-10-18 01:04:34', '2025-10-18 01:04:34');

-- ----------------------------
-- Table structure for payment_notify_log
-- ----------------------------
DROP TABLE IF EXISTS `payment_notify_log`;
CREATE TABLE `payment_notify_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `pay_channel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付渠道',
  `notify_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知类型: payment/refund',
  `notify_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知数据(JSON)',
  `verify_status` tinyint NULL DEFAULT NULL COMMENT '验签状态: 0-失败, 1-成功',
  `process_status` tinyint NULL DEFAULT 0 COMMENT '处理状态: 0-待处理, 1-成功, 2-失败',
  `process_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处理结果',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `retry_count` int NULL DEFAULT 0 COMMENT '重试次数',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知来源IP',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_process_status`(`process_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付异步通知日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_notify_log
-- ----------------------------
INSERT INTO `payment_notify_log` VALUES (1, 'PAY20251019002944326004', NULL, 'callback', '{transaction_id=MOCK_1760806220002, out_trade_no=PAY20251019002944326004, trade_state=SUCCESS, total_fee=100}', NULL, 1, '处理成功', NULL, 0, NULL, '2025-10-19 00:50:20', '2025-10-19 00:50:20');
INSERT INTO `payment_notify_log` VALUES (2, 'PAY20251019002944326004', NULL, 'callback', '{transaction_id=MOCK_1760806232021, out_trade_no=PAY20251019002944326004, trade_state=SUCCESS, total_fee=100}', NULL, 1, '订单已支付', NULL, 0, NULL, '2025-10-19 00:50:32', '2025-10-19 00:50:32');
INSERT INTO `payment_notify_log` VALUES (3, 'PAY20251019005057240565', NULL, 'callback', '{transaction_id=MOCK_1760806258526, out_trade_no=PAY20251019005057240565, trade_state=SUCCESS, total_fee=100}', NULL, 1, '处理成功', NULL, 0, NULL, '2025-10-19 00:50:59', '2025-10-19 00:50:58');
INSERT INTO `payment_notify_log` VALUES (4, 'PAY20251019005057240565', NULL, 'callback', '{transaction_id=MOCK_1760806695561, out_trade_no=PAY20251019005057240565, trade_state=SUCCESS, total_fee=100}', NULL, 1, '订单已支付', NULL, 0, NULL, '2025-10-19 00:58:16', '2025-10-19 00:58:15');
INSERT INTO `payment_notify_log` VALUES (5, 'PAY123456789', NULL, 'callback', '{transaction_id=MOCK_1760806801486, out_trade_no=PAY123456789, trade_state=SUCCESS, total_fee=100}', NULL, 2, NULL, '订单不存在', 0, NULL, '2025-10-19 01:00:01', '2025-10-19 01:00:01');
INSERT INTO `payment_notify_log` VALUES (6, 'PAY123456789', NULL, 'callback', '{transaction_id=MOCK_1760807099674, out_trade_no=PAY123456789, trade_state=SUCCESS, total_fee=100}', NULL, 2, NULL, '订单不存在', 0, NULL, '2025-10-19 01:05:00', '2025-10-19 01:04:59');
INSERT INTO `payment_notify_log` VALUES (7, 'PAY20251019010640126771', NULL, 'callback', '{transaction_id=MOCK_1760807202347, out_trade_no=PAY20251019010640126771, trade_state=SUCCESS, total_fee=100}', NULL, 1, '处理成功', NULL, 0, NULL, '2025-10-19 01:06:42', '2025-10-19 01:06:42');
INSERT INTO `payment_notify_log` VALUES (8, 'PAY20251020092338973759', NULL, 'callback', '{transaction_id=MOCK_1760923425348, out_trade_no=PAY20251020092338973759, trade_state=SUCCESS, total_fee=100}', NULL, 1, '处理成功', NULL, 0, NULL, '2025-10-20 09:23:45', '2025-10-20 09:23:45');
INSERT INTO `payment_notify_log` VALUES (9, 'PAY20251020093403761959', NULL, 'callback', '{transaction_id=MOCK_1760924048408, out_trade_no=PAY20251020093403761959, trade_state=SUCCESS, total_fee=100}', NULL, 1, '处理成功', NULL, 0, NULL, '2025-10-20 09:34:08', '2025-10-20 09:34:08');
INSERT INTO `payment_notify_log` VALUES (10, 'PAY20251020094457477094', NULL, 'callback', '{transaction_id=MOCK_1760924700214, out_trade_no=PAY20251020094457477094, trade_state=SUCCESS, total_fee=100}', NULL, 1, '处理成功', NULL, 0, NULL, '2025-10-20 09:45:00', '2025-10-20 09:45:00');
INSERT INTO `payment_notify_log` VALUES (11, 'PAY20251020095047549893', NULL, 'callback', '{transaction_id=MOCK_1760925050066, out_trade_no=PAY20251020095047549893, trade_state=SUCCESS, total_fee=100}', NULL, 1, '处理成功', NULL, 0, NULL, '2025-10-20 09:50:50', '2025-10-20 09:50:50');
INSERT INTO `payment_notify_log` VALUES (12, 'PAY20251020100433371509', NULL, 'callback', '{transaction_id=MOCK_1760925876934, out_trade_no=PAY20251020100433371509, trade_state=SUCCESS, total_fee=100}', NULL, 1, '处理成功', NULL, 0, NULL, '2025-10-20 10:04:37', '2025-10-20 10:04:36');

-- ----------------------------
-- Table structure for payment_order
-- ----------------------------
DROP TABLE IF EXISTS `payment_order`;
CREATE TABLE `payment_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付订单ID',
  `payment_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付订单号(唯一)',
  `business_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务订单号(预订订单号或会员卡订单号)',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型: booking-预订, member_card-会员卡, course-课程',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `payment_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式: wechat_native, alipay_pc等',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `fee_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '手续费',
  `actual_amount` decimal(10, 2) NOT NULL COMMENT '实际支付金额',
  `status` tinyint NULL DEFAULT 0 COMMENT '支付状态: 0-待支付, 1-支付中, 2-支付成功, 3-支付失败, 4-已取消, 5-已退款',
  `third_party_order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方支付订单号',
  `third_party_trade_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方支付流水号',
  `qr_code_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付二维码URL',
  `payment_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付跳转URL',
  `client_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户IP地址',
  `paid_time` datetime NULL DEFAULT NULL COMMENT '支付完成时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '订单过期时间',
  `refund_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '退款金额',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款时间',
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款原因',
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付失败原因',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_business_no`(`business_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `payment_order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_order
-- ----------------------------
INSERT INTO `payment_order` VALUES (1, 'PAY20251018063731213940', 'BK1760739804199', 'booking', 9, 'wechat_native', 300.00, 0.00, 300.00, 0, 'MOCK_WX_1760740651509', NULL, 'https://api.mch.weixin.qq.com/pay/qrcode?data=mock_PAY20251018063731213940', NULL, NULL, NULL, '2025-10-18 07:07:32', 0.00, NULL, NULL, NULL, NULL, '2025-10-18 06:37:32', '2025-10-18 06:37:32');
INSERT INTO `payment_order` VALUES (2, 'PAY20251019000041262354', 'BK1760802760177', 'booking', 9, 'wechat_native', 300.00, 0.00, 300.00, 0, 'MOCK_WX_1760803241650', NULL, 'https://api.mch.weixin.qq.com/pay/qrcode?data=mock_PAY20251019000041262354', NULL, NULL, NULL, '2025-10-19 00:30:42', 0.00, NULL, NULL, NULL, NULL, '2025-10-19 00:00:42', '2025-10-19 00:00:42');
INSERT INTO `payment_order` VALUES (3, 'PAY20251019002944326004', 'BK1760804982329', 'booking', 9, 'wechat_native', 1000.00, 0.00, 1000.00, 2, 'MOCK_WX_1760804984921', NULL, 'https://api.mch.weixin.qq.com/pay/qrcode?data=mock_PAY20251019002944326004', NULL, NULL, '2025-10-19 00:50:20', '2025-10-19 00:59:45', 0.00, NULL, NULL, NULL, NULL, '2025-10-19 00:29:45', '2025-10-19 00:50:20');
INSERT INTO `payment_order` VALUES (4, 'PAY20251019005057240565', 'BK1760806250791', 'booking', 9, 'wechat_native', 360.00, 0.00, 360.00, 2, 'MOCK_WX_1760806257027', NULL, 'https://api.mch.weixin.qq.com/pay/qrcode?data=mock_PAY20251019005057240565', NULL, NULL, '2025-10-19 00:50:59', '2025-10-19 01:20:57', 0.00, NULL, NULL, NULL, NULL, '2025-10-19 00:50:57', '2025-10-19 00:50:59');
INSERT INTO `payment_order` VALUES (5, 'PAY20251019010640126771', 'BK1760807197060', 'booking', 9, 'wechat_native', 110.00, 0.00, 110.00, 2, 'MOCK_WX_1760807200442', NULL, 'https://api.mch.weixin.qq.com/pay/qrcode?data=mock_PAY20251019010640126771', NULL, NULL, '2025-10-19 01:06:42', '2025-10-19 01:36:40', 0.00, NULL, NULL, NULL, NULL, '2025-10-19 01:06:40', '2025-10-19 01:06:42');
INSERT INTO `payment_order` VALUES (6, 'PAY20251020092338973759', 'BK1760923408792', 'booking', 9, 'wechat_native', 180.00, 0.00, 180.00, 2, 'MOCK_WX_1760923418892', NULL, 'https://api.mch.weixin.qq.com/pay/qrcode?data=mock_PAY20251020092338973759', NULL, NULL, '2025-10-20 09:23:45', '2025-10-20 09:53:39', 0.00, NULL, NULL, NULL, NULL, '2025-10-20 09:23:39', '2025-10-20 09:23:45');
INSERT INTO `payment_order` VALUES (7, 'PAY20251020093403761959', 'BK1760923670119', 'booking', 9, 'alipay_page', 120.00, 0.00, 120.00, 2, 'MOCK_ALIPAY_1760924043877', NULL, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=mock_alipay_PAY20251020093403761959', NULL, NULL, '2025-10-20 09:34:08', '2025-10-20 10:04:04', 0.00, NULL, NULL, NULL, NULL, '2025-10-20 09:34:04', '2025-10-20 09:34:08');
INSERT INTO `payment_order` VALUES (8, 'PAY20251020094457477094', 'BK1760924690784', 'booking', 9, 'wechat_native', 165.00, 0.00, 165.00, 2, 'MOCK_WX_1760924697334', NULL, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=mock_wechat_PAY20251020094457477094', NULL, NULL, '2025-10-20 09:45:00', '2025-10-20 10:14:57', 0.00, NULL, NULL, NULL, NULL, '2025-10-20 09:44:57', '2025-10-20 09:45:00');
INSERT INTO `payment_order` VALUES (9, 'PAY20251020095047549893', 'BK1760925040943', 'booking', 9, 'alipay_page', 130.00, 0.00, 130.00, 2, 'MOCK_ALIPAY_1760925047055', NULL, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=mock_alipay_PAY20251020095047549893', NULL, NULL, '2025-10-20 09:50:50', '2025-10-20 10:20:47', 0.00, NULL, NULL, NULL, NULL, '2025-10-20 09:50:47', '2025-10-20 09:50:50');
INSERT INTO `payment_order` VALUES (10, 'PAY20251020100433371509', 'BK1760925852660', 'booking', 9, 'alipay_page', 400.00, 0.00, 400.00, 2, 'MOCK_ALIPAY_1760925873170', NULL, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=mock_alipay_PAY20251020100433371509', NULL, NULL, '2025-10-20 10:04:37', '2025-10-20 10:34:33', 0.00, NULL, NULL, NULL, NULL, '2025-10-20 10:04:33', '2025-10-20 10:04:37');

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限代码',
  `permission_type` tinyint NULL DEFAULT NULL COMMENT '权限类型: 1-菜单, 2-按钮, 3-接口',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父权限ID',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `permission_code`(`permission_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------

-- ----------------------------
-- Table structure for points_exchange
-- ----------------------------
DROP TABLE IF EXISTS `points_exchange`;
CREATE TABLE `points_exchange`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '兑换ID',
  `exchange_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '兑换编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `points_cost` int NOT NULL COMMENT '消耗积分',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-待发货, 1-已发货, 2-已完成, 3-已取消',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人',
  `contact_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货地址',
  `express_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '快递公司',
  `express_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '快递单号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `exchange_no`(`exchange_no` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_exchange_no`(`exchange_no` ASC) USING BTREE,
  CONSTRAINT `points_exchange_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `points_exchange_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `points_product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分兑换记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_exchange
-- ----------------------------

-- ----------------------------
-- Table structure for points_product
-- ----------------------------
DROP TABLE IF EXISTS `points_product`;
CREATE TABLE `points_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_type` tinyint NULL DEFAULT NULL COMMENT '类型: 1-实物, 2-优惠券, 3-课程券, 4-场地券',
  `points_required` int NOT NULL COMMENT '所需积分',
  `stock` int NULL DEFAULT 0 COMMENT '库存',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
  `sort_order` int NULL DEFAULT 0,
  `exchange_count` int NULL DEFAULT 0 COMMENT '兑换次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_product
-- ----------------------------

-- ----------------------------
-- Table structure for points_record
-- ----------------------------
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '积分记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points` int NOT NULL COMMENT '积分变动(正数为获得,负数为消费)',
  `type` tinyint NOT NULL COMMENT '类型: 1-消费赠送, 2-兑换商品, 3-签到, 4-活动, 5-过期扣除, 6-管理员调整',
  `related_order` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联订单号',
  `points_before` int NULL DEFAULT NULL COMMENT '变动前积分',
  `points_after` int NULL DEFAULT NULL COMMENT '变动后积分',
  `expire_date` date NULL DEFAULT NULL COMMENT '积分过期日期',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `points_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_record
-- ----------------------------

-- ----------------------------
-- Table structure for refund
-- ----------------------------
DROP TABLE IF EXISTS `refund`;
CREATE TABLE `refund`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '退款ID',
  `refund_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '退款编号',
  `payment_id` bigint NOT NULL COMMENT '支付记录ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款原因',
  `refund_type` tinyint NULL DEFAULT NULL COMMENT '退款类型: 1-用户申请, 2-系统自动, 3-管理员操作',
  `refund_status` tinyint NULL DEFAULT 0 COMMENT '退款状态: 0-申请中, 1-已同意, 2-已拒绝, 3-退款中, 4-已退款',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款交易号',
  `apply_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款完成时间',
  `processor_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `refund_no`(`refund_no` ASC) USING BTREE,
  INDEX `payment_id`(`payment_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_refund_no`(`refund_no` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_status`(`refund_status` ASC) USING BTREE,
  CONSTRAINT `refund_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `refund_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '退款记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refund
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色代码: ADMIN, MANAGER, COACH, MEMBER',
  `role_level` int NULL DEFAULT 1 COMMENT '角色级别',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE,
  INDEX `idx_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员', 'ADMIN', 5, '系统最高权限', 1, '2025-10-01 00:25:39');
INSERT INTO `role` VALUES (2, '场馆管理员', 'MANAGER', 4, '负责场馆运营管理', 1, '2025-10-01 00:25:39');
INSERT INTO `role` VALUES (3, '教练', 'COACH', 3, '负责课程教学', 1, '2025-10-01 00:25:39');
INSERT INTO `role` VALUES (4, '会员用户', 'MEMBER', 2, '注册会员', 1, '2025-10-01 00:25:39');
INSERT INTO `role` VALUES (5, '游客', 'GUEST', 1, '未注册用户', 1, '2025-10-01 00:25:39');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `permission_id`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for scheduled_reminder
-- ----------------------------
DROP TABLE IF EXISTS `scheduled_reminder`;
CREATE TABLE `scheduled_reminder`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提醒ID',
  `task_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务类型: member_expire/course_start/booking_remind',
  `target_user_id` bigint NOT NULL COMMENT '目标用户ID',
  `biz_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
  `biz_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务ID',
  `remind_time` datetime NOT NULL COMMENT '提醒时间',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提醒标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提醒内容',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-待执行, 1-已执行, 2-已取消, 3-失败',
  `execute_time` datetime NULL DEFAULT NULL COMMENT '执行时间',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行结果',
  `retry_count` int NULL DEFAULT 0 COMMENT '重试次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_remind_time`(`remind_time` ASC, `status` ASC) USING BTREE,
  INDEX `idx_user`(`target_user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `scheduled_reminder_ibfk_1` FOREIGN KEY (`target_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '定时提醒任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scheduled_reminder
-- ----------------------------
INSERT INTO `scheduled_reminder` VALUES (1, 'booking_start', 9, 'booking', '12', '2025-10-18 07:00:00', '预订开始提醒', '您的场地预订将在1小时后开始，请准时到场', 0, NULL, NULL, 0, '2025-10-18 07:00:00', '2025-10-18 07:00:00');
INSERT INTO `scheduled_reminder` VALUES (2, 'booking_start', 9, 'booking', '19', '2025-10-20 10:00:00', '预订开始提醒', '您的场地预订将在1小时后开始，请准时到场', 0, NULL, NULL, 0, '2025-10-20 10:00:00', '2025-10-20 10:00:00');
INSERT INTO `scheduled_reminder` VALUES (3, 'booking_start', 9, 'booking', '20', '2025-10-20 10:00:00', '预订开始提醒', '您的场地预订将在1小时后开始，请准时到场', 0, NULL, NULL, 0, '2025-10-20 10:00:00', '2025-10-20 10:00:00');
INSERT INTO `scheduled_reminder` VALUES (4, 'booking_start', 9, 'booking', '17', '2025-10-20 11:00:00', '预订开始提醒', '您的场地预订将在1小时后开始，请准时到场', 0, NULL, NULL, 0, '2025-10-20 11:00:00', '2025-10-20 11:00:00');
INSERT INTO `scheduled_reminder` VALUES (5, 'booking_start', 2, 'booking', '4', '2025-10-20 13:00:00', '预订开始提醒', '您的场地预订将在1小时后开始，请准时到场', 0, NULL, NULL, 0, '2025-10-20 13:00:00', '2025-10-20 13:00:00');
INSERT INTO `scheduled_reminder` VALUES (6, 'booking_start', 3, 'booking', '8', '2025-10-20 14:00:00', '预订开始提醒', '您的场地预订将在1小时后开始，请准时到场', 0, NULL, NULL, 0, '2025-10-20 14:00:00', '2025-10-20 14:00:00');
INSERT INTO `scheduled_reminder` VALUES (7, 'booking_start', 3, 'booking', '5', '2025-10-20 18:00:00', '预订开始提醒', '您的场地预订将在1小时后开始，请准时到场', 0, NULL, NULL, 0, '2025-10-20 18:00:00', '2025-10-20 18:00:00');

-- ----------------------------
-- Table structure for sms_code
-- ----------------------------
DROP TABLE IF EXISTS `sms_code`;
CREATE TABLE `sms_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
  `code_type` tinyint NULL DEFAULT NULL COMMENT '类型: 1-注册, 2-登录, 3-找回密码, 4-修改手机',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `is_used` tinyint NULL DEFAULT 0 COMMENT '是否已使用',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone_code`(`phone` ASC, `code` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '短信验证码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_code
-- ----------------------------

-- ----------------------------
-- Table structure for sms_verification_code
-- ----------------------------
DROP TABLE IF EXISTS `sms_verification_code`;
CREATE TABLE `sms_verification_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '验证码ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型: login/register/bind/reset',
  `scene` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '使用场景描述',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `used` tinyint NULL DEFAULT 0 COMMENT '是否已使用: 0-未使用, 1-已使用',
  `used_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `send_count` int NULL DEFAULT 1 COMMENT '发送次数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-失败, 1-成功',
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone_type`(`phone` ASC, `type` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '短信验证码表(新)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_verification_code
-- ----------------------------

-- ----------------------------
-- Table structure for statistics_cache
-- ----------------------------
DROP TABLE IF EXISTS `statistics_cache`;
CREATE TABLE `statistics_cache`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '缓存ID',
  `cache_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '缓存键',
  `stats_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '统计类型: member_activity/course_popularity/venue_usage',
  `cache_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '缓存数据(JSON格式)',
  `ttl` int NULL DEFAULT 3600 COMMENT '过期时间(秒)',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `cache_key`(`cache_key` ASC) USING BTREE,
  INDEX `idx_type`(`stats_type` ASC) USING BTREE,
  INDEX `idx_expire`(`expire_time` ASC) USING BTREE,
  INDEX `idx_stats_type_key`(`stats_type` ASC, `cache_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '统计数据缓存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of statistics_cache
-- ----------------------------

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `config_type` tinyint NULL DEFAULT NULL COMMENT '类型: 1-字符串, 2-数字, 3-布尔, 4-JSON',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `group_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置组',
  `sort_order` int NULL DEFAULT 0,
  `is_editable` tinyint NULL DEFAULT 1 COMMENT '是否可编辑',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE,
  INDEX `idx_group`(`group_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 150 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'system.name', '篮球馆管理系统', 1, '系统名称', '基本配置', 0, 1, '2025-10-01 00:25:39', '2025-10-01 00:25:39');
INSERT INTO `system_config` VALUES (2, 'booking.advance_days', '7', 2, '最多可提前预订天数', '预订配置', 0, 1, '2025-10-01 00:25:39', '2025-10-01 00:25:39');
INSERT INTO `system_config` VALUES (3, 'booking.cancel_hours', '2', 2, '取消预订最少提前小时数', '预订配置', 0, 1, '2025-10-01 00:25:39', '2025-10-01 00:25:39');
INSERT INTO `system_config` VALUES (4, 'booking.timeout_minutes', '30', 2, '订单超时时间(分钟)', '预订配置', 0, 1, '2025-10-01 00:25:39', '2025-10-01 00:25:39');
INSERT INTO `system_config` VALUES (5, 'points.consume_rate', '100', 2, '消费送积分比例(消费100元送1积分)', '积分配置', 0, 1, '2025-10-01 00:25:39', '2025-10-01 00:25:39');
INSERT INTO `system_config` VALUES (6, 'points.expire_days', '365', 2, '积分有效期(天)', '积分配置', 0, 1, '2025-10-01 00:25:39', '2025-10-01 00:25:39');
INSERT INTO `system_config` VALUES (7, 'sms.code_expire_minutes', '5', 2, '验证码有效期(分钟)', '短信配置', 0, 1, '2025-10-01 00:25:39', '2025-10-01 00:25:39');
INSERT INTO `system_config` VALUES (8, 'system.version', '1.0.0', 1, '系统版本', '基本配置', 2, 1, '2025-10-02 14:35:38', '2025-10-02 14:35:38');
INSERT INTO `system_config` VALUES (24, 'site.name', '篮球场馆预约系统', 1, '系统名称', '基础配置', 1, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `system_config` VALUES (25, 'site.logo', '/images/logo.png', 1, '系统Logo', '基础配置', 2, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `system_config` VALUES (26, 'points.booking_rate', '100', 2, '预订消费积分比例(每100元获得积分)', '积分配置', 1, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `system_config` VALUES (27, 'points.booking_points', '10', 2, '预订消费获得积分数', '积分配置', 2, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `system_config` VALUES (28, 'points.signin_points', '5', 2, '每日签到获得积分', '积分配置', 3, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `system_config` VALUES (29, 'member.silver_discount', '0.95', 2, '银卡会员折扣', '会员配置', 1, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `system_config` VALUES (30, 'member.gold_discount', '0.90', 2, '金卡会员折扣', '会员配置', 2, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `system_config` VALUES (31, 'member.diamond_discount', '0.85', 2, '钻石会员折扣', '会员配置', 3, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `system_config` VALUES (91, 'system.company', '篮球场馆管理公司', 1, '所属公司', '系统设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (92, 'system.contact.email', 'admin@basketball.com', 1, '联系邮箱', '系统设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (93, 'system.contact.phone', '400-123-4567', 1, '联系电话', '系统设置', 5, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (94, 'booking.advance.days', '7', 2, '提前预订天数(天)', '预订设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (95, 'booking.cancel.hours', '24', 2, '取消预订提前时间(小时)', '预订设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (96, 'booking.min.duration', '60', 2, '最短预订时长(分钟)', '预订设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (97, 'booking.max.duration', '480', 2, '最长预订时长(分钟)', '预订设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (98, 'price.standard.hour', '100', 2, '标准场地每小时价格(元)', '价格设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (99, 'price.vip.hour', '200', 2, 'VIP场地每小时价格(元)', '价格设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (100, 'price.complex.hour', '150', 2, '综合场地每小时价格(元)', '价格设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (101, 'price.coach.hour', '300', 2, '教练每小时价格(元)', '价格设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (102, 'member.normal.discount', '0', 2, '普通会员折扣率(%)', '会员设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (103, 'member.silver.discount', '10', 2, '银卡会员折扣率(%)', '会员设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (104, 'member.gold.discount', '20', 2, '金卡会员折扣率(%)', '会员设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (105, 'member.diamond.discount', '30', 2, '钻石会员折扣率(%)', '会员设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (106, 'payment.wechat.enabled', 'true', 3, '启用微信支付', '支付设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (107, 'payment.alipay.enabled', 'true', 3, '启用支付宝支付', '支付设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (108, 'payment.cash.enabled', 'true', 3, '启用现金支付', '支付设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (109, 'payment.auto.cancel.minutes', '15', 2, '自动取消未支付订单时间(分钟)', '支付设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (110, 'sms.provider', 'aliyun', 1, '短信服务提供商', '短信设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (111, 'sms.app.id', '', 1, '短信应用ID', '短信设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (112, 'sms.app.key', '', 1, '短信应用密钥', '短信设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (113, 'sms.template.id', '', 1, '短信模板ID', '短信设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (114, 'email.smtp.host', 'smtp.qq.com', 1, 'SMTP服务器地址', '邮件设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (115, 'email.smtp.port', '587', 2, 'SMTP服务器端口', '邮件设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (116, 'email.smtp.username', '', 1, 'SMTP用户名', '邮件设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (117, 'email.smtp.password', '', 1, 'SMTP密码', '邮件设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (118, 'email.from.address', 'noreply@basketball.com', 1, '发件人邮箱', '邮件设置', 5, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (119, 'email.from.name', '篮球场馆系统', 1, '发件人名称', '邮件设置', 6, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (120, 'system.cache.enabled', 'true', 3, '启用系统缓存', '性能设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (121, 'system.cache.expire.minutes', '30', 2, '缓存过期时间(分钟)', '性能设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (122, 'system.upload.max.size', '10', 2, '文件上传最大大小(MB)', '性能设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (123, 'system.upload.allowed.types', 'jpg,jpeg,png,gif,pdf,doc,docx', 1, '允许上传的文件类型', '性能设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (124, 'security.login.max.attempts', '5', 2, '最大登录尝试次数', '安全设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (125, 'security.lockout.minutes', '30', 2, '账户锁定时间(分钟)', '安全设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (126, 'security.session.timeout', '120', 2, '会话超时时间(分钟)', '安全设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (127, 'security.password.min.length', '8', 2, '密码最小长度', '安全设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (128, 'security.password.require.special', 'true', 3, '密码要求特殊字符', '安全设置', 5, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (129, 'security.password.require.number', 'true', 3, '密码要求数字', '安全设置', 6, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (130, 'security.password.require.uppercase', 'true', 3, '密码要求大写字母', '安全设置', 7, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (131, 'security.password.require.lowercase', 'true', 3, '密码要求小写字母', '安全设置', 8, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (132, 'logging.level.root', 'INFO', 1, '根日志级别', '日志设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (133, 'logging.level.com.basketball', 'DEBUG', 1, '应用日志级别', '日志设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (134, 'logging.file.max.size', '50', 2, '日志文件最大大小(MB)', '日志设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (135, 'logging.file.max.history', '30', 2, '日志文件保留天数', '日志设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (136, 'logging.log.sql.enabled', 'true', 3, '记录SQL日志', '日志设置', 5, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (137, 'analytics.enabled', 'true', 3, '启用统计分析', '分析设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (138, 'analytics.cache.hours', '24', 2, '统计数据缓存时间(小时)', '分析设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (139, 'analytics.data.retention.days', '90', 2, '数据保留天数', '分析设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (140, 'analytics.report.daily.enabled', 'true', 3, '启用日报', '分析设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (141, 'analytics.report.weekly.enabled', 'true', 3, '启用周报', '分析设置', 5, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (142, 'analytics.report.monthly.enabled', 'true', 3, '启用月报', '分析设置', 6, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (143, 'backup.enabled', 'true', 3, '启用数据备份', '备份设置', 1, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (144, 'backup.frequency', 'daily', 1, '备份频率(daily/weekly/monthly)', '备份设置', 2, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (145, 'backup.retention.days', '30', 2, '备份保留天数', '备份设置', 3, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (146, 'backup.auto.upload.enabled', 'false', 3, '启用自动上传备份', '备份设置', 4, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');
INSERT INTO `system_config` VALUES (147, 'backup.storage.path', '/backup', 1, '备份存储路径', '备份设置', 5, 1, '2025-10-20 18:05:20', '2025-10-20 18:05:20');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(加密)',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `member_level` int NULL DEFAULT 0 COMMENT '会员等级: 0-普通, 1-银卡, 2-金卡, 3-钻石',
  `points` int NULL DEFAULT 0 COMMENT '积分',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138000', NULL, '系统管理员', 0, NULL, 1, 3, 0, 0.00, '2025-10-01 00:25:39', '2025-10-01 00:25:39', '2025-10-18 02:35:57');
INSERT INTO `user` VALUES (2, 'zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138001', 'zhangsan@basketball.com', '张三', 1, NULL, 1, 2, 500, 200.00, '2025-10-02 14:35:38', '2025-10-02 14:35:38', '2025-10-02 18:37:46');
INSERT INTO `user` VALUES (3, 'admin123', '$2a$10$5P3OronOqu5fwZwn1.0zCORFIavq7a/Qg/idkf96be9e9lFl75tye', '15555555555', NULL, NULL, 0, NULL, 1, 0, 0, 0.00, '2025-10-01 00:31:35', '2025-10-01 00:31:35', '2025-10-01 00:57:32');
INSERT INTO `user` VALUES (4, 'wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138003', 'wangwu@basketball.com', '王五', 2, NULL, 1, 0, 50, 0.00, '2025-10-02 14:35:38', '2025-10-02 14:35:38', NULL);
INSERT INTO `user` VALUES (5, 'coach_wang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900139001', 'wang@basketball.com', '王教练', 1, NULL, 1, 3, 0, 0.00, '2025-10-02 20:42:29', '2025-10-02 20:42:29', NULL);
INSERT INTO `user` VALUES (6, 'coach_li', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900139002', 'li@basketball.com', '李教练', 1, NULL, 1, 3, 0, 0.00, '2025-10-02 20:42:29', '2025-10-02 20:42:29', NULL);
INSERT INTO `user` VALUES (7, 'coach_zhao', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900139003', 'zhao@basketball.com', '赵教练', 2, NULL, 1, 3, 0, 0.00, '2025-10-02 20:42:29', '2025-10-02 20:42:29', NULL);
INSERT INTO `user` VALUES (8, 'coach_sun', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900139004', 'sun@basketball.com', '孙教练', 1, NULL, 1, 3, 0, 0.00, '2025-10-02 20:42:29', '2025-10-02 20:42:29', NULL);
INSERT INTO `user` VALUES (9, 'aqq', '$2a$10$YWeI4lnYXpwLxCYLHFS2SuCZy5G202M6BhqRGJIAPAtjVbBLc/W9q', '17512345678', NULL, NULL, 0, NULL, 1, 0, 0, 0.00, '2025-10-18 02:24:36', '2025-10-18 02:24:36', '2025-10-18 02:55:50');

-- ----------------------------
-- Table structure for user_behavior_log
-- ----------------------------
DROP TABLE IF EXISTS `user_behavior_log`;
CREATE TABLE `user_behavior_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID (未登录则为NULL)',
  `behavior_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行为类型: login/view/booking/payment/cancel等',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模块: user/venue/course/booking/payment',
  `business_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '行为描述',
  `device_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型: web/ios/android/mini-program',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User-Agent',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `behavior_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_time`(`user_id` ASC, `behavior_time` ASC) USING BTREE,
  INDEX `idx_behavior_type`(`behavior_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_module_business`(`module` ASC, `business_id` ASC) USING BTREE,
  INDEX `idx_behavior_user_type_time`(`user_id` ASC, `behavior_type` ASC, `behavior_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 284 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_behavior_log
-- ----------------------------
INSERT INTO `user_behavior_log` VALUES (1, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-13 08:30:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (2, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览场地', '2025-10-13 08:35:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (3, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订场地', '2025-10-13 09:00:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (4, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-13 09:05:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (5, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-14 10:00:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (6, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览课程', '2025-10-14 10:10:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (7, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-15 14:00:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (8, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订场地', '2025-10-15 14:30:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (9, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-15 14:35:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (10, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-16 16:00:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (11, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览场地', '2025-10-16 16:10:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (12, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-17 09:00:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (13, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订课程', '2025-10-17 09:30:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (14, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-17 09:35:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (15, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-18 11:00:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (16, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览场地', '2025-10-18 11:10:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (17, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-19 13:00:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (18, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订场地', '2025-10-19 13:30:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (19, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-19 13:35:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (20, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-20 08:00:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (21, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览课程', '2025-10-20 08:10:00', '2025-10-20 10:52:52');
INSERT INTO `user_behavior_log` VALUES (22, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-13 08:30:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (23, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览场地', '2025-10-13 08:35:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (24, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订场地', '2025-10-13 09:00:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (25, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-13 09:05:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (26, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-14 10:00:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (27, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览课程', '2025-10-14 10:10:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (28, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-15 14:00:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (29, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订场地', '2025-10-15 14:30:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (30, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-15 14:35:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (31, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-16 16:00:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (32, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览场地', '2025-10-16 16:10:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (33, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-17 09:00:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (34, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订课程', '2025-10-17 09:30:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (35, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-17 09:35:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (36, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-18 11:00:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (37, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览场地', '2025-10-18 11:10:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (38, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-19 13:00:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (39, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订场地', '2025-10-19 13:30:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (40, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-19 13:35:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (41, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-20 08:00:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (42, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览课程', '2025-10-20 08:10:00', '2025-10-20 10:59:50');
INSERT INTO `user_behavior_log` VALUES (43, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-13 08:30:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (44, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览场地', '2025-10-13 08:35:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (45, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订场地', '2025-10-13 09:00:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (46, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-13 09:05:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (47, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-14 10:00:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (48, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览课程', '2025-10-14 10:10:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (49, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-15 14:00:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (50, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订场地', '2025-10-15 14:30:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (51, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-15 14:35:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (52, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-16 16:00:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (53, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览场地', '2025-10-16 16:10:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (54, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-17 09:00:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (55, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订课程', '2025-10-17 09:30:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (56, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-17 09:35:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (57, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-18 11:00:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (58, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览场地', '2025-10-18 11:10:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (59, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-19 13:00:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (60, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '预订场地', '2025-10-19 13:30:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (61, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '支付订单', '2025-10-19 13:35:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (62, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '用户登录', '2025-10-20 08:00:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (63, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.100', 'Mozilla/5.0', '浏览课程', '2025-10-20 08:10:00', '2025-10-20 11:02:18');
INSERT INTO `user_behavior_log` VALUES (64, 2, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-09-26 13:50:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (65, 3, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-09-28 10:20:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (66, 4, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-09-29 15:50:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (67, 2, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-03 08:50:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (68, 3, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-03 09:50:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (69, 2, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-03 10:50:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (70, 4, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-03 11:20:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (71, 3, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-02 13:50:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (72, 2, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-01 15:50:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (73, 4, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-03 11:50:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (74, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-18 02:22:58', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (75, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-18 02:46:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (76, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-18 06:13:24', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (77, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-18 23:42:40', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (78, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-19 00:19:42', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (79, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-19 00:40:51', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (80, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-19 00:56:37', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (81, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 09:13:29', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (82, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 09:17:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (83, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 09:34:51', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (84, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 09:40:41', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (85, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 09:54:13', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (86, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:49:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (87, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:49:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (88, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:49:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (89, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:49:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (90, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:49:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (91, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:52:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (92, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:52:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (93, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:52:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (94, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:52:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (95, 9, 'login', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '登录', '2025-10-20 10:52:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (127, 2, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-09-26 13:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (128, 3, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-09-28 10:25:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (129, 4, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-09-29 15:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (130, 2, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-03 08:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (131, 3, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-03 09:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (132, 2, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-03 10:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (133, 4, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-03 11:25:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (134, 3, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-02 13:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (135, 2, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-01 15:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (136, 4, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-03 11:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (137, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-18 02:27:58', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (138, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-18 02:51:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (139, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-18 06:18:24', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (140, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-18 23:47:40', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (141, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-19 00:24:42', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (142, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-19 00:45:51', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (143, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-19 01:01:37', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (144, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 09:18:29', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (145, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 09:22:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (146, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 09:39:51', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (147, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 09:45:41', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (148, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 09:59:13', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (149, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:54:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (150, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:54:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (151, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:54:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (152, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:54:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (153, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:54:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (154, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:57:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (155, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:57:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (156, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:57:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (157, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:57:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (158, 9, 'view', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '浏览场地', '2025-10-20 10:57:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (190, 2, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-09-26 14:00:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (191, 3, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-09-28 10:30:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (192, 4, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-09-29 16:00:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (193, 2, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-03 09:00:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (194, 3, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-03 10:00:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (195, 2, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-03 11:00:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (196, 4, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-03 11:30:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (197, 3, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-02 14:00:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (198, 2, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-01 16:00:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (199, 4, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-03 12:00:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (200, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-18 02:32:58', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (201, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-18 02:56:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (202, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-18 06:23:24', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (203, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-18 23:52:40', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (204, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-19 00:29:42', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (205, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-19 00:50:51', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (206, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-19 01:06:37', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (207, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 09:23:29', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (208, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 09:27:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (209, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 09:44:51', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (210, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 09:50:41', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (211, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 10:04:13', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (212, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 10:59:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (213, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 10:59:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (214, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 10:59:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (215, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 10:59:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (216, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 10:59:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (217, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 11:02:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (218, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 11:02:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (219, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 11:02:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (220, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 11:02:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (221, 9, 'booking', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '预订场地', '2025-10-20 11:02:18', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (253, 2, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-09-26 14:05:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (254, 3, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-09-28 10:35:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (255, 4, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-09-30 18:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (256, 2, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-03 09:05:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (257, 3, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-03 10:02:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (258, 2, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-01 16:05:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (259, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-18 03:14:42', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (260, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-19 01:06:42', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (261, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-20 09:23:45', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (262, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-20 09:34:08', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (263, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-20 09:45:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (264, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-20 09:50:50', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (265, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-20 10:04:37', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (266, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-21 13:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (267, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-22 09:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (268, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-23 15:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (269, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-24 08:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (270, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-25 13:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (271, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-21 13:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (272, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-22 09:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (273, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-23 15:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (274, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-24 08:55:00', '2025-10-20 11:17:35');
INSERT INTO `user_behavior_log` VALUES (275, 9, 'payment', NULL, NULL, NULL, NULL, '192.168.1.1', 'Mozilla/5.0', '支付订单', '2025-10-25 13:55:00', '2025-10-20 11:17:35');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `coupon_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '优惠券码',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-未使用, 1-已使用, 2-已过期',
  `receive_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '使用订单号',
  `start_time` datetime NULL DEFAULT NULL COMMENT '生效时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `coupon_code`(`coupon_code` ASC) USING BTREE,
  INDEX `template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_end_time`(`end_time` ASC) USING BTREE,
  CONSTRAINT `user_coupon_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `coupon_template` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_coupon_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for user_oauth
-- ----------------------------
DROP TABLE IF EXISTS `user_oauth`;
CREATE TABLE `user_oauth`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `oauth_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '第三方类型: wechat/qq/weibo/alipay',
  `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'OpenID',
  `union_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'UnionID (微信)',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方头像',
  `access_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访问令牌',
  `refresh_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '刷新令牌',
  `token_expire_time` datetime NULL DEFAULT NULL COMMENT '令牌过期时间',
  `bind_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-解绑, 1-已绑定',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_oauth`(`oauth_type` ASC, `open_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_union_id`(`union_id` ASC) USING BTREE,
  CONSTRAINT `user_oauth_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '第三方登录绑定表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_oauth
-- ----------------------------

-- ----------------------------
-- Table structure for user_profile_tag
-- ----------------------------
DROP TABLE IF EXISTS `user_profile_tag`;
CREATE TABLE `user_profile_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `tag_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签类型: behavior/preference/consume/risk',
  `tag_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签键',
  `tag_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签值',
  `weight` int NULL DEFAULT 0 COMMENT '标签权重(0-100)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_tag_type`(`tag_type` ASC) USING BTREE,
  INDEX `idx_tag_key`(`tag_key` ASC) USING BTREE,
  CONSTRAINT `user_profile_tag_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户画像标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_profile_tag
-- ----------------------------

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1, 1, '2025-10-01 00:25:39');
INSERT INTO `user_role` VALUES (2, 3, 3, '2025-10-01 00:31:35');
INSERT INTO `user_role` VALUES (3, 2, 4, '2025-10-02 14:35:38');
INSERT INTO `user_role` VALUES (4, 3, 4, '2025-10-02 14:35:38');
INSERT INTO `user_role` VALUES (5, 4, 4, '2025-10-02 14:35:38');
INSERT INTO `user_role` VALUES (11, 5, 3, '2025-10-02 20:42:29');
INSERT INTO `user_role` VALUES (12, 6, 3, '2025-10-02 20:42:29');
INSERT INTO `user_role` VALUES (13, 7, 3, '2025-10-02 20:42:29');
INSERT INTO `user_role` VALUES (14, 8, 3, '2025-10-02 20:42:29');
INSERT INTO `user_role` VALUES (20, 9, 3, '2025-10-18 02:24:36');

-- ----------------------------
-- Table structure for user_session
-- ----------------------------
DROP TABLE IF EXISTS `user_session`;
CREATE TABLE `user_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Session ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '访问令牌',
  `refresh_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '刷新令牌',
  `login_type` tinyint NULL DEFAULT NULL COMMENT '登录类型: 1-账号密码, 2-手机验证码, 3-微信, 4-其他',
  `device_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型: web/ios/android',
  `device_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备ID',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录地点',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User-Agent',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `last_active_time` datetime NULL DEFAULT NULL COMMENT '最后活跃时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-已失效, 1-有效',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `token`(`token` ASC) USING BTREE,
  INDEX `idx_token`(`token` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  CONSTRAINT `user_session_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户Session表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_session
-- ----------------------------

-- ----------------------------
-- Table structure for venue
-- ----------------------------
DROP TABLE IF EXISTS `venue`;
CREATE TABLE `venue`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '场地ID',
  `venue_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场地名称',
  `venue_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '场地编号',
  `venue_type` tinyint NOT NULL COMMENT '场地类型: 1-室内全场, 2-室内半场, 3-室外全场, 4-室外半场',
  `area` decimal(10, 2) NULL DEFAULT NULL COMMENT '面积(平方米)',
  `capacity` int NULL DEFAULT NULL COMMENT '容纳人数',
  `floor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地板类型: 木地板, 塑胶, 水泥等',
  `facilities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '设施描述(JSON格式): 灯光,空调,休息区等',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-不可用, 1-可用, 2-维护中, 3-已删除',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '场地图片',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '多张图片(JSON数组)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '场地描述',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '位置描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `venue_code`(`venue_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_type`(`venue_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '场地表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of venue
-- ----------------------------
INSERT INTO `venue` VALUES (1, 'A1专业篮球场', 'A001', 1, 420.00, 20, '枫木地板', '专业灯光,中央空调,更衣室,淋浴间,休息区,饮水机', 1, 'https://images.unsplash.com/photo-1546519638-68e109498ffc?w=800&h=600&fit=crop', '[\"https://images.unsplash.com/photo-1546519638-68e109498ffc?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1608245449230-4ac19066d2d0?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1519766304817-4f37bda74a26?w=800&h=600&fit=crop\"]', '专业级室内全场，采用国际标准枫木地板，配备专业灯光系统，适合专业比赛和训练。场地宽敞明亮，设施完善。', '1号馆一楼东侧', 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue` VALUES (2, 'A2标准篮球场', 'A002', 1, 420.00, 20, '枫木地板', '专业灯光,中央空调,更衣室,休息区', 1, 'https://images.unsplash.com/photo-1608245449230-4ac19066d2d0?w=800&h=600&fit=crop', '[\"https://images.unsplash.com/photo-1608245449230-4ac19066d2d0?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1519766304817-4f37bda74a26?w=800&h=600&fit=crop\"]', '标准室内全场，地板质量优良，灯光充足，适合各类篮球活动。', '1号馆一楼西侧', 2, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue` VALUES (3, 'B1训练半场', 'B001', 2, 210.00, 10, '塑胶地板', '专业灯光,空调,休息区,饮水机', 1, 'https://images.unsplash.com/photo-1519766304817-4f37bda74a26?w=800&h=600&fit=crop', '[\"https://images.unsplash.com/photo-1519766304817-4f37bda74a26?w=800&h=600&fit=crop\"]', '室内半场，适合小型比赛和日常训练，性价比高。', '2号馆二楼北侧', 3, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue` VALUES (4, 'B2青少年训练场', 'B002', 2, 210.00, 10, '塑胶地板', '专业灯光,空调,休息区', 1, 'https://images.unsplash.com/photo-1517649763962-0c623066013b?w=800&h=600&fit=crop', '[\"https://images.unsplash.com/photo-1517649763962-0c623066013b?w=800&h=600&fit=crop\"]', '专为青少年训练设计的半场，安全舒适。', '2号馆二楼南侧', 4, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue` VALUES (5, 'C1室外标准场', 'C001', 3, 420.00, 22, '硅PU', '夜间灯光,看台,休息区', 1, 'https://images.unsplash.com/photo-1504450758481-7338eba7524a?w=800&h=600&fit=crop', '[\"https://images.unsplash.com/photo-1504450758481-7338eba7524a?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1515523110800-9415d13b84a8?w=800&h=600&fit=crop\"]', '室外标准全场，硅PU地面，夜间有专业照明，适合各类户外活动。', '室外运动区A区', 5, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue` VALUES (6, 'C2阳光篮球场', 'C002', 3, 420.00, 22, '硅PU', '夜间灯光,休息区', 1, 'https://images.unsplash.com/photo-1515523110800-9415d13b84a8?w=800&h=600&fit=crop', '[\"https://images.unsplash.com/photo-1515523110800-9415d13b84a8?w=800&h=600&fit=crop\"]', '开阔的室外场地，视野良好，适合阳光充足的日间活动。', '室外运动区B区', 6, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue` VALUES (7, 'D1街头篮球场', 'D001', 4, 210.00, 12, '硅PU', '夜间灯光,休息区', 1, 'https://images.unsplash.com/photo-1529654423582-310c920228e1?w=800&h=600&fit=crop', '[\"https://images.unsplash.com/photo-1529654423582-310c920228e1?w=800&h=600&fit=crop\"]', '街头风格的室外半场，适合三人篮球和自由对抗。', '室外运动区C区', 7, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue` VALUES (8, 'D2休闲半场', 'D002', 4, 210.00, 12, '硅PU', '夜间灯光', 1, 'https://images.unsplash.com/photo-1627627256672-027a4613d028?w=800&h=600&fit=crop', '[\"https://images.unsplash.com/photo-1627627256672-027a4613d028?w=800&h=600&fit=crop\"]', '休闲娱乐的室外半场，适合朋友聚会打球。', '室外运动区D区', 8, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue` VALUES (9, 'E1维护场地', 'E001', 1, 420.00, 20, '枫木地板', '专业灯光,中央空调,更衣室', 2, 'https://images.unsplash.com/photo-1546519638-68e109498ffc?w=800&h=600&fit=crop', '[\"https://images.unsplash.com/photo-1546519638-68e109498ffc?w=800&h=600&fit=crop\"]', '场地维护中，暂时不可预订。预计一周后恢复使用。', '1号馆二楼', 9, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue` VALUES (10, 'F1已删除场地', 'F001', 1, 420.00, 20, '枫木地板', '专业灯光,中央空调', 3, 'https://images.unsplash.com/photo-1546519638-68e109498ffc?w=800&h=600&fit=crop', '[]', '此场地已不再使用。', '3号馆', 10, '2025-10-02 20:49:48', '2025-10-02 20:49:48');

-- ----------------------------
-- Table structure for venue_maintenance
-- ----------------------------
DROP TABLE IF EXISTS `venue_maintenance`;
CREATE TABLE `venue_maintenance`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '维护记录ID',
  `venue_id` bigint NOT NULL COMMENT '场地ID',
  `maintenance_type` tinyint NULL DEFAULT NULL COMMENT '维护类型: 1-日常维护, 2-设备维修, 3-清洁, 4-其他',
  `maintenance_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '维护内容',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `cost` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '维护费用',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-计划中, 1-进行中, 2-已完成',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_venue_id`(`venue_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `venue_maintenance_ibfk_1` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `venue_maintenance_ibfk_2` FOREIGN KEY (`operator_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '场地维护记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of venue_maintenance
-- ----------------------------

-- ----------------------------
-- Table structure for venue_price
-- ----------------------------
DROP TABLE IF EXISTS `venue_price`;
CREATE TABLE `venue_price`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '价格ID',
  `venue_id` bigint NOT NULL COMMENT '场地ID',
  `time_type` tinyint NOT NULL COMMENT '时间类型: 1-工作日, 2-周末, 3-节假日',
  `time_period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时段: 如 08:00-12:00',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `price` decimal(10, 2) NOT NULL COMMENT '标准价格(元/小时)',
  `member_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '会员价格',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id`(`venue_id` ASC) USING BTREE,
  INDEX `idx_time`(`time_type` ASC, `start_time` ASC, `end_time` ASC) USING BTREE,
  CONSTRAINT `venue_price_ibfk_1` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 117 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '场地价格表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of venue_price
-- ----------------------------
INSERT INTO `venue_price` VALUES (85, 1, 1, '08:00-12:00', '08:00:00', '12:00:00', 120.00, 100.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (86, 1, 1, '12:00-18:00', '12:00:00', '18:00:00', 100.00, 85.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (87, 1, 1, '18:00-22:00', '18:00:00', '22:00:00', 150.00, 130.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (88, 1, 2, '08:00-12:00', '08:00:00', '12:00:00', 150.00, 130.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (89, 1, 2, '12:00-18:00', '12:00:00', '18:00:00', 130.00, 110.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (90, 1, 2, '18:00-22:00', '18:00:00', '22:00:00', 180.00, 160.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (91, 1, 3, '08:00-22:00', '08:00:00', '22:00:00', 200.00, 180.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (92, 2, 1, '08:00-12:00', '08:00:00', '12:00:00', 100.00, 85.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (93, 2, 1, '12:00-18:00', '12:00:00', '18:00:00', 80.00, 70.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (94, 2, 1, '18:00-22:00', '18:00:00', '22:00:00', 120.00, 100.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (95, 2, 2, '08:00-22:00', '08:00:00', '22:00:00', 130.00, 110.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (96, 2, 3, '08:00-22:00', '08:00:00', '22:00:00', 160.00, 140.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (97, 3, 1, '08:00-18:00', '08:00:00', '18:00:00', 60.00, 50.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (98, 3, 1, '18:00-22:00', '18:00:00', '22:00:00', 80.00, 70.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (99, 3, 2, '08:00-22:00', '08:00:00', '22:00:00', 90.00, 75.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (100, 3, 3, '08:00-22:00', '08:00:00', '22:00:00', 100.00, 85.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (101, 4, 1, '08:00-18:00', '08:00:00', '18:00:00', 55.00, 45.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (102, 4, 1, '18:00-22:00', '18:00:00', '22:00:00', 75.00, 65.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (103, 4, 2, '08:00-22:00', '08:00:00', '22:00:00', 85.00, 70.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (104, 5, 1, '08:00-18:00', '08:00:00', '18:00:00', 70.00, 60.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (105, 5, 1, '18:00-22:00', '18:00:00', '22:00:00', 90.00, 75.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (106, 5, 2, '08:00-22:00', '08:00:00', '22:00:00', 100.00, 85.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (107, 5, 3, '08:00-22:00', '08:00:00', '22:00:00', 120.00, 100.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (108, 6, 1, '08:00-18:00', '08:00:00', '18:00:00', 65.00, 55.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (109, 6, 1, '18:00-22:00', '18:00:00', '22:00:00', 85.00, 70.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (110, 6, 2, '08:00-22:00', '08:00:00', '22:00:00', 95.00, 80.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (111, 7, 1, '08:00-18:00', '08:00:00', '18:00:00', 45.00, 40.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (112, 7, 1, '18:00-22:00', '18:00:00', '22:00:00', 60.00, 50.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (113, 7, 2, '08:00-22:00', '08:00:00', '22:00:00', 70.00, 60.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (114, 8, 1, '08:00-18:00', '08:00:00', '18:00:00', 40.00, 35.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (115, 8, 1, '18:00-22:00', '18:00:00', '22:00:00', 55.00, 45.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');
INSERT INTO `venue_price` VALUES (116, 8, 2, '08:00-22:00', '08:00:00', '22:00:00', 65.00, 55.00, 1, '2025-10-02 20:49:48', '2025-10-02 20:49:48');

-- ----------------------------
-- Table structure for venue_schedule
-- ----------------------------
DROP TABLE IF EXISTS `venue_schedule`;
CREATE TABLE `venue_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `venue_id` bigint NOT NULL COMMENT '场地ID',
  `schedule_date` date NOT NULL COMMENT '日期',
  `time_slot` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时段: 08:00-09:00',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-不可用, 1-可用, 2-已预订, 3-维护',
  `booking_id` bigint NULL DEFAULT NULL COMMENT '预订ID(如果已预订)',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '该时段价格',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_venue_date_time`(`venue_id` ASC, `schedule_date` ASC, `start_time` ASC) USING BTREE,
  INDEX `idx_date`(`schedule_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `venue_schedule_ibfk_1` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '场地时段表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of venue_schedule
-- ----------------------------

-- ----------------------------
-- Table structure for venue_usage_analysis
-- ----------------------------
DROP TABLE IF EXISTS `venue_usage_analysis`;
CREATE TABLE `venue_usage_analysis`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分析ID',
  `venue_id` bigint NOT NULL COMMENT '场地ID',
  `analysis_date` date NOT NULL COMMENT '分析日期',
  `total_bookings` int NULL DEFAULT 0 COMMENT '总预订次数',
  `success_bookings` int NULL DEFAULT 0 COMMENT '成功预订次数',
  `cancelled_bookings` int NULL DEFAULT 0 COMMENT '取消预订次数',
  `unique_users` int NULL DEFAULT 0 COMMENT '预订人数(去重)',
  `booking_conversion_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '预订转化率(%)',
  `available_slots` int NULL DEFAULT 10 COMMENT '可用时段数',
  `booked_slots` int NULL DEFAULT 0 COMMENT '已预订时段数',
  `usage_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '使用率(%)',
  `revenue` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '收入金额',
  `avg_order_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '平均客单价',
  `peak_period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '高峰时段: morning-上午, afternoon-下午, evening-晚上',
  `peak_bookings` int NULL DEFAULT 0 COMMENT '高峰时段预订数',
  `venue_rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '场地评分(0-5分)',
  `rating_count` int NULL DEFAULT 0 COMMENT '评分数量',
  `usage_score` int NULL DEFAULT 0 COMMENT '使用得分(0-100)',
  `trend` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '趋势: up-上升, down-下降, stable-稳定',
  `trend_change` int NULL DEFAULT 0 COMMENT '趋势变化值',
  `suggestion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '运营建议',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_venue_date`(`venue_id` ASC, `analysis_date` ASC) USING BTREE,
  INDEX `idx_analysis_date`(`analysis_date` ASC) USING BTREE,
  INDEX `idx_usage_rate`(`usage_rate` DESC) USING BTREE,
  INDEX `idx_usage_score`(`usage_score` DESC) USING BTREE,
  CONSTRAINT `venue_usage_analysis_ibfk_1` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '场地使用分析表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of venue_usage_analysis
-- ----------------------------
INSERT INTO `venue_usage_analysis` VALUES (1, 1, '2025-10-17', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-18 03:00:00', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (2, 2, '2025-10-17', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-18 03:00:00', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (3, 3, '2025-10-17', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-18 03:00:00', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (4, 4, '2025-10-17', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-18 03:00:00', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (5, 5, '2025-10-17', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-18 03:00:00', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (6, 6, '2025-10-17', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-18 03:00:00', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (7, 7, '2025-10-17', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-18 03:00:00', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (8, 8, '2025-10-17', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-18 03:00:00', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (9, 1, '2025-10-19', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'down', -17, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:36:34', '2025-10-20 18:15:00');
INSERT INTO `venue_usage_analysis` VALUES (10, 2, '2025-10-19', 1, 0, 1, 1, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠; 取消率较高,建议分析取消原因并改进服务', '2025-10-20 10:36:34', '2025-10-20 18:15:00');
INSERT INTO `venue_usage_analysis` VALUES (11, 3, '2025-10-19', 1, 0, 1, 1, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠; 取消率较高,建议分析取消原因并改进服务', '2025-10-20 10:36:34', '2025-10-20 18:15:00');
INSERT INTO `venue_usage_analysis` VALUES (12, 4, '2025-10-19', 1, 1, 0, 1, 100.00, 10, 1, 10.00, 110.00, 110.00, 'afternoon', 1, 0.00, 0, 32, 'up', 32, '使用率较低,建议增加营销推广或调整价格策略', '2025-10-20 10:36:34', '2025-10-20 18:15:00');
INSERT INTO `venue_usage_analysis` VALUES (13, 5, '2025-10-19', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:36:34', '2025-10-20 18:15:00');
INSERT INTO `venue_usage_analysis` VALUES (14, 6, '2025-10-19', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:36:34', '2025-10-20 18:15:00');
INSERT INTO `venue_usage_analysis` VALUES (15, 7, '2025-10-19', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:36:34', '2025-10-20 18:15:00');
INSERT INTO `venue_usage_analysis` VALUES (16, 8, '2025-10-19', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:36:34', '2025-10-20 18:15:00');
INSERT INTO `venue_usage_analysis` VALUES (17, 1, '2025-10-20', 7, 1, 0, 1, 14.29, 10, 1, 10.00, 400.00, 400.00, 'morning', 1, 0.00, 0, 18, 'up', 18, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠; 上午时段最受欢迎,可考虑推出早鸟优惠', '2025-10-20 10:36:34', '2025-10-20 11:09:25');
INSERT INTO `venue_usage_analysis` VALUES (18, 2, '2025-10-20', 2, 0, 0, 1, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:36:34', '2025-10-20 11:09:25');
INSERT INTO `venue_usage_analysis` VALUES (19, 3, '2025-10-20', 4, 2, 0, 1, 50.00, 10, 2, 20.00, 300.00, 150.00, 'morning', 2, 0.00, 0, 29, 'up', 29, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠; 上午时段最受欢迎,可考虑推出早鸟优惠', '2025-10-20 10:36:34', '2025-10-20 11:09:25');
INSERT INTO `venue_usage_analysis` VALUES (20, 4, '2025-10-20', 1, 1, 0, 1, 100.00, 10, 1, 10.00, 165.00, 165.00, 'morning', 1, 0.00, 0, 34, 'stable', 2, '使用率较低,建议增加营销推广或调整价格策略; 上午时段最受欢迎,可考虑推出早鸟优惠', '2025-10-20 10:36:34', '2025-10-20 11:09:25');
INSERT INTO `venue_usage_analysis` VALUES (21, 5, '2025-10-20', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:36:34', '2025-10-20 11:09:25');
INSERT INTO `venue_usage_analysis` VALUES (22, 6, '2025-10-20', 1, 1, 0, 1, 100.00, 10, 1, 10.00, 130.00, 130.00, 'morning', 1, 0.00, 0, 33, 'up', 33, '使用率较低,建议增加营销推广或调整价格策略; 上午时段最受欢迎,可考虑推出早鸟优惠', '2025-10-20 10:36:34', '2025-10-20 11:09:25');
INSERT INTO `venue_usage_analysis` VALUES (23, 7, '2025-10-20', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:36:34', '2025-10-20 11:09:25');
INSERT INTO `venue_usage_analysis` VALUES (24, 8, '2025-10-20', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:36:34', '2025-10-20 11:09:25');
INSERT INTO `venue_usage_analysis` VALUES (25, 1, '2025-10-13', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (26, 2, '2025-10-13', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (27, 3, '2025-10-13', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (28, 4, '2025-10-13', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (29, 5, '2025-10-13', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (30, 6, '2025-10-13', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (31, 7, '2025-10-13', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (32, 8, '2025-10-13', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (33, 1, '2025-10-14', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (34, 2, '2025-10-14', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (35, 3, '2025-10-14', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (36, 4, '2025-10-14', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (37, 5, '2025-10-14', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (38, 6, '2025-10-14', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (39, 7, '2025-10-14', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (40, 8, '2025-10-14', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:57', '2025-10-20 10:37:57');
INSERT INTO `venue_usage_analysis` VALUES (41, 1, '2025-10-15', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (42, 2, '2025-10-15', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (43, 3, '2025-10-15', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (44, 4, '2025-10-15', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (45, 5, '2025-10-15', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (46, 6, '2025-10-15', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (47, 7, '2025-10-15', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (48, 8, '2025-10-15', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (49, 1, '2025-10-16', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (50, 2, '2025-10-16', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (51, 3, '2025-10-16', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (52, 4, '2025-10-16', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (53, 5, '2025-10-16', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (54, 6, '2025-10-16', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (55, 7, '2025-10-16', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (56, 8, '2025-10-16', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:58', '2025-10-20 10:37:58');
INSERT INTO `venue_usage_analysis` VALUES (57, 1, '2025-10-18', 2, 1, 1, 1, 50.00, 10, 1, 10.00, 0.00, 0.00, 'morning', 1, 0.00, 0, 17, 'up', 17, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠; 取消率较高,建议分析取消原因并改进服务; 上午时段最受欢迎,可考虑推出早鸟优惠', '2025-10-20 10:37:59', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (58, 2, '2025-10-18', 2, 0, 2, 1, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠; 取消率较高,建议分析取消原因并改进服务', '2025-10-20 10:37:59', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (59, 3, '2025-10-18', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:59', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (60, 4, '2025-10-18', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:59', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (61, 5, '2025-10-18', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:59', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (62, 6, '2025-10-18', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:59', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (63, 7, '2025-10-18', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:59', '2025-10-20 10:37:59');
INSERT INTO `venue_usage_analysis` VALUES (64, 8, '2025-10-18', 0, 0, 0, 0, 0.00, 10, 0, 0.00, 0.00, 0.00, 'afternoon', 0, 0.00, 0, 0, 'stable', 0, '使用率较低,建议增加营销推广或调整价格策略; 预订转化率偏低,建议优化预订流程或提供优惠', '2025-10-20 10:37:59', '2025-10-20 10:37:59');

-- ----------------------------
-- View structure for v_course_popularity_rank
-- ----------------------------
DROP VIEW IF EXISTS `v_course_popularity_rank`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_course_popularity_rank` AS select `c`.`id` AS `course_id`,`c`.`course_name` AS `course_name`,`c`.`course_type` AS `course_type`,`cpa`.`analysis_date` AS `analysis_date`,`cpa`.`enroll_count` AS `enroll_count`,`cpa`.`completion_rate` AS `completion_rate`,`cpa`.`avg_rating` AS `avg_rating`,`cpa`.`revenue` AS `revenue`,`cpa`.`popularity_score` AS `popularity_score`,`cpa`.`ranking` AS `ranking` from (`course` `c` join `course_popularity_analysis` `cpa` on((`c`.`id` = `cpa`.`course_id`))) where (`cpa`.`analysis_date` = (select max(`course_popularity_analysis`.`analysis_date`) from `course_popularity_analysis`)) order by `cpa`.`ranking`;

-- ----------------------------
-- View structure for v_daily_member_activity
-- ----------------------------
DROP VIEW IF EXISTS `v_daily_member_activity`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_daily_member_activity` AS select `member_activity_analysis`.`analysis_date` AS `analysis_date`,count(0) AS `total_members`,sum((case when (`member_activity_analysis`.`activity_score` >= 60) then 1 else 0 end)) AS `active_members`,sum((case when (`member_activity_analysis`.`activity_score` < 30) then 1 else 0 end)) AS `inactive_members`,sum((case when (`member_activity_analysis`.`churn_risk` >= 2) then 1 else 0 end)) AS `high_churn_risk`,avg(`member_activity_analysis`.`activity_score`) AS `avg_activity_score`,sum(`member_activity_analysis`.`payment_amount`) AS `total_payment` from `member_activity_analysis` group by `member_activity_analysis`.`analysis_date`;

-- ----------------------------
-- View structure for v_daily_revenue
-- ----------------------------
DROP VIEW IF EXISTS `v_daily_revenue`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_daily_revenue` AS select cast(`payment`.`pay_time` as date) AS `stat_date`,`payment`.`order_type` AS `order_type`,count(0) AS `order_count`,sum(`payment`.`amount`) AS `total_amount` from `payment` where (`payment`.`pay_status` = 1) group by cast(`payment`.`pay_time` as date),`payment`.`order_type`;

-- ----------------------------
-- View structure for v_user_statistics
-- ----------------------------
DROP VIEW IF EXISTS `v_user_statistics`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_user_statistics` AS select cast(`user`.`create_time` as date) AS `register_date`,count(0) AS `new_users`,sum((case when (`user`.`member_level` > 0) then 1 else 0 end)) AS `member_count` from `user` group by cast(`user`.`create_time` as date);

-- ----------------------------
-- View structure for v_venue_revenue_stats
-- ----------------------------
DROP VIEW IF EXISTS `v_venue_revenue_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_venue_revenue_stats` AS select `v`.`id` AS `venue_id`,`v`.`venue_name` AS `venue_name`,`v`.`venue_type` AS `venue_type`,date_format(`vua`.`analysis_date`,'%Y-%m') AS `stat_month`,sum(`vua`.`revenue`) AS `monthly_revenue`,avg(`vua`.`usage_rate`) AS `avg_usage_rate`,sum(`vua`.`total_bookings`) AS `total_bookings` from (`venue` `v` join `venue_usage_analysis` `vua` on((`v`.`id` = `vua`.`venue_id`))) group by `v`.`id`,date_format(`vua`.`analysis_date`,'%Y-%m');

-- ----------------------------
-- View structure for v_venue_usage
-- ----------------------------
DROP VIEW IF EXISTS `v_venue_usage`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_venue_usage` AS select `v`.`id` AS `venue_id`,`v`.`venue_name` AS `venue_name`,cast(`b`.`booking_date` as date) AS `stat_date`,count(`b`.`id`) AS `booking_count`,sum(`b`.`duration`) AS `total_hours`,sum(`b`.`actual_price`) AS `total_revenue` from (`venue` `v` left join `booking` `b` on(((`v`.`id` = `b`.`venue_id`) and (`b`.`status` = 3)))) group by `v`.`id`,cast(`b`.`booking_date` as date);

SET FOREIGN_KEY_CHECKS = 1;
