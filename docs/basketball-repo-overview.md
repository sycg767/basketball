# 篮球馆管理系统（basketball）仓库扫描与项目总览

本文基于代码仓库的实际扫描结果，汇总后端/前端/数据层的技术栈、目录结构、业务模块、数据库、API、安全与鉴权、运行与开发、构建与部署等关键信息，便于快速上手与维护。

仓库根目录：
- 后端：basketball-system
- 前端：basketball-web
- 数据库脚本：basketball_system.sql

---

## 1. 技术栈与版本（以代码为准）

- 后端（Java 17 + Spring Boot 2.7）
  - Java: 17（见 [basketball-system/pom.xml](../basketball-system/pom.xml) properties）
  - Spring Boot: 2.7.18（parent）
  - MyBatis-Plus: 3.5.3.1
  - MySQL Connector: 8.0.33
  - JWT: io.jsonwebtoken jjwt 0.11.5
  - Springdoc OpenAPI: 1.7.0（Swagger UI）
  - Hutool: 5.8.25
  - 其他：Lombok、spring-security-crypto（BCrypt）、FastJSON 2.0.43
  - 支付 SDK：
    - 微信支付 V3: wechatpay-java 0.2.12
    - 支付宝: alipay-sdk-java 4.38.157.ALL

- 前端（Vue 3 + Vite 5 + Element Plus）
  - Vue: ^3.4.0
  - Vite: ^5.0.0
  - Element Plus: ^2.4.4
  - Pinia: ^2.1.7
  - Vue Router: ^4.2.5
  - Axios: ^1.6.2
  - ECharts: ^6.0.0
  - 详见 [basketball-web/package.json](../basketball-web/package.json)

- 数据库
  - MySQL 8（脚本导出信息显示 8.0.40）
  - 初始化脚本：仓库根目录 [basketball_system.sql](../basketball_system.sql)

---

## 2. 目录结构

