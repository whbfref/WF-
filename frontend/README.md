# 公寓管理系统前端

本项目是基于Vue 3 + Vite + Element Plus开发的公寓管理系统前端部分。

## 技术栈

- Vue 3.x
- Vue Router
- Pinia (状态管理)
- Element Plus (UI组件库)
- Axios (HTTP请求)
- Vite (构建工具)
- Sass (CSS预处理器)

## 项目结构

```
frontend/
├── public/            # 静态资源
├── src/               # 源代码
│   ├── api/           # API请求
│   ├── assets/        # 资源文件(图片、样式等)
│   ├── components/    # 公共组件
│   ├── router/        # 路由配置
│   ├── stores/        # Pinia状态管理
│   ├── utils/         # 工具函数
│   ├── views/         # 页面视图
│   ├── App.vue        # 根组件
│   └── main.js        # 入口文件
├── index.html         # HTML模板
├── vite.config.js     # Vite配置
└── package.json       # 项目依赖
```

## 功能模块

- 用户认证(登录/注册/退出)
- 房源管理
- 租赁管理
- 账单管理
- 押金管理
- 用户管理
- 数据统计

## 开发指南

### 安装依赖

```bash
npm install
```

### 本地开发

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

## 与后端集成

前端通过Axios与Spring Boot后端进行通信，API前缀配置在`vite.config.js`中：

```js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080/api/v1',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, ''),
    },
  },
},
```

构建后的前端资源会自动输出到Spring Boot的静态资源目录中：

```js
build: {
  outDir: '../src/main/resources/static',
},
```

## 目前实现的页面

- 登录页面
- 注册页面
- 简单的仪表盘页面
- 404错误页面

## 注意事项

1. 确保后端API与前端请求路径一致
2. 首次运行时需要手动创建`src/assets/images`目录并添加必要的图片资源
3. 构建前端项目后需要重启Spring Boot服务以加载最新的静态资源 