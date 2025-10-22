# 🔧 数据分析功能快速修复指南

## 问题描述

数据分析页面显示空白，API返回空数组：
```json
{
  "code": 200,
  "data": [],  // 空数组
  "message": "success"
}
```

**原因**：数据库分析表为空，需要初始化数据。

---

## ✅ 解决方案（3种方法）

### 方法1：执行SQL脚本（推荐）⭐

**步骤**：
1. 打开MySQL客户端或命令行
2. 执行以下命令：

```bash
# Windows
mysql -u root -p basketball < basketball-system\src\main\resources\sql\init_analytics_data.sql

# Linux/Mac
mysql -u root -p basketball < basketball-system/src/main/resources/sql/init_analytics_data.sql
```

3. 输入数据库密码
4. 刷新前端页面

**优点**：一次性生成所有测试数据

---

### 方法2：使用API手动触发（最简单）⭐⭐⭐

**步骤**：
1. 双击运行 `test_analytics_api.bat`
2. 或手动执行以下命令：

```bash
# 会员活跃度分析
curl -X POST "http://localhost:8080/api/admin/analytics/member/analyze?analysisDate=2025-01-22"

# 课程热度分析
curl -X POST "http://localhost:8080/api/admin/analytics/course/analyze?analysisDate=2025-01-22"

# 场地使用分析
curl -X POST "http://localhost:8080/api/admin/analytics/venue/analyze?analysisDate=2025-01-22"
```

3. 等待几秒钟
4. 刷新前端页面

**优点**：无需数据库操作，直接通过API生成

---

### 方法3：等待定时任务（自动）

如果系统配置了定时任务，会在每天凌晨自动生成分析数据。

**优点**：无需手动操作

---

## 🧪 验证是否成功

### 1. 测试API返回数据

```bash
# 测试会员活跃度
curl "http://localhost:8080/api/admin/analytics/member/active-users?minScore=60"

# 应该返回类似：
{
  "code": 200,
  "data": [
    {
      "userId": 1,
      "username": "test",
      "activityScore": 85,
      "lastActiveTime": "2025-01-22 10:30:00"
    }
  ]
}
```

### 2. 访问前端页面

- 会员活跃度分析：`http://localhost:5173/admin/analytics/member`
- 课程热度分析：`http://localhost:5173/admin/analytics/course`
- 场地使用分析：`http://localhost:5173/admin/analytics/venue`

应该能看到图表和数据列表。

---

## ❌ 常见错误处理

### 错误1：`1054 - Unknown column 'course_count' in 'field list'`

**原因**：SQL脚本字段名错误

**解决**：已修复，使用最新的 `init_analytics_data.sql`

---

### 错误2：`Table 'basketball.member_activity_analysis' doesn't exist`

**原因**：数据库表未创建

**解决**：
```bash
mysql -u root -p basketball < basketball-system/src/main/resources/sql/member_activity_analysis.sql
```

---

### 错误3：API返回 `401 Unauthorized`

**原因**：未登录或Token过期

**解决**：
1. 先登录管理后台
2. 或在curl命令中添加正确的Token：
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" ...
```

---

## 📊 预期效果

执行修复后，应该看到：

### Dashboard页面
- ✅ 统计卡片显示数字（场地9个、用户12个、预订64个、收入¥8213.71）
- ✅ 预订趋势图表有折线
- ✅ 场地使用率饼图有数据
- ✅ 最近预订列表有记录

### 会员活跃度分析页面
- ✅ 活跃度趋势图表有数据
- ✅ 会员列表显示用户信息
- ✅ 活跃度分数显示

### 课程热度分析页面
- ✅ 课程热度排行榜有数据
- ✅ 热门课程列表显示

### 场地使用分析页面
- ✅ 场地使用率排行有数据
- ✅ 使用率图表显示

---

## 🚀 快速测试脚本

已创建测试脚本：`test_analytics_api.bat`

**使用方法**：
1. 双击运行
2. 查看输出结果
3. 如果看到数据，说明修复成功

---

## 📞 需要帮助？

如果以上方法都不行，请检查：
1. ✅ 后端服务是否正常运行（`http://localhost:8080`）
2. ✅ 数据库连接是否正常
3. ✅ 是否有用户、课程、场地等基础数据
4. ✅ 日志中是否有错误信息

---

**最后更新**：2025-01-22
**状态**：✅ SQL脚本已修复，可以正常使用