- 后端（按 controller/service/mapper 分层，常见模块均已具备）
  - 入口与主包：
    - 启动类：[basketball-system/src/main/java/com/basketball/BasketballApplication.java](../basketball-system/src/main/java/com/basketball/BasketballApplication.java)
  - 分层与关键目录：
    - controller：业务接口层（用户、场馆、预订、课程、支付、通知、数据分析、后台等）
      - 示例：
        - 用户：[.../controller/UserController.java](../basketball-system/src/main/java/com/basketball/controller/UserController.java)
        - 预订：[.../controller/BookingController.java](../basketball-system/src/main/java/com/basketball/controller/BookingController.java)
        - 支付（统一）：[.../controller/PaymentController.java](../basketball-system/src/main/java/com/basketball/controller/PaymentController.java)
        - 支付（微信）：[.../controller/WechatPayController.java](../basketball-system/src/main/java/com/basketball/controller/WechatPayController.java)
        - 支付（支付宝）：[.../controller/AlipayController.java](../basketball-system/src/main/java/com/basketball/controller/AlipayController.java)
        - 数据分析（课程/场馆）：[.../controller/CourseAnalyticsController.java](../basketball-system/src/main/java/com/basketball/controller/CourseAnalyticsController.java) / [.../controller/VenueAnalyticsController.java](../basketball-system/src/main/java/com/basketball/controller/VenueAnalyticsController.java)
        - 后台：[/controller/admin](../basketball-system/src/main/java/com/basketball/controller/admin)
    - service / service/impl：业务服务实现
    - mapper：MyBatis-Plus 持久化接口
    - entity：数据库实体
    - dto：数据传输对象（request/response）
    - vo：视图对象
    - common：常量/统一返回/异常封装
      - 统一返回：[.../common/result/Result.java](../basketball-system/src/main/java/com/basketball/common/result/Result.java)
      - 全局异常：[.../common/exception/GlobalExceptionHandler.java](../basketball-system/src/main/java/com/basketball/common/exception/GlobalExceptionHandler.java)
    - config：框架与三方配置
      - Swagger：[.../config/SwaggerConfig.java](../basketball-system/src/main/java/com/basketball/config/SwaggerConfig.java)
      - MyBatis-Plus：[.../config/MyBatisPlusConfig.java](../basketball-system/src/main/java/com/basketball/config/MyBatisPlusConfig.java)
      - CORS：[.../config/CorsConfig.java](../basketball-system/src/main/java/com/basketball/config/CorsConfig.java)
      - WebMvc（拦截器/参数解析器注册）：[.../config/WebMvcConfig.java](../basketball-system/src/main/java/com/basketball/config/WebMvcConfig.java)
      - 微信/支付宝支付配置：[.../config/WechatPayConfig.java](../basketball-system/src/main/java/com/basketball/config/WechatPayConfig.java) / [.../config/AlipayConfig.java](../basketball-system/src/main/java/com/basketball/config/AlipayConfig.java)
      - Redis（预留，当前注释）：[.../config/RedisConfig.java](../basketball-system/src/main/java/com/basketball/config/RedisConfig.java)
    - security：JWT 工具与用户上下文
      - JWT 工具：[.../security/JwtTokenProvider.java](../basketball-system/src/main/java/com/basketball/security/JwtTokenProvider.java)
      - 用户上下文：[.../security/UserContext.java](../basketball-system/src/main/java/com/basketball/security/UserContext.java)
    - interceptor：拦截器（JWT 认证、短信限流）
      - JWT 拦截器：[.../interceptor/JwtAuthenticationInterceptor.java](../basketball-system/src/main/java/com/basketball/interceptor/JwtAuthenticationInterceptor.java)
      - 短信限流：[.../interceptor/SmsRateLimitInterceptor.java](../basketball-system/src/main/java/com/basketball/interceptor/SmsRateLimitInterceptor.java)
    - annotation / resolver：
      - 注解 @CurrentUserId：[.../annotation/CurrentUserId.java](../basketball-system/src/main/java/com/basketball/annotation/CurrentUserId.java)
      - 解析器：[.../resolver/CurrentUserIdArgumentResolver.java](../basketball-system/src/main/java/com/basketball/resolver/CurrentUserIdArgumentResolver.java)
    - scheduled / task：定时与计划任务
      - 课程热度定时任务：[.../scheduled/CoursePopularityScheduledTask.java](../basketball-system/src/main/java/com/basketball/scheduled/CoursePopularityScheduledTask.java)
      - 场馆使用率定时任务：[.../scheduled/VenueUsageScheduledTask.java](../basketball-system/src/main/java/com/basketball/scheduled/VenueUsageScheduledTask.java)
    - utils：工具类（Redis、短信、验证码等）
    - 资源配置：
      - 主配置：[.../resources/application.yml](../basketball-system/src/main/resources/application.yml)
      - 开发配置：[.../resources/application-dev.yml](../basketball-system/src/main/resources/application-dev.yml)

- 前端（api/router/store/utils/views/components 等）
  - 入口：
    - main：[/basketball-web/src/main.js](../basketball-web/src/main.js)
    - 根组件：[/basketball-web/src/App.vue](../basketball-web/src/App.vue)
  - 目录：
    - api：统一接口封装（请求拦截、鉴权、模块 API）
      - 请求封装：[/src/api/request.js](../basketball-web/src/api/request.js)
      - 业务 API：auth、user、booking、course、member、payment、notification、analytics、admin 等
    - router：路由与守卫 [/src/router/index.js](../basketball-web/src/router/index.js)
    - store：Pinia 模块（用户态等）[/src/store/modules/user.js](../basketball-web/src/store/modules/user.js)
    - utils：通用工具（鉴权、常量、日期、校验等）[/src/utils](../basketball-web/src/utils)
    - views：页面
      - 用户：登录、注册、个人中心等 [/src/views/user](../basketball-web/src/views/user)
      - 场馆：列表、详情 [/src/views/venue](../basketball-web/src/views/venue)
      - 预订：列表、详情、创建 [/src/views/booking](../basketball-web/src/views/booking)
      - 课程：列表、详情、我的课程 [/src/views/course](../basketball-web/src/views/course)
      - 会员：会员卡、购买、积分 [/src/views/member](../basketball-web/src/views/member)
      - 支付：方式选择、微信/支付宝、结果页 [/src/views/payment](../basketball-web/src/views/payment)
      - 通知：列表、详情 [/src/views/notification](../basketball-web/src/views/notification)
      - 管理后台：登录、仪表盘、用户/场馆/预订/课程/会员管理，数据分析 [/src/views/admin](../basketball-web/src/views/admin)
    - 构建配置：[/vite.config.js](../basketball-web/vite.config.js)（含 /api 代理）
    - 环境变量：[/.env.development](../basketball-web/.env.development)

