# ---- 构建阶段 (Builder Stage) ----
# 使用包含 Maven 和 JDK 17 的镜像作为构建环境
FROM maven:3.8.5-openjdk-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和 .mvn (如果使用 Maven Wrapper)
# 先复制依赖描述文件，以便利用 Docker 的层缓存机制
# 如果 pom.xml 没有改变，这些层可以被重用，加快后续构建速度
COPY pom.xml .

# 解析依赖，注意：-Dos.detected.classifier=linux-x86_64 是为了兼容linux系统的 JAR 包
RUN mvn validate dependency:go-offline -Dos.detected.classifier=linux-x86_64

# 复制源代码
COPY src ./src

# 打包应用，跳过测试以加快构建速度
RUN mvn package -DskipTests

# 1. 选择一个基础镜像，这里选择一个包含 OpenJDK 17 的精简版镜像
FROM amazoncorretto:17-alpine

# 2. (可选) 作者信息
LABEL maintainer="1115784675@qq.com"

# 2.1 安装字体和 ttf-dejavu 字体（为了生成验证码）
RUN apk update && \
    apk add --no-cache fontconfig ttf-dejavu && \
    rm -rf /var/cache/apk/*

# 3. (可选) 设置工作目录，后续的 COPY, CMD, ENTRYPOINT 指令都会在这个目录下执行
WORKDIR /app

# 4. 将你本地打包好的 JAR 文件复制到镜像中的工作目录 /app 下
#    假设你的 JAR 文件在 target 目录下，名为 my-spring-boot-app-0.0.1-SNAPSHOT.jar
#    第一个参数是相对于 Dockerfile 所在目录的路径
#    第二个参数是镜像内的目标路径
#COPY server-admin/target/mysiteforme-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /app/target/*.jar app.jar

# 5. (可选) 声明应用监听的端口，这只是一个元数据声明，实际端口映射在 docker run 时指定
EXPOSE 8080

# 6. 容器启动时执行的命令
#    这里使用 exec 格式，推荐这种方式
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# 或者，如果你想传递 JVM 参数，可以这样：
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]