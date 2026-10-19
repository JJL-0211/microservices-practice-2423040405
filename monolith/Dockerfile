# =========================================================================
# 第 05 周：多阶段构建 Dockerfile
#
# 阶段一 builder：使用 Maven + JDK 25 镜像编译打包，产出可运行 JAR。
# 阶段二 runtime：仅使用 JRE 25 镜像 + 上阶段产物，镜像更小、更安全。
# 因此本机无需安装 Maven / Java，构建完全在容器内完成。
# =========================================================================

# ---------- 阶段一：构建（Maven + JDK 25） ----------
FROM maven:3.9.16-eclipse-temurin-25 AS builder

WORKDIR /app

# 先只拷贝 pom.xml 并预下载依赖，利用 Docker 层缓存加速后续构建
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# 再拷贝源码并打包（含 H2 单元测试），产出 Spring Boot 可执行 JAR
COPY src ./src
RUN mvn -B -ntp clean package

# ---------- 阶段二：运行（JRE 25） ----------
FROM eclipse-temurin:25.0.4.1_1-jre

WORKDIR /app

# 仅从构建阶段拷贝最终的可运行 JAR
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