---

## 3. 业务模块地图（入口与数据流）

- 用户（注册/登录/资料/密码/绑定手机号/第三方）
  - 后端入口：[/controller/UserController.java](../basketball-system/src/main/java/com/basketball/controller/UserController.java)
    - /api/auth/register、/api/auth/login、/api/auth/phone/login、/api/user/info 等
  - 前端入口：views/user + api/auth.js、api/user.js
  - 数据流：前端表单 -> Axios 携带 JWT -> 后端服务 IUserService -> mapper/entity -> DB；统一 Result 返回

- 场馆（查询/价格/排期/使用率分析）
  - 后端入口：VenueController、VenueAnalyticsController
  - 前端入口：views/venue、views/admin/VenueManage.vue、views/admin/analytics/VenueAnalysis.vue
  - 数据流：查询场馆基本信息、价格、时段；后台定时任务产出使用率分析

- 预订（创建/我的预订/取消/支付/可用性校验）
  - 后端入口：[/controller/BookingController.java](../basketball-system/src/main/java/com/basketball/controller/BookingController.java)
  - 前端入口：views/booking 与 api/booking.js、支付联动 payment.js
  - 数据流：创建预订 -> 生成订单 -> 支付（在线/余额/会员卡）-> 状态流转（已支付/取消/完成/退款）

- 课程（创建/报名/排期/热度分析/评价）
  - 后端入口：CourseController、CourseAnalyticsController、AdminCourseController
  - 前端入口：views/course、views/admin/CourseManage.vue、views/admin/analytics/CourseAnalysis.vue
  - 数据流：课程基础信息 -> 排期 -> 报名与签到 -> 热度分析（定时任务写入分析表）

- 会员卡与积分（类型、购买、充值、消费、积分成长/兑换）
  - 后端入口：MemberController、MemberActivityController、AdminMemberController
  - 前端入口：views/member、views/admin/MemberManage.vue
  - 数据流：会员卡账户与记录（member_card、member_card_record）、积分记录（points_record）

- 支付（微信/支付宝 SDK 及回调）
  - 统一入口：[/controller/PaymentController.java](../basketball-system/src/main/java/com/basketball/controller/PaymentController.java)
  - 分渠道：[/controller/WechatPayController.java](../basketball-system/src/main/java/com/basketball/controller/WechatPayController.java) / [/controller/AlipayController.java](../basketball-system/src/main/java/com/basketball/controller/AlipayController.java)
  - 回调：/api/payment/callback/wechat、/api/payment/callback/alipay 以及渠道专用 notify
  - 数据流：PaymentService -> payment_order/payment_config/payment_notify_log 等表

- 通知/短信
  - 后端入口：NotificationController、SmsController；短信限流拦截器 SmsRateLimitInterceptor
  - 前端入口：views/notification、部分登录/绑定流程中的验证码

- 数据分析（课程热度、场馆使用率、会员活跃度等）
  - 后端入口：CourseAnalyticsController、VenueAnalyticsController、ScheduledReminderService、相关 scheduled 任务
  - 前端入口：管理后台 analytics 子页面（ECharts）

- 后台管理
  - 后端入口：Admin*Controller（用户/场馆/预订/课程/会员等）
  - 前端入口：/views/admin（Layout、Dashboard、各管理页面）

---

## 4. 数据库（核心表与关键字段/关联）

初始化脚本：根目录 [basketball_system.sql](../basketball_system.sql)

- 用户与权限
  - user：id, username, password(BCrypt), phone, status, member_level, points, balance...
  - role / user_role / permission / role_permission：基础 RBAC
  - user_session：登录 session / token 记录
  - user_oauth：第三方绑定（wechat/qq/alipay 等）
  - user_behavior_log / user_profile_tag：行为与画像

- 场馆与预订
  - venue：基本信息、图片、状态等（外键被多表引用）
  - venue_price：按时段/周末/节假日价格配置
  - venue_schedule：时段占用与状态
  - booking：预订主表（user_id, venue_id, 日期/时间段、价格、状态）
  - booking_detail：预订细项（时段与价格）
  - booking_review：评价

