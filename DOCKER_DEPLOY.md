# server-ui Docker 部署指南

本文档介绍如何使用 Docker 和 Docker Compose 部署 server-ui 前端项目。

## 前提条件

- 安装 [Docker](https://docs.docker.com/get-docker/)
- 安装 [Docker Compose](https://docs.docker.com/compose/install/)

## 构建和运行

### 开发环境

```bash
# --env-file 指定了要加载的环境变量文件
# -f 指定了基础和覆盖的compose文件
# --build 会强制重新构建镜像
docker-compose -f docker-compose.yml -f docker-compose-dev.yml --env-file .env.dev up --build -d
```


### 生产环境：

```bash
# 确保 .env.prod 中的密码是安全的或通过其他方式注入
docker-compose -f docker-compose.yml -f docker-compose-prod.yml --env-file .env.prod up --build -d
```

### 停止和移除容器：

```bash
# 开发环境
docker-compose -f docker-compose.yml -f docker-compose-dev.yml --env-file .env.dev down
# 生产环境
docker-compose -f docker-compose.yml -f docker-compose-prod.yml --env-file .env.prod down
# 移除volume (小心！会删除数据)
# docker-compose ... down -v
```

## 配置说明

### 环境变量

如需添加环境变量，请在 `.env.dev` 或 `.env.prod` 文件中添加。

## 生产环境注意事项

1. 确保使用 HTTPS
2. 考虑使用 Docker Swarm 或 Kubernetes 进行容器编排
3. 实施适当的监控和日志解决方案
4. 设置适当的资源限制（内存、CPU）

## 问题排查

1. 如果页面无法访问，检查容器是否正常运行：
   ```bash
   docker ps
   ```

2. 检查 Nginx 配置是否正确：
   ```bash
   docker exec -it server-ui nginx -t
   ```

3. 查看容器日志：
   ```bash
   docker logs server-ui
   ``` 