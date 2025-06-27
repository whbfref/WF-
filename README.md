# 公寓管理系统开发文档

## 项目概述
本项目是一个基于SpringBoot、MongoDB 3.5.0和Pure Admin Vue前端框架开发的公寓管理系统。系统旨在为房东、租户和管理员提供一个全面的公寓管理平台，包括房产信息管理、租赁合同管理、押金管理、水电费管理等功能。

## 技术栈
- 后端：SpringBoot 2.7.x
- 数据库：MySQL (通过SQLyog管理)
- 前端：Pure Admin Vue (基于Vue3.x)
- 数据存储：MongoDB 3.5.0
- 构建工具：Maven
- 版本控制：Git

## 功能模块
1. [用户模块](./api/user_module.md)
2. [房东模块](./api/landlord_module.md)
3. [管理员模块](./api/admin_module.md)
4. [房产信息模块](./api/property_module.md)
5. [押金管理模块](./api/deposit_module.md)
6. [水电费管理模块](./api/utility_module.md)
7. [位置选择模块](./api/location_module.md)
8. [网站助手模块](./api/assistant_module.md)
9. [头像上传模块](./api/avatar_module.md)

## 数据库设计
- [数据库ER图](./database/er_diagram.md)
- [数据库表结构](./database/table_structure.md)

## API接口文档
- [API概述](./api/api_overview.md)
- [接口规范](./api/api_standard.md)
- [认证与授权](./api/auth.md)

## 前端设计
- [页面原型](./frontend/wireframes.md)
- [组件设计](./frontend/components.md)
- [路由设计](./frontend/routes.md)

## 后端设计
- [项目结构](./backend/project_structure.md)
- [业务逻辑](./backend/business_logic.md)
- [安全设计](./backend/security.md)

## 部署方案
- [开发环境部署](./backend/dev_deployment.md)
- [生产环境部署](./backend/prod_deployment.md)

## 项目计划
- [开发计划](./project_plan.md)
- [里程碑](./milestones.md)
  
## 用户界面
![图片1](https://github.com/user-attachments/assets/25b79ad9-7ef0-4567-8c9c-e1c8ad08e999)
<img width="415" alt="image" src="https://github.com/user-attachments/assets/9abd00e5-f0a8-4ed8-abf5-7090ec27b3fb" />
![图片3](https://github.com/user-attachments/assets/f79e9598-7082-46d8-87e2-b4fb6d196bf9)
<img width="416" alt="image" src="https://github.com/user-attachments/assets/6b8ac682-3f38-4302-a007-5b9308b91cee" />
<img width="406" alt="image" src="https://github.com/user-attachments/assets/ce3d5ff9-62df-4037-ad3a-70ec9bcad776" />
<img width="414" alt="image" src="https://github.com/user-attachments/assets/63587454-1593-44e8-808e-2e13abdd7a89" />
<img width="408" alt="image" src="https://github.com/user-attachments/assets/4bc2cdf0-89e6-4742-8f41-e3174bf969f0" />

## 房东界面
<img width="415" alt="image" src="https://github.com/user-attachments/assets/833d2808-f86e-45f8-ab1c-98406652ba57" />
<img width="415" alt="image" src="https://github.com/user-attachments/assets/e748c0c0-a6a7-4068-b251-fa867486d1d3" />

## 管理员界面
<img width="415" alt="image" src="https://github.com/user-attachments/assets/b09488ed-8409-47d5-819c-da4d1d3f1548" />
<img width="415" alt="image" src="https://github.com/user-attachments/assets/456f73fe-0811-46e0-bb2b-18269b55fc52" />
