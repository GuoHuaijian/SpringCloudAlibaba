# 多阶段构建
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build
COPY . .

# 构建（跳过测试，使用 Maven Wrapper）
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B clean package -DskipTests -pl sloth-boot-example/sloth-boot-example-service -am

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="sloth-boot <564559079@qq.com>"
LABEL description="Sloth Boot 示例服务"

WORKDIR /app

# 从构建阶段复制可执行 jar（repackage classifier=exec）
COPY --from=builder /build/sloth-boot-example/sloth-boot-example-service/target/*-exec.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar app.jar \"$@\""]
