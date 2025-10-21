📋 完整执行计划
Phase 1: 设计系统基础 (2-3小时)
文件创建/修改清单：
创建设计系统目录结构
basketball-web/src/styles/design-system/
├── _variables.scss      # 所有设计变量
├── _colors.scss         # iOS色彩系统
├── _typography.scss     # SF Pro字体系统
├── _spacing.scss        # 8px网格间距
├── _shadows.scss        # 轻量级阴影
├── _animations.scss     # Apple标准动画
└── _mixins.scss         # 可复用混合宏
创建组件样式库
basketball-web/src/styles/components/
├── _buttons.scss        # Apple风格按钮
├── _cards.scss          # 优雅卡片样式
├── _forms.scss          # 表单组件
├── _tables.scss         # 表格样式
└── _modals.scss         # 弹窗样式
更新全局样式
src/assets/styles/index.scss - 引入新设计系统
src/App.vue - 更新全局基础样式
Phase 2: 首页重构 (3-4小时)
重构文件：
src/views/home/Index.vue - 首页主体
src/components/AnnouncementSection.vue - 公告组件
src/components/NotificationCenter.vue - 通知中心
重构要点：
毛玻璃导航栏（backdrop-filter）
简化Hero区域背景
优化快捷功能卡片
改进公告列表样式
Phase 3: 场地管理重构 (2-3小时)
重构文件：
src/views/venue/VenueList.vue - 场地列表
src/views/venue/VenueDetail.vue - 场地详情
src/components/venue/VenueCard.vue - 场地卡片
重构要点：
优化搜索筛选区
重设计场地卡片（类似App Store卡片）
改进图片展示和遮罩
优化按钮组
Phase 4: 课程系统重构 (2-3小时)
重构文件：
src/views/course/CourseList.vue - 课程列表
src/views/course/CourseDetail.vue - 课程详情
src/views/course/MyCourses.vue - 我的课程
重构要点：
重设计课程卡片
优化价格展示
改进标签和徽章
优化元信息布局
Phase 5: 会员系统重构 (2-3小时)
重构文件：
src/views/member/MemberCard.vue - 会员卡列表
src/views/member/MemberCardPurchase.vue - 购买会员卡
src/views/member/Points.vue - 积分页面
重构要点：
Apple Wallet风格会员卡
优雅的渐变和阴影
卡片翻转动画
改进充值对话框
Phase 6: 管理后台重构 (3-4小时)
重构文件：
src/views/admin/Layout.vue - 后台布局
src/views/admin/Dashboard.vue - 数据统计
src/views/admin/VenueManage.vue - 场地管理
src/views/admin/BookingManage.vue - 预订管理
src/views/admin/CourseManage.vue - 课程管理
src/views/admin/UserManage.vue - 用户管理
src/views/admin/MemberManage.vue - 会员管理
src/views/admin/announcement/AnnouncementManage.vue - 公告管理
重构要点：
浅色侧边栏
优化菜单样式
简化统计卡片
改进表格样式
优化图表展示
Phase 7: 表单和对话框重构 (2-3小时)
重构文件：
src/views/user/Login.vue - 登录页
src/views/user/Register.vue - 注册页
src/views/user/Profile.vue - 个人中心
src/views/booking/Create.vue - 创建预订
src/views/booking/List.vue - 预订列表
src/views/booking/Detail.vue - 预订详情
重构要点：
简化登录背景
优化表单输入框
改进按钮样式
优化对话框设计
Phase 8: 细节优化 (2-3小时)
优化内容：
微交互
按钮点击反馈（scale动画）
加载动画优化
过渡效果细化
添加骨架屏
响应式优化
移动端布局调整
平板适配优化
触摸交互改进
无障碍优化
对比度检查
焦点样式优化
键盘导航改进
性能优化
CSS优化
动画性能优化
减少重绘重排
📊 文件修改统计
类型	数量	说明
新建文件	13个	设计系统和组件样式
修改Vue文件	45个	所有页面和组件
修改样式文件	3个	全局样式
总计	61个文件	全面重构
🎯 核心改进点
视觉层面
✨ 统一使用iOS蓝色系（#007AFF）
🎨 简化色彩，增加中性灰
💎 优化圆角（6px-20px系统）
🌟 轻量级阴影系统
📐 8px网格间距系统
交互层面
⚡ Apple标准缓动函数
🎭 细腻的悬停效果
👆 优化的点击反馈
🔄 流畅的过渡动画
📱 改进的触摸交互
体验层面
🎯 清晰的视觉层次
📖 舒适的阅读体验
🧭 直观的导航系统
♿ 更好的无障碍支持
📱 完善的响应式设计
⚠️ 注意事项
保持功能完整性 - 所有现有功能保持不变
渐进增强 - 优先保证基础体验
浏览器兼容 - 支持现代浏览器（Chrome/Safari/Firefox）
性能优先 - 避免过度动画影响性能
可维护性 - 使用设计系统确保一致性
Claude’s Plan
🎨 篮球场馆预约系统 - Apple风格UI全面重构计划
执行方案
范围: 全部重构（8个阶段，61个文件）
风格: 完全Apple/iOS设计规范
色系: iOS蓝色系（#007AFF主色调）
方式: 一次性完成所有重构
优化: 可以优化交互逻辑和布局
重构阶段
Phase 1: 设计系统基础 (2-3h)
创建完整的设计系统（色彩、字体、间距、阴影、动画）
建立组件样式库（按钮、卡片、表单、表格、弹窗）
更新全局样式和App.vue
Phase 2: 首页重构 (3-4h)
毛玻璃导航栏
简化Hero区域
优化公告组件
改进快捷功能卡片
Phase 3: 场地管理 (2-3h)
重设计场地卡片（App Store风格）
优化搜索筛选
改进详情页
Phase 4: 课程系统 (2-3h)
重设计课程卡片
优化价格展示
改进列表布局
Phase 5: 会员系统 (2-3h)
Apple Wallet风格会员卡
优雅渐变和阴影
卡片动画效果
Phase 6: 管理后台 (3-4h)
浅色侧边栏
简化统计卡片
优化表格和图表
改进所有管理页面
Phase 7: 表单对话框 (2-3h)
简化登录注册页
优化表单样式
改进对话框设计
Phase 8: 细节优化 (2-3h)
微交互和动画
响应式优化
无障碍改进
性能优化
核心改进
✨ iOS蓝色系统一色彩
🎨 8px网格间距系统
💎 6px-20px圆角系统
🌟 轻量级阴影系统
⚡ Apple标准动画
📱 完善响应式设计
文件修改
新建: 13个设计系统文件
修改: 45个Vue组件
更新: 3个全局样式
总计: 61个文件
预计时间
18-26小时（完整重构）
User approved the plan
完美！计划已确认，现在开始执行Apple风格UI全面重构。让我先创建任务列表并开始实施：
Update Todos

Phase 1: 创建设计系统基础 - 色彩、字体、间距、阴影、动画

Phase 1: 创建组件样式库 - 按钮、卡片、表单、表格、弹窗

Phase 2: 首页重构 - 导航栏、Hero、公告、快捷功能

Phase 3: 场地管理重构 - 列表、卡片、详情页

Phase 4: 课程系统重构 - 列表、卡片、详情页

Phase 5: 会员系统重构 - Apple Wallet风格会员卡

Phase 6: 管理后台重构 - 布局、统计、所有管理页面

Phase 7: 表单对话框重构 - 登录、注册、表单、对话框

Phase 8: 细节优化 - 微交互、响应式、无障碍、性能