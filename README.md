# 🏀 篮球场馆管理系统

> 一个功能完善的篮球场馆预订与管理系统，支持场地预订、课程管理、会员服务、支付结算等全流程业务管理。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4.0-4FC08D.svg)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📑 目录

- [功能特性](#-功能特性)
- [技术架构](#️-技术架构)
- [快速开始](#-快速开始)
- [配置说明](#️-配置说明)
- [详细配置](#-详细配置)
- [功能模块详解](#-功能模块详解)
- [开发指南](#️-开发指南)
- [更新日志](#-更新日志)
- [贡献指南](#-贡献指南)
- [许可证](#-许可证)
- [联系方式](#-联系方式)

## ✨ 功能特性

### 🎯 用户端功能
- **用户管理**: 注册登录、微信登录、个人资料管理
- **场地预订**: 实时查看场地状态、在线预订、预订管理
- **课程报名**: 浏览课程信息、在线报名、我的课程
- **会员服务**: 会员卡购买、积分管理、余额充值
- **支付系统**: 支持微信支付、支付宝、余额支付、会员卡支付
- **消息通知**: 系统公告、预订提醒、课程通知
- **个人中心**: 预订记录、消费记录、积分明细

### 🛠️ 管理端功能
- **场地管理**: 场地信息维护、价格设置、时段管理
- **预订管理**: 预订审核、状态管理、退款处理
- **课程管理**: 课程发布、教练安排、学员管理
- **会员管理**: 会员信息、会员卡管理、积分规则
- **用户管理**: 用户信息、权限管理、行为分析
- **财务管理**: 收支统计、财务报表、对账管理
- **数据分析**: 场地使用率、收入分析、用户画像
- **系统管理**: 公告管理、日志管理、系统配置

## 🏗️ 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0 + MyBatis-Plus 3.5.3
- **安全**: JWT Token + BCrypt密码加密
- **支付**: 微信支付V3 + 支付宝SDK
- **工具**: Lombok + Hutool + FastJSON
- **文档**: SpringDoc OpenAPI (Swagger)
- **日志**: Spring AOP + 操作日志记录

### 前端技术栈
- **框架**: Vue 3.4.0 + Composition API
- **UI组件**: Element Plus 2.4.4
- **状态管理**: Pinia 2.1.7
- **路由**: Vue Router 4.2.5
- **构建工具**: Vite 5.0.0
- **HTTP客户端**: Axios 1.6.2
- **图表**: ECharts 6.0.0
- **样式**: Sass 1.69.5

### 数据库设计
- **52张数据表**，涵盖完整业务流程
- **用户体系**: 用户信息、角色权限、登录日志
- **业务核心**: 场地、预订、课程、会员、支付
- **运营支持**: 公告、通知、积分、财务记录
- **系统管理**: 操作日志、行为分析、系统配置

### 项目规模
- **后端代码**: 27个Controller，完整的RESTful API
- **前端页面**: 40+个Vue组件，响应式设计
- **数据库表**: 52张表，完整的业务数据模型
- **功能模块**: 10大核心业务模块
- **支付集成**: 微信支付V3 + 支付宝SDK

## 🚀 快速开始

### 环境要求

- **Java**: JDK 17+
- **Node.js**: 16.0+
- **MySQL**: 8.0+
- **Maven**: 3.6+

### 安装步骤

#### 1. 克隆项目
```bash
git clone https://github.com/your-username/basketball-system.git
cd basketball-system
```

#### 2. 数据库配置
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE basketball_system CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

# 导入数据
mysql -u root -p basketball_system < basketball_system.sql
```

#### 3. 后端配置
```bash
cd basketball-system

# 修改数据库配置
# 编辑 src/main/resources/application.yml
# 配置数据库连接信息

# 安装依赖并启动
mvn clean install
mvn spring-boot:run
```

#### 4. 前端配置
```bash
cd basketball-web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 访问地址

- **前端地址**: http://localhost:5173
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui/index.html

### 默认账号

#### 管理员账号
- **用户名**: admin
- **密码**: admin123
- **权限**: 系统管理员，拥有所有功能权限

#### 测试用户
- **用户名**: user
- **密码**: user123
- **权限**: 普通用户，可进行预订和会员操作

> ⚠️ **安全提醒**: 生产环境请务必修改默认密码！

## ⚙️ 配置说明

### 数据库配置

在 `basketball-system/src/main/resources/application.yml` 中配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/basketball_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 服务器配置

```yaml
server:
  port: 8080
  servlet:
    context-path: /
```

### 日志配置

```yaml
logging:
  level:
    com.basketball: info
    com.basketball.mapper: debug
```

## 📱 系统截图

### 用户端界面
- 首页展示场地信息和最新公告
- 场地列表支持筛选和实时状态查看
- 预订流程简洁明了，支持多种支付方式
- 个人中心功能完善，信息管理便捷

### 管理端界面
- Dashboard数据大屏，关键指标一目了然
- 场地管理支持批量操作和状态监控
- 预订管理提供完整的订单生命周期管理
- 数据分析模块提供多维度统计报表

## 🎯 核心功能详解

### 预订系统
- **实时状态**: 场地状态实时更新，避免冲突预订
- **智能定价**: 支持时段差价、会员折扣、活动优惠
- **灵活支付**: 多种支付方式，支持组合支付
- **订单管理**: 完整的订单生命周期管理

### 会员系统
- **会员等级**: 多级会员体系，不同权益
- **积分机制**: 消费积分、签到积分、推荐奖励
- **会员卡**: 储值卡、次卡、月卡等多种类型
- **优惠活动**: 新用户优惠、节日活动、会员专享

### 支付系统
- **微信支付**: 支持扫码支付、H5支付
- **支付宝**: 支持网页支付、手机支付
- **余额支付**: 账户余额充值和消费
- **会员卡**: 会员卡余额和次数扣减

## 🔧 详细配置

### 微信登录配置

在 `application.yml` 中配置微信开放平台参数：

```yaml
wechat:
  oauth:
    enabled: true  # 启用微信登录
    app-id: YOUR_WECHAT_APP_ID  # 微信开放平台AppID
    app-secret: YOUR_WECHAT_APP_SECRET  # 微信开放平台AppSecret
    redirect-uri: http://yourdomain.com/api/wechat/auth/callback  # 授权回调地址
```

**配置步骤**:
1. 在微信开放平台申请应用
2. 获取AppID和AppSecret
3. 配置授权回调域名
4. 更新配置文件并重启服务

### 微信支付配置

```yaml
wechat:
  pay:
    enabled: true  # 启用微信支付
    merchant-id: 1234567890  # 商户号
    private-key-path: /path/to/apiclient_key.pem  # 商户私钥路径
    merchant-serial-number: SERIAL_NUMBER_HERE  # 商户证书序列号
    api-v3-key: YOUR_API_V3_KEY_HERE  # APIv3密钥
    app-id: wx1234567890abcdef  # 公众号/小程序AppID
    notify-url: https://yourdomain.com/api/payment/callback/wechat  # 回调地址
```

**配置步骤**:
1. 开通微信商户平台账号
2. 下载商户证书和私钥
3. 配置API密钥和回调地址
4. 测试支付流程

### 支付宝配置

```yaml
alipay:
  enabled: true  # 启用支付宝
  gateway-url: https://openapi.alipay.com/gateway.do  # 正式环境
  # gateway-url: https://openapi.alipaydev.com/gateway.do  # 沙箱环境
  app-id: 2021000000000000  # 应用ID
  private-key: YOUR_PRIVATE_KEY_HERE  # 商户私钥
  alipay-public-key: YOUR_ALIPAY_PUBLIC_KEY_HERE  # 支付宝公钥
  sign-type: RSA2  # 签名类型
  charset: UTF-8  # 字符编码
  format: json  # 返回格式
  return-url: https://yourdomain.com/api/payment/alipay/return  # 同步回调
  notify-url: https://yourdomain.com/api/payment/callback/alipay  # 异步通知
```

### 其他配置项

#### JWT配置
```yaml
jwt:
  secret: your-secret-key  # JWT密钥
  expiration: 86400  # 过期时间(秒)
```

#### 文件上传配置
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB  # 单文件最大大小
      max-request-size: 50MB  # 请求最大大小
```

## 📋 功能模块详解

### 用户管理模块

#### 用户注册登录
- **手机号注册**: 支持短信验证码验证
- **微信登录**: 一键微信授权登录
- **密码登录**: 传统用户名密码登录
- **忘记密码**: 短信验证码重置密码

#### 用户信息管理
- **基本信息**: 昵称、头像、性别、生日
- **联系方式**: 手机号、邮箱、地址
- **实名认证**: 身份证验证(可选)
- **安全设置**: 密码修改、登录设备管理

### 场地管理模块

#### 场地信息
- **基本信息**: 场地名称、类型、位置、容量
- **设施配置**: 灯光、音响、更衣室、停车位
- **价格设置**: 分时段定价、会员价格、节假日价格
- **状态管理**: 开放状态、维护状态、预订状态

#### 时段管理
- **营业时间**: 每日营业时间设置
- **时段划分**: 灵活的时段划分规则
- **特殊日期**: 节假日、维护日特殊设置
- **价格策略**: 峰谷时段差异化定价

### 预订管理模块

#### 预订流程
1. **选择场地**: 浏览可用场地和时段
2. **确认信息**: 填写联系人和备注信息
3. **选择支付**: 多种支付方式选择
4. **完成支付**: 支付成功后生成订单
5. **签到使用**: 到场签到开始使用

#### 订单状态
- **待支付**: 30分钟内完成支付
- **已支付**: 等待使用时间到达
- **已完成**: 正常使用完成
- **已取消**: 用户主动取消或超时取消
- **已退款**: 退款处理完成

#### 退款规则
- **提前24小时**: 全额退款
- **提前2-24小时**: 扣除20%手续费
- **提前2小时内**: 扣除50%手续费
- **开始后**: 不支持退款

### 课程管理模块

#### 课程设置
- **课程信息**: 名称、描述、难度等级、适合人群
- **教练安排**: 教练信息、资质认证、教学经验
- **时间安排**: 上课时间、课程时长、招生人数
- **价格设置**: 单次价格、套餐价格、会员优惠

#### 报名管理
- **在线报名**: 选择课程和时间段报名
- **名额管理**: 实时显示剩余名额
- **候补机制**: 满员后支持候补排队
- **签到管理**: 上课前签到确认出勤

### 会员系统模块

#### 会员等级
- **普通会员**: 基础折扣和积分
- **银卡会员**: 更多优惠和特权
- **金卡会员**: 最高等级优惠
- **升级规则**: 基于消费金额和积分

#### 会员卡类型
- **储值卡**: 充值金额享受折扣
- **次卡**: 购买使用次数
- **月卡**: 月度无限使用
- **年卡**: 年度会员特权

#### 积分系统
- **获取积分**: 消费、签到、推荐好友
- **积分用途**: 抵扣现金、兑换礼品、升级会员
- **积分规则**: 1元=1积分，100积分=1元
- **有效期**: 积分2年有效期

### 支付系统模块

#### 支付方式
- **微信支付**: 扫码支付、H5支付
- **支付宝**: 网页支付、手机支付
- **余额支付**: 账户余额扣减
- **会员卡**: 会员卡余额/次数扣减
- **组合支付**: 多种方式组合使用

#### 支付流程
1. **创建订单**: 生成预支付订单
2. **选择支付**: 用户选择支付方式
3. **调用支付**: 调用第三方支付接口
4. **支付回调**: 处理支付结果通知
5. **订单更新**: 更新订单状态和用户余额

### 通知系统模块

#### 公告管理
- **系统通知**: 系统维护、功能更新
- **活动公告**: 优惠活动、节日活动
- **维护公告**: 场地维护、设备更新
- **紧急通知**: 突发事件、重要通知

#### 消息推送
- **预订提醒**: 预订成功、使用提醒
- **课程通知**: 课程开始、取消通知
- **支付通知**: 支付成功、退款通知
- **会员消息**: 积分变动、等级升级

### 数据统计模块

#### 场地分析
- **使用率统计**: 各场地使用率对比
- **收入分析**: 场地收入趋势分析
- **时段分析**: 热门时段和冷门时段
- **用户偏好**: 用户场地选择偏好

#### 用户分析
- **用户画像**: 年龄、性别、消费习惯
- **活跃度分析**: 用户活跃度和留存率
- **消费分析**: 用户消费能力和频次
- **行为分析**: 用户行为路径分析

#### 财务分析
- **收入统计**: 日、周、月、年收入统计
- **支付方式**: 各支付方式使用情况
- **退款统计**: 退款金额和原因分析
- **成本分析**: 运营成本和利润分析

## 🛠️ 开发指南

### 项目结构

```
basketball-system/
├── basketball-system/          # 后端项目
│   ├── src/main/java/com/basketball/
│   │   ├── annotation/         # 自定义注解
│   │   ├── aspect/            # AOP切面
│   │   ├── common/            # 公共类
│   │   ├── config/            # 配置类
│   │   ├── controller/        # 控制器
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类
│   │   ├── enums/             # 枚举类
│   │   ├── interceptor/       # 拦截器
│   │   ├── mapper/            # MyBatis映射器
│   │   ├── resolver/          # 参数解析器
│   │   ├── scheduled/         # 定时任务
│   │   ├── security/          # 安全相关
│   │   ├── service/           # 业务逻辑层
│   │   ├── task/              # 异步任务
│   │   ├── utils/             # 工具类
│   │   └── vo/                # 视图对象
│   ├── src/main/resources/
│   │   ├── application.yml    # 配置文件
│   │   └── sql/               # SQL脚本
│   └── pom.xml                # Maven配置
├── basketball-web/             # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 公共组件
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   ├── styles/            # 样式文件
│   │   ├── utils/             # 工具函数
│   │   └── views/             # 页面组件
│   ├── package.json           # 依赖配置
│   └── vite.config.js         # 构建配置
└── basketball_system.sql      # 数据库脚本
```

### API接口文档

#### 用户相关接口

```http
# 用户注册
POST /api/user/register
Content-Type: application/json

{
  "username": "string",
  "password": "string",
  "phone": "string",
  "smsCode": "string"
}

# 用户登录
POST /api/user/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}

# 获取用户信息
GET /api/user/profile
Authorization: Bearer {token}
```

#### 场地相关接口

```http
# 获取场地列表
GET /api/venue/list?page=1&size=10&type=1

# 获取场地详情
GET /api/venue/{id}

# 获取场地可用时段
GET /api/venue/{id}/available-times?date=2025-10-22
```

#### 预订相关接口

```http
# 创建预订
POST /api/booking/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "venueId": 1,
  "bookingDate": "2025-10-22",
  "startTime": "09:00",
  "endTime": "11:00",
  "contactName": "张三",
  "contactPhone": "13800138000"
}

# 获取预订列表
GET /api/booking/list?page=1&size=10&status=1
Authorization: Bearer {token}

# 取消预订
PUT /api/booking/{id}/cancel
Authorization: Bearer {token}
```

### 数据库设计

#### 核心表结构

**用户表 (user)**
```sql
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) COMMENT '密码',
  `phone` varchar(11) COMMENT '手机号',
  `email` varchar(100) COMMENT '邮箱',
  `nickname` varchar(50) COMMENT '昵称',
  `avatar` varchar(255) COMMENT '头像',
  `gender` tinyint DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
  `birthday` date COMMENT '生日',
  `status` tinyint DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `phone` (`phone`)
);
```

**场地表 (venue)**
```sql
CREATE TABLE `venue` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '场地ID',
  `name` varchar(100) NOT NULL COMMENT '场地名称',
  `type` tinyint NOT NULL COMMENT '场地类型: 1-室内, 2-室外',
  `location` varchar(200) COMMENT '位置',
  `capacity` int DEFAULT 0 COMMENT '容量',
  `facilities` text COMMENT '设施描述',
  `images` text COMMENT '场地图片',
  `price_per_hour` decimal(10,2) NOT NULL COMMENT '每小时价格',
  `status` tinyint DEFAULT 1 COMMENT '状态: 0-维护, 1-开放',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
```

**预订表 (booking)**
```sql
CREATE TABLE `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预订ID',
  `booking_no` varchar(32) NOT NULL COMMENT '预订编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `venue_id` bigint NOT NULL COMMENT '场地ID',
  `booking_date` date NOT NULL COMMENT '预订日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  `actual_price` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `payment_method` tinyint COMMENT '支付方式',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0-待支付, 1-已支付, 2-已取消, 3-已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `booking_no` (`booking_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_venue_id` (`venue_id`)
);
```

### 开发规范

#### 代码规范

**Java代码规范**
- 使用驼峰命名法
- 类名首字母大写
- 方法名和变量名首字母小写
- 常量全大写，下划线分隔
- 注释使用中文，格式规范

**前端代码规范**
- 组件名使用PascalCase
- 文件名使用kebab-case
- 变量名使用camelCase
- CSS类名使用BEM规范
- 统一使用2空格缩进

#### Git提交规范

```bash
# 功能开发
git commit -m "feat: 添加用户登录功能"

# 问题修复
git commit -m "fix: 修复预订时间冲突问题"

# 文档更新
git commit -m "docs: 更新API文档"

# 样式调整
git commit -m "style: 调整登录页面样式"

# 重构代码
git commit -m "refactor: 重构用户服务层代码"

# 性能优化
git commit -m "perf: 优化场地查询性能"

# 测试相关
git commit -m "test: 添加用户注册单元测试"
```

### 常见问题

#### 启动问题

**Q: 后端启动失败，提示数据库连接错误**
A: 检查以下配置：
1. 确认MySQL服务已启动
2. 检查数据库连接配置是否正确
3. 确认数据库已创建并导入数据
4. 检查用户名密码是否正确

**Q: 前端启动失败，提示依赖安装错误**
A: 解决方案：
1. 删除node_modules文件夹
2. 清除npm缓存：`npm cache clean --force`
3. 重新安装依赖：`npm install`
4. 如果仍有问题，尝试使用yarn安装

#### 功能问题

**Q: 微信登录无法使用**
A: 检查配置：
1. 确认微信开放平台配置正确
2. 检查回调域名是否已配置
3. 确认AppID和AppSecret是否正确
4. 检查网络连接是否正常

**Q: 支付功能异常**
A: 排查步骤：
1. 检查支付配置是否正确
2. 确认商户号和密钥是否有效
3. 检查回调地址是否可访问
4. 查看支付日志排查具体错误

### 部署指南

#### 生产环境部署

**1. 服务器要求**
- CPU: 2核心以上
- 内存: 4GB以上
- 硬盘: 50GB以上
- 操作系统: CentOS 7+ / Ubuntu 18+

**2. 环境安装**
```bash
# 安装Java 17
sudo yum install java-17-openjdk

# 安装MySQL 8.0
sudo yum install mysql-server

# 安装Nginx
sudo yum install nginx

# 安装Node.js
curl -sL https://rpm.nodesource.com/setup_16.x | sudo bash -
sudo yum install nodejs
```

**3. 应用部署**
```bash
# 后端部署
cd basketball-system
mvn clean package -Dmaven.test.skip=true
java -jar target/basketball-system-1.0.0.jar

# 前端部署
cd basketball-web
npm install
npm run build
# 将dist目录内容复制到nginx网站目录
```

**4. Nginx配置**
```nginx
server {
    listen 80;
    server_name yourdomain.com;

    # 前端静态文件
    location / {
        root /var/www/basketball-web;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

#### Docker部署

**Dockerfile (后端)**
```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/basketball-system-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

**Dockerfile (前端)**
```dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

**docker-compose.yml**
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: basketball_system
    ports:
      - "3306:3306"
    volumes:
      - ./basketball_system.sql:/docker-entrypoint-initdb.d/init.sql

  backend:
    build: ./basketball-system
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/basketball_system

  frontend:
    build: ./basketball-web
    ports:
      - "80:80"
    depends_on:
      - backend
```

## 📝 更新日志

### v1.0.0 (2025-10-22)
- ✨ 完成基础用户管理功能
- ✨ 实现场地预订核心功能
- ✨ 集成微信支付和支付宝支付
- ✨ 添加会员系统和积分功能
- ✨ 完善管理后台功能
- ✨ 实现数据统计分析
- 🐛 修复已知问题和优化性能

### 计划功能
- 📱 移动端APP开发
- 🔔 消息推送功能增强
- 📊 更多数据分析维度
- 🎯 营销活动管理
- 🤖 智能推荐系统

## 🤝 贡献指南

欢迎提交Issue和Pull Request来帮助改进项目！

### 贡献流程
1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

### 开发环境搭建
1. 确保已安装所需环境
2. 克隆项目到本地
3. 按照快速开始指南配置环境
4. 运行测试确保环境正常

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- **项目维护者**: [Your Name]
- **邮箱**: your.email@example.com
- **项目地址**: https://github.com/your-username/basketball-system
- **问题反馈**: https://github.com/your-username/basketball-system/issues

## 🙏 致谢

感谢以下开源项目的支持：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis-Plus](https://baomidou.com/)
- [ECharts](https://echarts.apache.org/)

---

⭐ 如果这个项目对你有帮助，请给个Star支持一下！