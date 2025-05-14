# mysiteforme 权限管理系统

**mysiteforme** 是一个基于 Spring Boot 开发的轻量级系统脚手架，旨在帮助开发者快速搭建属于自己的系统后台。系统集成了用户管理、角色管理、权限管理、资源管理、数据库管理、代码生成等功能，适合作为中小型项目的基础框架。

## 项目特点
- **前后端分离**：server-admin为后端项目源码,server-ui为前端项目源码。
- **快速开发**：提供了自动生成前后台基本代码的功能，减少重复劳动。
- **轻量高效**：基于 Spring Boot，性能优越，部署简单。
- **功能全面**：内置用户、角色、权限等管理模块，支持扩展。
- **在线预览**：支持在线预览:https://admin.weizhengzs.cn/。 

## 技术栈
项目使用了以下主流技术框架：
- **后端**：
    - Spring Boot 3.2.2：快速构建独立运行的 Java 应用。
    - Spring Security 3.2.2：权限管理框架，支持认证和授权。
    - MyBatis Plus 3.5.10.1：简化 MyBatis 操作，提升开发效率。
    - Quartz：任务调度框架，用于定时任务管理。
    - Redis：高性能的缓存数据库。
    - Druid 1.2.20：数据库连接池，支持监控和 SQL 优化。
- **前端**：
    - [Art-design-pro](https://github.com/Daymychen/art-design-pro)：轻量级开源VUE3后台框架，基于 Vue3、TypeScript、Vite 和 Element-Plus 精心打造的后台管理系统模板。
- **其他**：
    - Maven：项目构建和依赖管理。

## 功能模块
- **系统管理**：
    - 用户管理：支持用户的增删改查及角色分配。
    - 角色管理：支持角色的创建、修改及分配权限。
    - 权限管理：基于 Spring-Security 实现的权限分配和验证。
- **日志管理**：
    - 操作日志：记录用户的关键操作。
    - 登录日志：记录用户登录信息。
- **数据库管理**：
    - 数据表管理：查看和管理数据库中的表结构。
    - 源码生成：根据数据库表自动生成前后端代码。
- **任务调度**：
    - 定时任务：支持任务的创建、修改和执行。
- **系统设置**：
    - 文件上传：支持本地和多种云存储。
    - 系统字典：管理系统中的配置项。
    - 网站配置：设置网站的基础信息。
  - **国际化**：
    - 现在前后端均支持一键国际化网站内容。

## 快速体验
按以下步骤即可快速启动项目：
### 后端启动
1. **环境准备**：
    - 安装 JDK 17 及以上版本。
    - 安装 MySQL 数据库。
    - 安装 Redis。
2. **导入数据库**：
    - 将 `resources/sql` 文件夹中的 `new_mysiteforme.sql` 文件导入到 MySQL 数据库中。
3. **修改配置文件**：
    - 根据实际环境修改 `application.yml` 中的数据库和 Redis 配置。
4. **运行项目**：
    - 使用 IDE 或命令行运行项目的主类 `com.mysiteforme.Application`。
### 前段启动
1. **环境准备**：
   - 安装 Node.js。
   - 安装 pnpm命令：`npm install -g pnpm`
2. **安装依赖**：
   - 使用 pnpm 安装依赖：`pnpm install`
   - 如果 pnpm install 安装失败，尝试使用下面的命令安装依赖：`pnpm install --ignore-scripts`
3. **启动项目**：
   - 本地启动：进入前段文件夹server-ui，执行命令：`pnpm dev`
   - 生产环境打包：`pnpm build`
### 用户登录
1. **访问地址**：http://localhost:3006/
2. **默认用户账号和密码**：`test`,`123456` (可进入个人中心自助修改密码)
3. **文件上传**：系统默认本地上传,可在线配置阿里云上传,七牛云上传。配置并测试成功后可在网站设置中进行无缝切换
4. **位置服务**：系统默认使用[太平洋网络IP查询定位IP地址](https://whois.pconline.com.cn/),也可以在网站设置中配置腾讯的[IP查询定位服务](https://lbs.qq.com/)
## 项目结构
```
├── docs/                 # 项目文档
├── server-admin/         # 后端项目
│   └─ src/                  # 源码
│       ├── main/             # 主程序
│       │   ├── java/         # Java 源码
│       │   ├── resources/    # 配置文件及静态资源
│       └── test/             # 测试代码
│       └── pom.xml           # Maven 配置文件
├── server-ui/            # 前端项目
│   └─ src/                   # 源码
└── README.md             # 项目说明文件
```

## 截图预览
### 系统登录
![](server-admin/src/main/resources/static/images/login.png)
### 系统菜单
![](server-admin/src/main/resources/static/images/menu_manager.png)
### 系统权限
![](server-admin/src/main/resources/static/images/permission_manager.png)
### 数据表
![](server-admin/src/main/resources/static/images/table.png)
### 权限分配
![](server-admin/src/main/resources/static/images/permission_assgin.png)

## 贡献指南
欢迎提交 Issue 或 Pull Request，为项目贡献代码或建议。

## 开源协议
本项目基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 协议开源，您可以自由使用、修改和分发。
