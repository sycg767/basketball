# 篮球馆管理系统 - 前端

## 项目简介
基于 Vue3 + Vite + Element-Plus 的篮球馆管理系统前端项目

## 技术栈
- Vue 3.4
- Vite 5.0
- Vue Router 4.2
- Pinia 2.1
- Element-Plus 2.4
- Axios 1.6
- Sass

## 安装依赖
```bash
npm install
```

## 运行项目
```bash
npm run dev
```

访问：http://localhost:5173

## 打包项目
```bash
npm run build
```

## 项目结构
```
basketball-web/
├── src/
│   ├── api/              # API接口
│   ├── assets/           # 资源文件
│   ├── router/           # 路由
│   ├── store/            # 状态管理
│   ├── utils/            # 工具类
│   ├── views/            # 页面
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html
├── vite.config.js
└── package.json
```

## 功能模块
- ✅ 用户登录
- ✅ 用户注册
- ✅ 个人中心
- ✅ 密码修改

## 开发规范
请严格遵守 `前端开发规范.md` 中的规定

## 注意事项
1. 代码提交前请运行 `npm run build` 确保没有错误
2. 遵守命名规范和代码格式
3. 所有API接口统一在 `src/api` 目录下管理
4. 组件采用 Composition API（script setup）