- 课程与报名
  - course：课程主表（coach_id 外键 -> user.id）
  - course_schedule：课程排期（course_id, venue_id 外键）
  - course_enrollment：课程报名（关联 schedule/course/user）
  - course_popularity_analysis：课程热度分析（日维度）

- 会员与积分
  - member_card / member_card_type / member_card_record：会员卡、类型与交易记录
  - points_product / points_exchange / points_record：积分相关
  - member_activity_analysis：会员活跃度分析

- 支付
  - payment：通用支付记录（order_no、amount、pay_status...）
  - payment_order：统一支付订单（payment_no、business_no、business_type、status、跳转/二维码等）
  - payment_config：各渠道配置（pay_method 唯一、notify/return、沙箱/启用等）
  - payment_notify_log：回调日志
  - refund：退款记录

- 通知/短信/统计缓存
  - notification_*：通知模板、订阅、记录等
  - sms_code / sms_verification_code：短信验证码
  - statistics_cache：统计结果缓存

外键关系均在脚本内定义（booking -> user/venue、course* -> user/venue 等），并提供索引（如 idx_xxx）以支撑查询性能。

---

## 5. API 概览（Swagger/OpenAPI、分组、统一封装）

- 文档与入口
  - Springdoc OpenAPI 已启用（见 [SwaggerConfig](../basketball-system/src/main/java/com/basketball/config/SwaggerConfig.java) 与 [application-dev.yml](../basketball-system/src/main/resources/application-dev.yml)）。
  - 本地启动后访问：`http://localhost:8080/swagger-ui.html`（控制台启动日志也会打印地址）。

