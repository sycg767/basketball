# 篮球场馆预约系统 - 前端UI规范文档

## 📋 目录
1. [布局规范](#布局规范)
2. [页面结构](#页面结构)
3. [组件使用规范](#组件使用规范)
4. [样式规范](#样式规范)
5. [响应式设计](#响应式设计)
6. [已修复页面清单](#已修复页面清单)

---

## 🎯 布局规范

### 页面容器
所有页面必须使用统一的容器类名和样式：

```vue
<template>
  <div class="page-container">
    <!-- 页面内容 -->
  </div>
</template>
```

**容器规范：**
- 最大宽度：`1200px`
- 居中对齐：`margin: 0 auto`
- 内边距：`padding: 20px`
- 移动端：`padding: 10px`

---

## 📄 页面结构

### 1. 详情页/表单页结构

使用 `el-page-header` 组件提供返回功能：

```vue
<template>
  <div class="page-container">
    <!-- 页面头部 - 带返回按钮 -->
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="page-title">页面标题</span>
      </template>
    </el-page-header>

    <!-- 页面内容 -->
    <div class="content-wrapper">
      <el-card class="info-card">
        <!-- 卡片内容 -->
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goBack = () => {
  router.back();
};
</script>
```

**适用页面：**
- 预订创建页 (`Create.vue`)
- 场地详情页 (`VenueDetail.vue`)
- 预订详情页 (`Detail.vue`)
- 支付方式选择页 (`PaymentMethod.vue`)
- 通知中心页 (`NotificationList.vue`)
- 通知详情页 (`NotificationDetail.vue`)

---

### 2. 列表页结构

使用自定义页面头部 + 面包屑导航：

```vue
<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>页面标题</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>模块名称</el-breadcrumb-item>
        <el-breadcrumb-item>当前页面</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 搜索筛选区 -->
    <el-card class="filter-card">
      <!-- 搜索表单 -->
    </el-card>

    <!-- 列表内容 -->
    <div class="list-content">
      <!-- 列表项 -->
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination />
    </div>
  </div>
</template>
```

**适用页面：**
- 场地列表页 (`VenueList.vue`)
- 课程列表页 (`CourseList.vue`)
- 预订列表页 (`List.vue`)

---

### 3. 特殊页面

**首页 (`Index.vue`)**
- 保持全屏布局
- 自定义导航栏
- 不使用统一容器

**登录/注册页**
- 居中卡片布局
- 不使用统一容器

---

## 🧩 组件使用规范

### 卡片组件 (el-card)

**统一间距：**
```scss
.el-card {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }
}
```

**卡片头部：**
```vue
<el-card>
  <template #header>
    <div class="card-header">
      <span>卡片标题</span>
      <div class="header-actions">
        <!-- 操作按钮 -->
      </div>
    </div>
  </template>
  <!-- 卡片内容 -->
</el-card>
```

---

### 操作按钮区域

```vue
<div class="action-buttons">
  <el-button @click="handleCancel">取消</el-button>
  <el-button type="primary" @click="handleConfirm">确认</el-button>
</div>
```

**样式规范：**
- 右对齐：`justify-content: flex-end`
- 按钮间距：`gap: 12px`
- 顶部间距：`margin-top: 20px`
- 顶部边框：`border-top: 1px solid #EBEEF5`

---

### 分页组件

```vue
<div class="pagination-container">
  <el-pagination
    v-model:current-page="currentPage"
    v-model:page-size="pageSize"
    :total="total"
    :page-sizes="[10, 20, 50, 100]"
    layout="total, sizes, prev, pager, next, jumper"
    @size-change="handleSizeChange"
    @current-change="handlePageChange"
  />
</div>
```

---

## 🎨 样式规范

### 颜色规范

**主色调：**
- 主色：`#409EFF` (Element Plus 默认蓝色)
- 成功：`#67C23A`
- 警告：`#E6A23C`
- 危险：`#F56C6C`
- 信息：`#909399`

**文本颜色：**
- 主要文本：`#303133`
- 常规文本：`#606266`
- 次要文本：`#909399`
- 占位文本：`#C0C4CC`

**背景颜色：**
- 页面背景：`#F5F7FA`
- 卡片背景：`#FFFFFF`
- 边框颜色：`#DCDFE6` / `#EBEEF5`

---

### 字体规范

**字号：**
- 页面标题：`24px`
- 卡片标题：`18px`
- 正文：`14px`
- 辅助文字：`13px`
- 小字：`12px`

**字重：**
- 标题：`font-weight: 500`
- 正文：`font-weight: 400`
- 强调：`font-weight: bold`

---

### 间距规范

**外边距：**
- 大间距：`32px`
- 标准间距：`20px`
- 小间距：`12px`
- 最小间距：`8px`

**内边距：**
- 页面容器：`20px`
- 卡片内容：`20px` (Element Plus 默认)
- 按钮内边距：`12px 20px`

---

## 📱 响应式设计

### 断点规范

```scss
// 移动端
@media (max-width: 768px) {
  .page-container {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;

    h2 {
      font-size: 20px;
    }
  }

  .action-buttons {
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }
}

// 平板
@media (min-width: 769px) and (max-width: 1024px) {
  .page-container {
    max-width: 960px;
  }
}

// 桌面
@media (min-width: 1025px) {
  .page-container {
    max-width: 1200px;
  }
}
```

---

## ✅ 已修复页面清单

### 已添加返回按钮的页面
- ✅ `PaymentMethod.vue` - 支付方式选择页
- ✅ `NotificationList.vue` - 通知中心页

### 已添加页面头部的页面
- ✅ `CourseList.vue` - 课程列表页

### 已统一布局的页面
- ✅ `PaymentMethod.vue` - 统一容器宽度和间距
- ✅ `NotificationList.vue` - 统一容器宽度和间距
- ✅ `CourseList.vue` - 添加面包屑导航

### 已有正确布局的页面
- ✅ `Create.vue` - 预订创建页
- ✅ `VenueDetail.vue` - 场地详情页
- ✅ `Detail.vue` - 预订详情页
- ✅ `VenueList.vue` - 场地列表页

---

## 📝 开发注意事项

### 1. 新建页面时
- 必须使用统一的页面容器类名
- 根据页面类型选择合适的头部结构
- 确保所有卡片使用统一间距

### 2. 修改现有页面时
- 检查是否符合UI规范
- 确保响应式布局正常
- 测试返回按钮功能

### 3. 样式编写时
- 优先使用全局样式 (`page-common.scss`)
- 避免重复定义相同样式
- 使用 SCSS 变量管理颜色和间距

---

## 🔧 全局样式文件

所有页面通用样式已统一到：
```
src/styles/page-common.scss
```

该文件已在 `src/assets/styles/index.scss` 中引入，无需在单个页面中重复引入。

---

## 📞 联系方式

如有UI规范相关问题，请联系前端开发团队。

**文档版本：** v1.0
**最后更新：** 2025-10-20