- 接口分组与示例
  - 用户：/api/auth/*、/api/user/*
  - 场馆：/api/venue/*、/api/analytics/venue/*
  - 预订：/api/booking/*（创建、列表、详情、取消、支付、可用性校验）
  - 课程：/api/course/*、/api/analytics/course/*
  - 支付：/api/payment/*（统一）以及 /api/payment/wechat/*、/api/payment/alipay/*（渠道）
  - 通知/短信：/api/notification/*、/api/sms/*
  - 后台：/api/admin/*（用户/场馆/预订/课程/会员等）

- 统一返回与异常
  - Result 结构：code, message, data, timestamp（见 [Result.java](../basketball-system/src/main/java/com/basketball/common/result/Result.java)）
  - 全局异常处理：参数校验、业务异常、系统异常（见 [GlobalExceptionHandler.java](../basketball-system/src/main/java/com/basketball/common/exception/GlobalExceptionHandler.java)）

---

## 6. 安全与鉴权（JWT + 拦截器 + 参数解析）

- JWT 工具：
  - [JwtTokenProvider](../basketball-system/src/main/java/com/basketball/security/JwtTokenProvider.java)：生成/校验 Token、解析用户ID（HS512，密钥/过期在 yml 配置）
  - 配置项（开发环境）：[application-dev.yml](../basketball-system/src/main/resources/application-dev.yml)

- 拦截器：
  - [JwtAuthenticationInterceptor](../basketball-system/src/main/java/com/basketball/interceptor/JwtAuthenticationInterceptor.java)：
    - preHandle 从 Authorization: Bearer xxx 读取 token -> 校验 -> 将用户ID写入 ThreadLocal
    - afterCompletion 清理 ThreadLocal，避免泄漏
    - WebMvcConfig 注册并放行 /api/auth/**、/swagger-ui/**、/v3/api-docs/** 等

- 用户上下文与参数注入：
  - ThreadLocal 用户上下文：[UserContext](../basketball-system/src/main/java/com/basketball/security/UserContext.java)
  - 自定义注解与解析器：[@CurrentUserId](../basketball-system/src/main/java/com/basketball/annotation/CurrentUserId.java) + [CurrentUserIdArgumentResolver](../basketball-system/src/main/java/com/basketball/resolver/CurrentUserIdArgumentResolver.java)
  - 使用方式：在 Controller 方法参数处标注 @CurrentUserId Long userId 即可获得当前登录用户ID（拦截器已提前解析写入）

---

## 7. 运行与开发（本地）

- 先决条件
  - JDK 17、Maven 3.8+、Node.js 18+（Vite 5 需较新 Node）、MySQL 8.x

- 初始化数据库（本地）
  1) 启动 MySQL 并创建数据库：basketball_system（如脚本中所示）
  2) 导入示例数据：
  ```bash
  # 在仓库根目录执行
  mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS basketball_system DEFAULT CHARSET utf8mb4;"
  mysql -uroot -p basketball_system < basketball_system.sql
  ```
  3) 如需修改数据库连接，编辑 [application-dev.yml](../basketball-system/src/main/resources/application-dev.yml) 的 spring.datasource.*

- 启动后端（basketball-system）
  ```bash
  cd basketball-system
  mvn clean spring-boot:run
  # 或打包运行
  mvn -DskipTests package
  java -jar target/basketball-system-1.0.0.jar
  ```
  访问 Swagger: http://localhost:8080/swagger-ui.html

- 启动前端（basketball-web）
  ```bash
  cd basketball-web
  npm install
  npm run dev
  # 默认端口 5173，Vite 代理已将 /api 转发至 http://localhost:8080
  ```
  - 开发环境变量：[/basketball-web/.env.development](../basketball-web/.env.development)
  - 构建：`npm run build` -> 产物输出至 [/basketball-web/dist](../basketball-web/dist)

- Redis（可选）
  - 当前 Redis 配置已预留但默认注释（见 [RedisConfig.java](../basketball-system/src/main/java/com/basketball/config/RedisConfig.java) 和 application-dev.yml 中的 redis 段）。

- 支付参数（必配项）
  - 微信/支付宝默认 enabled=false，避免缺参启动失败；请在生产/联调环境按需配置 [application.yml](../basketball-system/src/main/resources/application.yml) 对应项或使用外部化配置。

---

## 8. 构建与部署

- 后端
  - 构建：`mvn -DskipTests package`
  - 产物：target/basketball-system-1.0.0.jar
  - 静态资源：无（前端独立部署）

- 前端
  - 构建：`npm run build`
  - 产物：/basketball-web/dist（静态资源，可托管于任意静态服务器/Nginx）

- 容器化/CI
  - 仓库中未发现 Dockerfile / docker-compose、CI 配置（.github/workflows 等）。如需容器化或CI，请新增对应配置。

---

## 9. 待办 / 风险与差异说明

- 代码不一致/潜在问题
  - 拦截器与用户上下文方法名不一致：
    - JwtAuthenticationInterceptor 中调用 `UserContext.setUserId(...)`
    - 但 UserContext 实际方法为 `setCurrentUserId(...)`
    - 影响：编译/运行期可能报错或无法正确注入用户ID。建议统一方法名或调整调用处。
  - 支付配置默认未启用，且关键参数缺失（密钥/证书等）。生产前需配齐并开启 enabled=true。
  - MyBatis-Plus 在 application-dev.yml 中配置了 `mapper-locations: classpath:mapper/*.xml`，仓库未见对应 XML（当前均采用 MP 默认/Service 实现，通常不影响运行，但如新增 XML 映射需创建对应目录与文件）。

- 运行依赖与环境
  - 需本地 MySQL 8.x 并导入初始化脚本。
  - Node 版本建议 18+（Vite 5 兼容）。
  - Redis 使用为可选（限流/缓存等可迁移至 Redis；当前为内存实现，生产建议替换）。

- 文档与可观测性
  - Swagger/OpenAPI 已启用，建议在部署环境开启访问控制并补充接口说明。

---

## 10. 常用命令速查

- 后端
```bash
cd basketball-system
mvn clean spring-boot:run
mvn -DskipTests package && java -jar target/basketball-system-1.0.0.jar
```

- 前端
```bash
cd basketball-web
npm install
npm run dev
npm run build
```

- 数据库
```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS basketball_system DEFAULT CHARSET utf8mb4;"
mysql -uroot -p basketball_system < basketball_system.sql
```

---

如需扩展：
- 新增 Docker 化与 CI 工作流
- 引入 Redis（限流、短信、缓存、分布式锁）
- 完善支付联调参数与回调验签
- 丰富数据分析与监控指标
